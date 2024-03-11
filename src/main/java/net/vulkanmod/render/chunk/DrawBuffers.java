package net.vulkanmod.render.chunk;

import net.vulkanmod.Initializer;
import net.vulkanmod.render.PipelineManager;
import net.vulkanmod.render.chunk.build.UploadBuffer;
import net.vulkanmod.render.chunk.util.StaticQueue;
import net.vulkanmod.render.vertex.TerrainRenderType;
import net.vulkanmod.vulkan.Renderer;
import net.vulkanmod.vulkan.VRenderSystem;
import net.vulkanmod.vulkan.memory.IndirectBuffer;
import net.vulkanmod.vulkan.shader.Pipeline;
import org.joml.Matrix4f;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VkCommandBuffer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.LongBuffer;
import java.util.EnumMap;

import static net.vulkanmod.render.vertex.TerrainRenderType.getActiveLayers;
import static org.lwjgl.vulkan.VK10.*;

public class DrawBuffers {

    private static final int VERTEX_SIZE = PipelineManager.TERRAIN_VERTEX_FORMAT.getVertexSize();
    private static final int INDEX_SIZE = Short.BYTES;
    private final int index;
    private final Vector3i origin;
    private int updateIndex;
    private final short minHeight;

    private boolean allocated = false;
    AreaBuffer indexBuffer;
    static final EnumMap<TerrainRenderType, ArenaBuffer> indirectBuffers2 = new EnumMap<>(TerrainRenderType.class);
    private final EnumMap<TerrainRenderType, Integer> drawCnts = new EnumMap<>(TerrainRenderType.class);
    private final EnumMap<TerrainRenderType, AreaBuffer> areaBufferTypes = new EnumMap<>(TerrainRenderType.class);

    static {

        for (TerrainRenderType renderType : getActiveLayers()) {
            indirectBuffers2.put(renderType, new ArenaBuffer(VK_BUFFER_USAGE_INDIRECT_BUFFER_BIT, 4));
        }
    }

    //Need ugly minHeight Parameter to fix custom world heights (exceeding 384 Blocks in total)
    public DrawBuffers(int index, Vector3i origin, int minHeight) {

        this.index = index;
        this.origin = origin;
        this.minHeight = (short) minHeight;
    }

    public void allocateBuffers() {
        getActiveLayers().forEach(renderType -> this.drawCnts.put(renderType, 0));

        this.allocated = true;
    }

    public void upload(int xOffset, int yOffset, int zOffset, UploadBuffer buffer, DrawParameters drawParameters, TerrainRenderType renderType) {
        int vertexOffset = drawParameters.vertexOffset;
        int firstIndex = 0;
        drawParameters.baseInstance = encodeSectionOffset(xOffset, yOffset, zOffset);

        if(!buffer.indexOnly) {
            this.getAreaBufferCheckedAlloc(renderType).upload(buffer.getVertexBuffer(), drawParameters.vertexBufferSegment);
//            drawParameters.vertexOffset = drawParameters.vertexBufferSegment.getOffset() / VERTEX_SIZE;
            vertexOffset = drawParameters.vertexBufferSegment.getOffset() / VERTEX_SIZE;

            //debug
//            if(drawParameters.vertexBufferSegment.getOffset() % VERTEX_SIZE != 0) {
//                throw new RuntimeException("misaligned vertex buffer");
//            }
        }

        if(!buffer.autoIndices) {
            if (this.indexBuffer==null)
                this.indexBuffer = new AreaBuffer(VK_BUFFER_USAGE_INDEX_BUFFER_BIT, 786432 /*RenderType.SMALL_BUFFER_SIZE*/, INDEX_SIZE);
            this.indexBuffer.upload(buffer.getIndexBuffer(), drawParameters.indexBufferSegment);
//            drawParameters.firstIndex = drawParameters.indexBufferSegment.getOffset() / INDEX_SIZE;
            firstIndex = drawParameters.indexBufferSegment.getOffset() / INDEX_SIZE;
        }

//        AreaUploadManager.INSTANCE.enqueueParameterUpdate(
//                new ParametersUpdate(drawParameters, buffer.indexCount, firstIndex, vertexOffset));

        drawParameters.indexCount = buffer.indexCount;
        drawParameters.firstIndex = firstIndex;
        drawParameters.vertexOffset = vertexOffset;



        buffer.release();


    }
    //Exploit Pass by Reference to allow all keys to be the same AreaBufferObject (if perRenderTypeAreaBuffers is disabled)
    private AreaBuffer getAreaBufferCheckedAlloc(TerrainRenderType r) {
        return this.areaBufferTypes.computeIfAbsent(r, t -> new AreaBuffer(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT, r.initialSize, VERTEX_SIZE));
    }

    AreaBuffer getAreaBuffer(TerrainRenderType r) {
        return this.areaBufferTypes.get(r);
    }

    private boolean hasRenderType(TerrainRenderType r) {
        return this.areaBufferTypes.containsKey(r);
    }

    private int encodeSectionOffset(int xOffset, int yOffset, int zOffset) {
        final int xOffset1 = (xOffset & 127);
        final int zOffset1 = (zOffset & 127);
        final int yOffset1 = (yOffset-this.minHeight & 127);
        return yOffset1 << 16 | zOffset1 << 8 | xOffset1;
    }

