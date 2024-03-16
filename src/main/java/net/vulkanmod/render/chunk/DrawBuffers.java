package net.vulkanmod.render.chunk;

<<<<<<< HEAD
=======
import net.vulkanmod.Initializer;
import net.vulkanmod.render.PipelineManager;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
import net.vulkanmod.render.chunk.build.UploadBuffer;
import net.vulkanmod.render.chunk.util.StaticQueue;
import net.vulkanmod.render.vertex.TerrainRenderType;
import net.vulkanmod.vulkan.Renderer;
import net.vulkanmod.vulkan.VRenderSystem;
<<<<<<< HEAD
=======
import net.vulkanmod.vulkan.memory.IndirectBuffer;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
import net.vulkanmod.vulkan.shader.Pipeline;
import org.joml.Matrix4f;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VkCommandBuffer;

<<<<<<< HEAD
import java.nio.FloatBuffer;
import java.util.EnumMap;

import static net.vulkanmod.render.vertex.TerrainRenderType.*;
=======
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.EnumMap;

>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
import static org.lwjgl.vulkan.VK10.*;

public class DrawBuffers {

<<<<<<< HEAD
    private static final int VERTEX_SIZE = 18;
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

        for (TerrainRenderType renderType : ALL_RENDER_TYPES) {
            indirectBuffers2.put(renderType, new ArenaBuffer(VK_BUFFER_USAGE_INDIRECT_BUFFER_BIT, 4));
        }
    }

=======
    private static final int VERTEX_SIZE = PipelineManager.TERRAIN_VERTEX_FORMAT.getVertexSize();
    private static final int INDEX_SIZE = Short.BYTES;
    private final int index;
    private final Vector3i origin;
    private final int minHeight;

    private boolean allocated = false;
    AreaBuffer vertexBuffer, indexBuffer;
    private final EnumMap<TerrainRenderType, AreaBuffer> areaBufferTypes = new EnumMap<>(TerrainRenderType.class);

>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
    //Need ugly minHeight Parameter to fix custom world heights (exceeding 384 Blocks in total)
    public DrawBuffers(int index, Vector3i origin, int minHeight) {

        this.index = index;
        this.origin = origin;
<<<<<<< HEAD
        this.minHeight = (short) minHeight;
    }

    public void allocateBuffers() {
        ALL_RENDER_TYPES.forEach(renderType -> this.drawCnts.put(renderType, 0));
=======
        this.minHeight = minHeight;
    }

    public void allocateBuffers() {
        if(!Initializer.CONFIG.perRenderTypeAreaBuffers)
            vertexBuffer = new AreaBuffer(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT, 2097152 /*RenderType.BIG_BUFFER_SIZE>>1*/, VERTEX_SIZE);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

        this.allocated = true;
    }