    private void updateChunkAreaOrigin(VkCommandBuffer commandBuffer, Pipeline pipeline, double camX, double camY, double camZ, FloatBuffer mPtr) {
            float x = (float)(camX - (this.origin.x));
            float y = (float)(camY - (this.origin.y));
            float z = (float)(camZ - (this.origin.z));

            Matrix4f MVP = new Matrix4f().set(VRenderSystem.MVP.buffer.asFloatBuffer());
            Matrix4f MV = new Matrix4f().set(VRenderSystem.modelViewMatrix.buffer.asFloatBuffer());

            MVP.translate(-x, -y, -z).get(mPtr);
            MV.translate(-x, -y, -z).get(16,mPtr);

            vkCmdPushConstants(commandBuffer, pipeline.getLayout(), VK_SHADER_STAGE_VERTEX_BIT, 0, mPtr);
    }
    public void buildDrawBatchesIndirect(StaticQueue<DrawParameters> queue, TerrainRenderType terrainRenderType) {

        try (MemoryStack stack = MemoryStack.stackPush()) {
            if((updateIndex & terrainRenderType.bitMask()) !=0 || drawCnts.get(terrainRenderType)!=queue.size())
            {
                updateIndirectCmds(queue, terrainRenderType, stack);

                updateIndex ^= terrainRenderType.bitMask();

                drawCnts.put(terrainRenderType, queue.size());

            }

            ArenaBuffer arenaBuffer = indirectBuffers2.get(terrainRenderType);

            vkCmdDrawIndexedIndirect(Renderer.getCommandBuffer(), arenaBuffer.getId(), arenaBuffer.getBaseOffset(this.index), queue.size(), 20);
        }


    }

    private void updateIndirectCmds(StaticQueue<DrawParameters> queue, TerrainRenderType terrainRenderType, MemoryStack stack) {
        int size = queue.size() * 20;
        long bufferPtr = stack.nmalloc(size);


        int drawCount = 0;
        boolean isTranslucent = terrainRenderType == TerrainRenderType.TRANSLUCENT;
        for (var iterator = queue.iterator(isTranslucent); iterator.hasNext(); drawCount++) {

            DrawParameters drawParameters = iterator.next();


            long ptr = bufferPtr + (drawCount * 20L);
            MemoryUtil.memPutInt(ptr, drawParameters.indexCount);
            MemoryUtil.memPutInt(ptr + 4, drawParameters.instanceCount);
            MemoryUtil.memPutInt(ptr + 8, drawParameters.firstIndex);
            MemoryUtil.memPutInt(ptr + 12, drawParameters.vertexOffset);
            MemoryUtil.memPutInt(ptr + 16, drawParameters.baseInstance);


        }

        indirectBuffers2.get(terrainRenderType).uploadSubAlloc(bufferPtr, this.index, size);
    }

    public void buildDrawBatchesDirect(StaticQueue<DrawParameters> queue, TerrainRenderType renderType) {

        boolean isTranslucent = renderType == TerrainRenderType.TRANSLUCENT;

        VkCommandBuffer commandBuffer = Renderer.getCommandBuffer();

        for (var iterator = queue.iterator(isTranslucent); iterator.hasNext(); ) {
            final DrawParameters drawParameters = iterator.next();

            vkCmdDrawIndexed(commandBuffer, drawParameters.indexCount, drawParameters.instanceCount, drawParameters.firstIndex, drawParameters.vertexOffset, drawParameters.baseInstance);

        }
    }

    void bindBuffers(VkCommandBuffer commandBuffer, Pipeline pipeline, TerrainRenderType terrainRenderType, double camX, double camY, double camZ) {

        try(MemoryStack stack = MemoryStack.stackPush()) {
            nvkCmdBindVertexBuffers(commandBuffer, 0, 1, stack.npointer(getAreaBuffer(terrainRenderType).getId()), stack.npointer(0));
            updateChunkAreaOrigin(commandBuffer, pipeline, camX, camY, camZ, stack.mallocFloat(32));
        }

        if(terrainRenderType == TerrainRenderType.TRANSLUCENT) {
            vkCmdBindIndexBuffer(commandBuffer, this.indexBuffer.getId(), 0, VK_INDEX_TYPE_UINT16);
        }

    }

    public void releaseBuffers() {
        if(!this.allocated)
            return;

        this.areaBufferTypes.values().forEach(AreaBuffer::freeBuffer);

        this.areaBufferTypes.clear();
        if(this.indexBuffer != null)
            this.indexBuffer.freeBuffer();


        for (ArenaBuffer a : indirectBuffers2.values()) {
            a.rem(this.index);
        }

        drawCnts.replaceAll((t, v) -> 0);

        this.indexBuffer = null;
        this.allocated = false;
    }

    public boolean isAllocated() {
        return allocated;
    }
    //instanceCount was added to encourage memcpy optimisations _(JIT doesn't need to insert a 1, which generates better ASM + helps JIT)_
    public static class DrawParameters {
        int indexCount, instanceCount = 1, firstIndex, vertexOffset, baseInstance;
        final AreaBuffer.Segment vertexBufferSegment = new AreaBuffer.Segment();
        final AreaBuffer.Segment indexBufferSegment;

        DrawParameters(boolean translucent) {
            indexBufferSegment = translucent ? new AreaBuffer.Segment() : null;
        }

        public void reset(ChunkArea chunkArea, TerrainRenderType r) {
            this.indexCount = 0;
            this.firstIndex = 0;
            this.vertexOffset = 0;

            int segmentOffset = this.vertexBufferSegment.getOffset();
            if(chunkArea != null && chunkArea.drawBuffers().hasRenderType(r) && segmentOffset != -1) {
//                this.chunkArea.drawBuffers.vertexBuffer.setSegmentFree(segmentOffset);
                chunkArea.drawBuffers().getAreaBuffer(r).setSegmentFree(this.vertexBufferSegment);
            }
        }
    }

}