<<<<<<< HEAD
    public void upload(int xOffset, int yOffset, int zOffset, UploadBuffer buffer, DrawParameters drawParameters, TerrainRenderType renderType, int idx) {
=======
    public void upload(int xOffset, int yOffset, int zOffset, UploadBuffer buffer, DrawParameters drawParameters, TerrainRenderType renderType) {
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
        int vertexOffset = drawParameters.vertexOffset;
        int firstIndex = 0;
        drawParameters.baseInstance = encodeSectionOffset(xOffset, yOffset, zOffset);

        if(!buffer.indexOnly) {
<<<<<<< HEAD
            this.getAreaBufferOrAlloc(renderType).upload(buffer.getVertexBuffer(), drawParameters.vertexBufferSegment, idx);
=======
            this.getAreaBufferOrAlloc(renderType).upload(buffer.getVertexBuffer(), drawParameters.vertexBufferSegment);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
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
<<<<<<< HEAD
            this.indexBuffer.upload(buffer.getIndexBuffer(), drawParameters.indexBufferSegment, idx);
=======
            this.indexBuffer.upload(buffer.getIndexBuffer(), drawParameters.indexBufferSegment);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
//            drawParameters.firstIndex = drawParameters.indexBufferSegment.getOffset() / INDEX_SIZE;
            firstIndex = drawParameters.indexBufferSegment.getOffset() / INDEX_SIZE;
        }

        drawParameters.indexCount = buffer.indexCount;
        drawParameters.firstIndex = firstIndex;
        drawParameters.vertexOffset = vertexOffset;

<<<<<<< HEAD
        updateIndex |= renderType.bitMask();

=======
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
        buffer.release();
    }

    //Exploit Pass by Reference to allow all keys to be the same AreaBufferObject (if perRenderTypeAreaBuffers is disabled)
<<<<<<< HEAD

    private AreaBuffer getAreaBufferOrAlloc(TerrainRenderType r) {
        return this.areaBufferTypes.computeIfAbsent(
                r, t -> new AreaBuffer(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT, r.initialSize, VERTEX_SIZE));
=======
    private AreaBuffer getAreaBufferOrAlloc(TerrainRenderType r) {
        return this.areaBufferTypes.computeIfAbsent(
                r, t -> Initializer.CONFIG.perRenderTypeAreaBuffers ? new AreaBuffer(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT, r.initialSize, VERTEX_SIZE) : this.vertexBuffer);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
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

<<<<<<< HEAD
            Matrix4f MVP = new Matrix4f().set(VRenderSystem.MVP.buffer().asFloatBuffer());
            Matrix4f MV = new Matrix4f().set(VRenderSystem.modelViewMatrix.buffer().asFloatBuffer());
=======
            Matrix4f MVP = new Matrix4f().set(VRenderSystem.MVP.buffer.asFloatBuffer());
            Matrix4f MV = new Matrix4f().set(VRenderSystem.modelViewMatrix.buffer.asFloatBuffer());
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

            MVP.translate(-x, -y, -z).get(mPtr);
            MV.translate(-x, -y, -z).get(16,mPtr);

            vkCmdPushConstants(commandBuffer, pipeline.getLayout(), VK_SHADER_STAGE_VERTEX_BIT, 0, mPtr);
    }
<<<<<<< HEAD
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
=======
    public void buildDrawBatchesIndirect(IndirectBuffer indirectBuffer, StaticQueue<RenderSection> queue, TerrainRenderType terrainRenderType) {

        try (MemoryStack stack = MemoryStack.stackPush()) {

            ByteBuffer byteBuffer = stack.malloc(20 * queue.size());
            long bufferPtr = MemoryUtil.memAddress0(byteBuffer);

            boolean isTranslucent = terrainRenderType == TerrainRenderType.TRANSLUCENT;

            int drawCount = 0;
            for (var iterator = queue.iterator(isTranslucent); iterator.hasNext(); ) {

                final RenderSection section = iterator.next();
                final DrawParameters drawParameters = section.getDrawParameters(terrainRenderType);

//                //TODO
//                if (!drawParameters.ready && drawParameters.vertexBufferSegment.getOffset() != -1) {
//                    if (!drawParameters.vertexBufferSegment.isReady())
//                        continue;
//                    drawParameters.ready = true;
//                }

                long ptr = bufferPtr + (drawCount * 20L);
                MemoryUtil.memPutInt(ptr, drawParameters.indexCount);
                MemoryUtil.memPutInt(ptr + 4, 1);
                MemoryUtil.memPutInt(ptr + 8, drawParameters.firstIndex);
                MemoryUtil.memPutInt(ptr + 12, drawParameters.vertexOffset);
                MemoryUtil.memPutInt(ptr + 16, drawParameters.baseInstance);

                drawCount++;
            }

            if (drawCount == 0) return;

            indirectBuffer.recordCopyCmd(byteBuffer.position(0));


            vkCmdDrawIndexedIndirect(Renderer.getCommandBuffer(), indirectBuffer.getId(), indirectBuffer.getOffset(), drawCount, 20);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
        }


    }

<<<<<<< HEAD
    private void updateIndirectCmds(StaticQueue<DrawParameters> queue, TerrainRenderType terrainRenderType, MemoryStack stack) {
        int size = queue.size() * 20;
        long bufferPtr = stack.nmalloc(size);


        int drawCount = 0;
        boolean isTranslucent = terrainRenderType == TRANSLUCENT;
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
=======
    public void buildDrawBatchesDirect(StaticQueue<RenderSection> queue, TerrainRenderType renderType) {
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

        boolean isTranslucent = renderType == TerrainRenderType.TRANSLUCENT;

        VkCommandBuffer commandBuffer = Renderer.getCommandBuffer();

        for (var iterator = queue.iterator(isTranslucent); iterator.hasNext(); ) {
<<<<<<< HEAD
            final DrawParameters drawParameters = iterator.next();

            vkCmdDrawIndexed(commandBuffer, drawParameters.indexCount, drawParameters.instanceCount, drawParameters.firstIndex, drawParameters.vertexOffset, drawParameters.baseInstance);
=======
            final RenderSection section = iterator.next();
            final DrawParameters drawParameters = section.getDrawParameters(renderType);

            vkCmdDrawIndexed(commandBuffer, drawParameters.indexCount, 1, drawParameters.firstIndex, drawParameters.vertexOffset, drawParameters.baseInstance);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

        }
    }

    void bindBuffers(VkCommandBuffer commandBuffer, Pipeline pipeline, TerrainRenderType terrainRenderType, double camX, double camY, double camZ) {

        try(MemoryStack stack = MemoryStack.stackPush()) {
            nvkCmdBindVertexBuffers(commandBuffer, 0, 1, stack.npointer(getAreaBuffer(terrainRenderType).getId()), stack.npointer(0));
            updateChunkAreaOrigin(commandBuffer, pipeline, camX, camY, camZ, stack.mallocFloat(32));
        }

<<<<<<< HEAD
        if(terrainRenderType == TRANSLUCENT) {
=======
        if(terrainRenderType == TerrainRenderType.TRANSLUCENT) {
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
            vkCmdBindIndexBuffer(commandBuffer, this.indexBuffer.getId(), 0, VK_INDEX_TYPE_UINT16);
        }

    }

    public void releaseBuffers() {
        if(!this.allocated)
            return;

<<<<<<< HEAD
        this.areaBufferTypes.values().forEach(AreaBuffer::freeBuffer);
=======
        if(this.vertexBuffer == null) {
            this.areaBufferTypes.values().forEach(AreaBuffer::freeBuffer);
        }
        else
            this.vertexBuffer.freeBuffer();
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

        this.areaBufferTypes.clear();
        if(this.indexBuffer != null)
            this.indexBuffer.freeBuffer();

<<<<<<< HEAD

        for (ArenaBuffer a : indirectBuffers2.values()) {
            a.rem(this.index);
        }

        drawCnts.replaceAll((t, v) -> 0);

=======
        this.vertexBuffer = null;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
        this.indexBuffer = null;
        this.allocated = false;
    }

    public boolean isAllocated() {
        return allocated;
    }
<<<<<<< HEAD
    //instanceCount was added to encourage memcpy optimisations _(JIT doesn't need to insert a 1, which generates better ASM + helps JIT)_
    public static class DrawParameters {
        int indexCount, instanceCount = 1, firstIndex, vertexOffset, baseInstance;
        final AreaBuffer.Segment vertexBufferSegment = new AreaBuffer.Segment();
        final AreaBuffer.Segment indexBufferSegment;
=======

    public static class DrawParameters {
        int indexCount, firstIndex, vertexOffset, baseInstance;
        final AreaBuffer.Segment vertexBufferSegment = new AreaBuffer.Segment();
        final AreaBuffer.Segment indexBufferSegment;
        boolean ready = false;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

        DrawParameters(boolean translucent) {
            indexBufferSegment = translucent ? new AreaBuffer.Segment() : null;
        }

<<<<<<< HEAD
        public void reset(ChunkArea chunkArea, TerrainRenderType r, int index) {
=======
        public void reset(ChunkArea chunkArea, TerrainRenderType r) {
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
            this.indexCount = 0;
            this.firstIndex = 0;
            this.vertexOffset = 0;

            int segmentOffset = this.vertexBufferSegment.getOffset();
<<<<<<< HEAD
            if(chunkArea != null && chunkArea.drawBuffers().hasRenderType(r) && segmentOffset != -1) {
//                this.chunkArea.drawBuffers.vertexBuffer.setSegmentFree(segmentOffset);
                chunkArea.drawBuffers().getAreaBuffer(r).setSegmentFree(index);
=======
            if(chunkArea != null && chunkArea.drawBuffers.hasRenderType(r) && segmentOffset != -1) {
//                this.chunkArea.drawBuffers.vertexBuffer.setSegmentFree(segmentOffset);
                chunkArea.drawBuffers.getAreaBuffer(r).setSegmentFree(this.vertexBufferSegment);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
            }
        }
    }

<<<<<<< HEAD
=======
    public record ParametersUpdate(DrawParameters drawParameters, int indexCount, int firstIndex, int vertexOffset) {

        public void setDrawParameters() {
            this.drawParameters.indexCount = indexCount;
            this.drawParameters.firstIndex = firstIndex;
            this.drawParameters.vertexOffset = vertexOffset;
            this.drawParameters.ready = true;
        }
    }

>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
}
