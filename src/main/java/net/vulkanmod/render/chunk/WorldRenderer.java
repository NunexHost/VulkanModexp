package net.vulkanmod.render.chunk;

import com.google.common.collect.Lists;
<<<<<<< HEAD
=======
import com.google.common.collect.Sets;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.vulkanmod.Initializer;
<<<<<<< HEAD
import net.vulkanmod.config.Options;
=======
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
import net.vulkanmod.interfaces.FrustumMixed;
import net.vulkanmod.render.PipelineManager;
import net.vulkanmod.render.chunk.build.ChunkTask;
import net.vulkanmod.render.chunk.build.TaskDispatcher;
import net.vulkanmod.render.chunk.util.AreaSetQueue;
import net.vulkanmod.render.chunk.util.ResettableQueue;
import net.vulkanmod.render.chunk.util.Util;
import net.vulkanmod.render.profiling.BuildTimeBench;
import net.vulkanmod.render.profiling.Profiler;
import net.vulkanmod.render.profiling.Profiler2;
import net.vulkanmod.render.vertex.TerrainRenderType;
import net.vulkanmod.vulkan.Renderer;
import net.vulkanmod.vulkan.VRenderSystem;
<<<<<<< HEAD
import net.vulkanmod.vulkan.queue.Queue;
import net.vulkanmod.vulkan.shader.GraphicsPipeline;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkCommandBuffer;
=======
import net.vulkanmod.vulkan.memory.Buffer;
import net.vulkanmod.vulkan.memory.IndirectBuffer;
import net.vulkanmod.vulkan.memory.MemoryTypes;
import net.vulkanmod.vulkan.shader.GraphicsPipeline;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

import javax.annotation.Nullable;
import java.util.*;

<<<<<<< HEAD
import static net.vulkanmod.render.vertex.TerrainRenderType.*;

=======
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
public class WorldRenderer {
    private static WorldRenderer INSTANCE;

    private final Minecraft minecraft;

    private ClientLevel level;
<<<<<<< HEAD
    private int lastViewDistance;
=======
    private int renderDistance;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
    private final RenderBuffers renderBuffers;

    private Vec3 cameraPos;
    private int lastCameraSectionX;
    private int lastCameraSectionY;
    private int lastCameraSectionZ;
    private float lastCameraX;
    private float lastCameraY;
    private float lastCameraZ;
    private float lastCamRotX;
    private float lastCamRotY;

    private SectionGrid sectionGrid;

    private boolean needsUpdate;
<<<<<<< HEAD
=======
    private final Set<BlockEntity> globalBlockEntities = Sets.newHashSet();
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

    private final TaskDispatcher taskDispatcher;
    private final ResettableQueue<RenderSection> chunkQueue = new ResettableQueue<>();
    private AreaSetQueue chunkAreaQueue;
    private short lastFrame = 0;

    private double xTransparentOld;
    private double yTransparentOld;
    private double zTransparentOld;

    private VFrustum frustum;

<<<<<<< HEAD
//    IndirectBuffer[] indirectBuffers;
=======
    IndirectBuffer[] indirectBuffers;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
//    UniformBuffers uniformBuffers;

    public RenderRegionCache renderRegionCache;
    int nonEmptyChunks;

    private final List<Runnable> onAllChangedCallbacks = new ObjectArrayList<>();

    private WorldRenderer(RenderBuffers renderBuffers) {
        this.minecraft = Minecraft.getInstance();
        this.renderBuffers = renderBuffers;
        this.taskDispatcher = new TaskDispatcher();
        ChunkTask.setTaskDispatcher(this.taskDispatcher);
        allocateIndirectBuffers();

        Renderer.getInstance().addOnResizeCallback(() -> {
<<<<<<< HEAD
            for (Map.Entry<TerrainRenderType, ArenaBuffer> entry : DrawBuffers.indirectBuffers2.entrySet()) {
                entry.getValue().SubmitAll();
            }
        });


        addOnAllChangedCallback(Queue::trimCmdPools);
        addOnAllChangedCallback(this::reset);
        addOnAllChangedCallback(this::trimChunkQueue);
    }

    private void allocateIndirectBuffers() {
//        if(this.indirectBuffers != null)
//            Arrays.stream(this.indirectBuffers).forEach(Buffer::freeBuffer);
//
//        this.indirectBuffers = new IndirectBuffer[Renderer.getFramesNum()];
//
//        for(int i = 0; i < this.indirectBuffers.length; ++i) {
//            this.indirectBuffers[i] = new IndirectBuffer(1000000, MemoryTypes.HOST_MEM);
////            this.indirectBuffers[i] = new IndirectBuffer(1000000, MemoryType.GPU_MEM);
//        }

//        uniformBuffers = new UniformBuffers(100000, MemoryType.GPU_MEM);
=======
            if(this.indirectBuffers.length != Renderer.getFramesNum())
                allocateIndirectBuffers();
        });
    }

    private void allocateIndirectBuffers() {
        if(this.indirectBuffers != null)
            Arrays.stream(this.indirectBuffers).forEach(Buffer::freeBuffer);

        this.indirectBuffers = new IndirectBuffer[Renderer.getFramesNum()];

        for(int i = 0; i < this.indirectBuffers.length; ++i) {
            this.indirectBuffers[i] = new IndirectBuffer(1000000, MemoryTypes.HOST_MEM);
//            this.indirectBuffers[i] = new IndirectBuffer(1000000, MemoryTypes.GPU_MEM);
        }

//        uniformBuffers = new UniformBuffers(100000, MemoryTypes.GPU_MEM);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
    }

    public static WorldRenderer init(RenderBuffers renderBuffers) {
        if(INSTANCE != null)
            return INSTANCE;
        else
            return INSTANCE = new WorldRenderer(renderBuffers);
    }

    public static WorldRenderer getInstance() {
        return INSTANCE;
    }

    public static ClientLevel getLevel() {
        return INSTANCE.level;
    }

    public static Vec3 getCameraPos() {
        return INSTANCE.cameraPos;
    }

    private void benchCallback() {
        BuildTimeBench.runBench(this.needsUpdate, this.taskDispatcher);
    }

    public void setupRenderer(Camera camera, Frustum frustum, boolean isCapturedFrustum, boolean spectator) {
<<<<<<< HEAD
//        Profiler p = Profiler.getProfiler("chunks");
//        p.start();
=======
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
        Profiler2 profiler = Profiler2.getMainProfiler();
        profiler.push("Setup_Renderer");

        benchCallback();

<<<<<<< HEAD
//        this.frustum = frustum.offsetToFullyIncludeCameraCube(8);
        this.cameraPos = camera.getPosition();
        if (this.minecraft.options.getEffectiveRenderDistance() != this.lastViewDistance) {
            this.allChanged();
        }
=======
        this.cameraPos = camera.getPosition();
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

        this.level.getProfiler().push("camera");
        float cameraX = (float)cameraPos.x();
        float cameraY = (float)cameraPos.y();
        float cameraZ = (float)cameraPos.z();
        int sectionX = SectionPos.posToSectionCoord(cameraX);
        int sectionY = SectionPos.posToSectionCoord(cameraY);
        int sectionZ = SectionPos.posToSectionCoord(cameraZ);

        Profiler p2 = Profiler.getProfiler("camera");
        profiler.push("reposition");

        if (this.lastCameraSectionX != sectionX || this.lastCameraSectionY != sectionY || this.lastCameraSectionZ != sectionZ) {

            p2.start();
            this.lastCameraSectionX = sectionX;
            this.lastCameraSectionY = sectionY;
            this.lastCameraSectionZ = sectionZ;
            this.sectionGrid.repositionCamera(cameraX, cameraZ);
            p2.pushMilestone("end-reposition");
            p2.round();
        }
        profiler.pop();

        double entityDistanceScaling = this.minecraft.options.entityDistanceScaling().get();
        Entity.setViewScale(Mth.clamp((double)this.minecraft.options.getEffectiveRenderDistance() / 8.0D, 1.0D, 2.5D) * entityDistanceScaling);

//        this.chunkRenderDispatcher.setCamera(cameraPos);
        this.level.getProfiler().popPush("cull");
        this.minecraft.getProfiler().popPush("culling");
        BlockPos blockpos = camera.getBlockPosition();

        this.minecraft.getProfiler().popPush("update");

        boolean flag = this.minecraft.smartCull;
        if (spectator && this.level.getBlockState(blockpos).isSolidRender(this.level, blockpos)) {
            flag = false;
        }

        float d_xRot = Math.abs(camera.getXRot() - this.lastCamRotX);
        float d_yRot = Math.abs(camera.getYRot() - this.lastCamRotY);
        this.needsUpdate |= d_xRot > 2.0f || d_yRot > 2.0f;

<<<<<<< HEAD
        this.needsUpdate |= Initializer.CONFIG.BFSMode ? Math.abs(cameraY - this.lastCameraY) > 2.0f : cameraX != this.lastCameraX || cameraY != this.lastCameraY || cameraZ != this.lastCameraZ; //May have very minor issues, but reduces FPS drops alot when moving
=======
        this.needsUpdate |= cameraX != this.lastCameraX || cameraY != this.lastCameraY || cameraZ != this.lastCameraZ;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

        if (!isCapturedFrustum) {

            //Debug
//            this.needsUpdate = true;
//            this.needsUpdate = false;

            if (this.needsUpdate) {
<<<<<<< HEAD


                this.frustum = (((FrustumMixed)(frustum)).customFrustum()).offsetToFullyIncludeCameraCube(8);
                this.sectionGrid.updateFrustumVisibility(this.frustum);
=======
                this.needsUpdate = false;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
                this.lastCameraX = cameraX;
                this.lastCameraY = cameraY;
                this.lastCameraZ = cameraZ;
                this.lastCamRotX = camera.getXRot();
                this.lastCamRotY = camera.getYRot();

<<<<<<< HEAD
//                p2.pushMilestone("frustum");
=======
                this.frustum = (((FrustumMixed)(frustum)).customFrustum()).offsetToFullyIncludeCameraCube(8);
                this.sectionGrid.updateFrustumVisibility(this.frustum);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

                this.minecraft.getProfiler().push("partial_update");

                this.chunkQueue.clear();
                this.initUpdate();
                this.initializeQueueForFullUpdate(camera);

                this.renderRegionCache = new RenderRegionCache();

<<<<<<< HEAD
                if(flag) this.updateRenderChunks();
                this.needsUpdate = false;
=======
                if(flag)
                    this.updateRenderChunks();
                else
                    this.updateRenderChunksSpectator();

>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
                this.minecraft.getProfiler().pop();

            }

//            p.pushMilestone("update");
//            p.round();
        }

<<<<<<< HEAD
//        this.indirectBuffers[Renderer.getCurrentFrame()].reset();
=======
        this.indirectBuffers[Renderer.getCurrentFrame()].reset();
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
//        this.uniformBuffers.reset();

        this.minecraft.getProfiler().pop();
        profiler.pop();
    }

    private void initializeQueueForFullUpdate(Camera camera) {
        int i = 16;
        Vec3 vec3 = camera.getPosition();
        BlockPos blockpos = camera.getBlockPosition();
        RenderSection renderSection = this.sectionGrid.getSectionAtBlockPos(blockpos);

        if (renderSection == null) {
            boolean flag = blockpos.getY() > this.level.getMinBuildHeight();
            int j = flag ? this.level.getMaxBuildHeight() - 8 : this.level.getMinBuildHeight() + 8;
            int k = Mth.floor(vec3.x / 16.0D) * 16;
            int l = Mth.floor(vec3.z / 16.0D) * 16;

            List<RenderSection> list = Lists.newArrayList();

<<<<<<< HEAD
            for(int i1 = -this.lastViewDistance; i1 <= this.lastViewDistance; ++i1) {
                for(int j1 = -this.lastViewDistance; j1 <= this.lastViewDistance; ++j1) {
=======
            for(int i1 = -this.renderDistance; i1 <= this.renderDistance; ++i1) {
                for(int j1 = -this.renderDistance; j1 <= this.renderDistance; ++j1) {
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

                    RenderSection renderSection1 = this.sectionGrid.getSectionAtBlockPos(new BlockPos(k + SectionPos.sectionToBlockCoord(i1, 8), j, l + SectionPos.sectionToBlockCoord(j1, 8)));
                    if (renderSection1 != null) {
                        renderSection1.setGraphInfo(null, (byte) 0);
                        renderSection1.setLastFrame(this.lastFrame);
                        list.add(renderSection1);

                    }
                }
            }

            //Probably not needed
//            list.sort(Comparator.comparingDouble((p_194358_) -> {
//                return blockpos.distSqr(p_194358_.chunk.getOrigin().offset(8, 8, 8));
//            }));

            for (RenderSection chunkInfo : list) {
                this.chunkQueue.add(chunkInfo);
            }

        } else {
            renderSection.setGraphInfo(null, (byte) 0);
            renderSection.setLastFrame(this.lastFrame);
            this.chunkQueue.add(renderSection);
        }

    }

<<<<<<< HEAD
    private void trimChunkQueue() {

        final int i = Math.max(1024, (this.lastViewDistance * this.lastViewDistance << 4));
        if(this.chunkQueue.capacity() > i)
        {
            this.chunkQueue.trim(i);
        }
    }

=======
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
    private void initUpdate() {
        this.resetUpdateQueues();

        this.lastFrame++;
        this.nonEmptyChunks = 0;
    }

    private void resetUpdateQueues() {
        this.chunkAreaQueue.clear();
        this.sectionGrid.chunkAreaManager.resetQueues();
    }

    private void updateRenderChunks() {
        int maxDirectionsChanges = Initializer.CONFIG.advCulling;
<<<<<<< HEAD
        if(!needsUpdate) return;
//        if(taskDispatcher.getIdleThreadsCount() == 0)
//            /*return;*/this.needsUpdate = true;
        //TODO maybe decouple Segments from DrawIndirectCmds: execute ChunkTask then asign a specific section/ via Morton codes, indexes or some other decoupled assignment/indexing implementation.
        // Concerned with the culling. not the drawCallIndirectCommand Contents; at least for Basic Frustum Culling tbh, (i.e. this isn't occlusion Culling, and/or Lods e.g.)_ teselation eg..
=======

        int buildLimit = taskDispatcher.getIdleThreadsCount() * (Minecraft.getInstance().options.enableVsync().get() ? 6 : 3);

        if(buildLimit == 0)
            this.needsUpdate = true;

>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
        while(this.chunkQueue.hasNext()) {
            RenderSection renderSection = this.chunkQueue.poll();


            if(!renderSection.isCompletelyEmpty()) {
<<<<<<< HEAD
                renderSection.getChunkArea().addSections(renderSection);
=======
                renderSection.getChunkArea().sectionQueue.add(renderSection);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
                this.chunkAreaQueue.add(renderSection.getChunkArea());
                this.nonEmptyChunks++;
            }

            this.scheduleUpdate(renderSection);

            if(renderSection.directionChanges > maxDirectionsChanges)
                continue;

            for(Direction direction : Util.DIRECTIONS) {
                RenderSection relativeChunk = renderSection.getNeighbour(direction);

                if (relativeChunk != null && !renderSection.hasDirection(direction.getOpposite())) {

                    if (renderSection.hasMainDirection()) {
                        if (!renderSection.visibilityBetween(renderSection.mainDir.getOpposite(), direction))
                            continue;
                    }

                    this.addNode(renderSection, relativeChunk, direction);
                }
            }
<<<<<<< HEAD
            //Push/Insert into updateIndexQueue
        }



        //BFS has atcually finished: no new updates will occut

        //Progresive Sca shouldbe OK as BFS updates from tot botton i


        //TODO: move uploads into BFS Queue/Thread instead of Main render thread
        taskDispatcher.uploadAllPendingUploads();
        needsUpdate=false;
=======
        }

    }

    private void updateRenderChunksSpectator() {
        int maxDirectionsChanges = Initializer.CONFIG.advCulling;

        int rebuildLimit = taskDispatcher.getIdleThreadsCount();

        if(rebuildLimit == 0)
            this.needsUpdate = true;

        while(this.chunkQueue.hasNext()) {
            RenderSection renderSection = this.chunkQueue.poll();


            if(!renderSection.isCompletelyEmpty()) {
                renderSection.getChunkArea().sectionQueue.add(renderSection);
                this.chunkAreaQueue.add(renderSection.getChunkArea());
                this.nonEmptyChunks++;
            }

            this.scheduleUpdate(renderSection);

            for(Direction direction : Util.DIRECTIONS) {
                RenderSection relativeChunk = renderSection.getNeighbour(direction);

                if (relativeChunk != null && !renderSection.hasDirection(direction.getOpposite())) {

                    this.addNode(renderSection, relativeChunk, direction);

                }
            }
        }
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

    }

    private void addNode(RenderSection renderSection, RenderSection relativeChunk, Direction direction) {
<<<<<<< HEAD
        final byte b = relativeChunk.getChunkArea().inFrustum(relativeChunk.frustumIndex);
        if (b >= FrustumIntersection.PLANE_NX) {
            return;
        }
        if (relativeChunk.getLastFrame() == this.lastFrame) {
            int d = renderSection.mainDir != direction? renderSection.directionChanges + 1 : renderSection.directionChanges;
=======
        if (relativeChunk.getChunkArea().inFrustum(relativeChunk.frustumIndex) >= 0) {
            return;
        }
        else if (relativeChunk.getLastFrame() == this.lastFrame) {
            int d = renderSection.mainDir != direction && !renderSection.isCompletelyEmpty() ?
                    renderSection.directionChanges + 1 : renderSection.directionChanges;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

            relativeChunk.addDir(direction);

            relativeChunk.directionChanges = d < relativeChunk.directionChanges ? (byte) d : relativeChunk.directionChanges;

            return;
        }
<<<<<<< HEAD
        if(b == FrustumIntersection.INTERSECT) {
=======
        else if(relativeChunk.getChunkArea().inFrustum(relativeChunk.frustumIndex) == FrustumIntersection.INTERSECT) {
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
            if(frustum.cubeInFrustum(relativeChunk.xOffset, relativeChunk.yOffset, relativeChunk.zOffset,
                    relativeChunk.xOffset + 16 , relativeChunk.yOffset + 16, relativeChunk.zOffset + 16) >= 0)
                return;
        }

        relativeChunk.setLastFrame(this.lastFrame);

        relativeChunk.setGraphInfo(direction, (byte) (renderSection.step + 1));
        relativeChunk.setDirections(renderSection.directions, direction);
        this.chunkQueue.add(relativeChunk);

        byte d;
        if ((renderSection.sourceDirs & (1 << direction.ordinal())) == 0 && !renderSection.isCompletelyEmpty())
        {
            d = renderSection.step > 4 ? (byte) (renderSection.directionChanges + 1) : 0;
        }
        else
            d = renderSection.directionChanges;

        relativeChunk.directionChanges = d;
    }

    public void scheduleUpdate(RenderSection section) {
        if(!section.isDirty())
            return;

        section.rebuildChunkAsync(this.taskDispatcher, this.renderRegionCache);
        section.setNotDirty();
    }

    public void compileSections(Camera camera) {
        this.minecraft.getProfiler().push("populate_chunks_to_compile");
//        RenderRegionCache renderregioncache = new RenderRegionCache();
//        BlockPos cameraPos = camera.getBlockPosition();
//        List<RenderSection> list = Lists.newArrayList();

        this.minecraft.getProfiler().popPush("upload");

        Profiler2 profiler = Profiler2.getMainProfiler();
        profiler.push("Uploads");
<<<<<<< HEAD

        //TOOD: DrawCmdArray listener/Update pump pergapes...

        if(this.taskDispatcher.hasUploads())
            this.needsUpdate = true;
        this.chunkAreaQueue.add(getSectionGrid().chunkAreaManager.getChunkArea(1));
=======
        if(this.taskDispatcher.uploadAllPendingUploads())
            this.needsUpdate = true;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
        profiler.pop();
        this.minecraft.getProfiler().popPush("schedule_async_compile");

//        //debug
//        Profiler p = null;
//        if(!list.isEmpty()) {
//            p = Profiler.getProfiler("compileChunks");
//            p.start();
//        }


//        for(RenderSection renderSection : list) {
//            renderSection.rebuildChunkAsync(this.taskDispatcher, renderregioncache);
////            renderSection.rebuildChunkSync(this.taskDispatcher, renderregioncache);
//            renderSection.setNotDirty();
//        }

//        if(!list.isEmpty()) {
//            p.round();
//        }
        this.minecraft.getProfiler().pop();
    }

    public boolean isSectionCompiled(BlockPos blockPos) {
        RenderSection renderSection = this.sectionGrid.getSectionAtBlockPos(blockPos);
        return renderSection != null && renderSection.isCompiled();
    }

    public void allChanged() {
        if (this.level != null) {
//            this.graphicsChanged();
            this.level.clearTintCaches();

            this.taskDispatcher.createThreads();

            this.needsUpdate = true;
//            this.generateClouds = true;

<<<<<<< HEAD
            this.lastViewDistance = this.minecraft.options.getEffectiveRenderDistance();
=======
            this.renderDistance = this.minecraft.options.getEffectiveRenderDistance();
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
            if (this.sectionGrid != null) {
                this.sectionGrid.releaseAllBuffers();
            }

            this.taskDispatcher.clearBatchQueue();
<<<<<<< HEAD

            this.sectionGrid = new SectionGrid(this.level, this.minecraft.options.getEffectiveRenderDistance());
=======
            synchronized(this.globalBlockEntities) {
                this.globalBlockEntities.clear();
            }

            this.sectionGrid = new SectionGrid(this.level, this.renderDistance);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
            this.chunkAreaQueue = new AreaSetQueue(this.sectionGrid.chunkAreaManager.size);

            this.onAllChangedCallbacks.forEach(Runnable::run);

            Entity entity = this.minecraft.getCameraEntity();
            if (entity != null) {
                this.sectionGrid.repositionCamera(entity.getX(), entity.getZ());
            }

        }
    }

    public void setLevel(@Nullable ClientLevel level) {
<<<<<<< HEAD
        this.lastCameraY = Float.MIN_VALUE;
=======
        this.lastCameraX = Float.MIN_VALUE;
        this.lastCameraY = Float.MIN_VALUE;
        this.lastCameraZ = Float.MIN_VALUE;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
        this.lastCameraSectionX = Integer.MIN_VALUE;
        this.lastCameraSectionY = Integer.MIN_VALUE;
        this.lastCameraSectionZ = Integer.MIN_VALUE;
//        this.entityRenderDispatcher.setLevel(level);
        this.level = level;
        if (level != null) {
            this.allChanged();
        }  else {
            if (this.sectionGrid != null) {
                this.sectionGrid.releaseAllBuffers();
                this.sectionGrid = null;
            }

            this.taskDispatcher.stopThreads();

            this.needsUpdate = true;
        }

    }

    public void addOnAllChangedCallback(Runnable runnable) {
        this.onAllChangedCallbacks.add(runnable);
    }

    public void clearOnAllChangedCallbacks() {
        this.onAllChangedCallbacks.clear();
    }

    public void renderSectionLayer(RenderType renderType, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projection) {
<<<<<<< HEAD
        //debug
//        Profiler p = Profiler.getProfiler("chunks");
        Profiler2 p = Profiler2.getMainProfiler();
        final TerrainRenderType terrainRenderType = get(renderType.name);

//        p.pushMilestone("layer " + layerName);
        if(terrainRenderType.equals(SOLID))
            p.push("Opaque_terrain_pass");
        else if(terrainRenderType.equals(TRANSLUCENT))
        {
            p.pop();
            p.push("Translucent_terrain_pass");
        }


        RenderSystem.assertOnRenderThread();
=======
        TerrainRenderType terrainRenderType = TerrainRenderType.get(renderType);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
        renderType.setupRenderState();

        this.sortTranslucentSections(camX, camY, camZ);

        this.minecraft.getProfiler().push("filterempty");
        this.minecraft.getProfiler().popPush(() -> "render_" + renderType);

<<<<<<< HEAD
        final boolean isFancy = Options.getGraphicsState();
        final boolean isTranslucent = terrainRenderType == TRANSLUCENT;
        final boolean indirectDraw = Initializer.CONFIG.drawIndirect;

        VRenderSystem.applyMVP(poseStack.last().pose(), projection);

        final VkCommandBuffer commandBuffer = Renderer.getCommandBuffer();


        p.push("draw batches");

        final int currentFrame = Renderer.getCurrentFrame();
        if((ALL_RENDER_TYPES).contains(terrainRenderType)) {

            if(!isFancy) VRenderSystem.depthFunc(GL11C.GL_LESS); //Fast Grass
            VRenderSystem.depthMask(!isTranslucent); //Disable Depth writes if Translucent

            GraphicsPipeline terrainShader = PipelineManager.getTerrainShader(terrainRenderType);
            boolean shouldUpdate = (Renderer.getInstance().bindGraphicsPipeline(terrainShader));
            {
                Renderer.getDrawer().bindAutoIndexBuffer(commandBuffer, 7);
                terrainShader.bindDescriptorSets(commandBuffer, currentFrame, shouldUpdate);
            }

            for(Iterator<ChunkArea> iterator = this.chunkAreaQueue.iterator(isTranslucent); iterator.hasNext();) {
                ChunkArea chunkArea = iterator.next();
                var queue = chunkArea.sectionQueue().get(terrainRenderType);
                DrawBuffers drawBuffers = chunkArea.getDrawBuffers();


                if(drawBuffers.getAreaBuffer(terrainRenderType) != null && queue.size() != 0) {
                    drawBuffers.bindBuffers(commandBuffer, terrainShader, terrainRenderType, camX, camY, camZ);
                    if (indirectDraw) drawBuffers.buildDrawBatchesIndirect(queue, terrainRenderType);
                    else drawBuffers.buildDrawBatchesDirect(queue, terrainRenderType);
                }

            }
            if(indirectDraw) DrawBuffers.indirectBuffers2.get(terrainRenderType).SubmitAll();
//            uniformBuffers.submitUploads();
        }


        p.pop();


=======
        final boolean isTranslucent = terrainRenderType == TerrainRenderType.TRANSLUCENT;
        final boolean indirectDraw = Initializer.CONFIG.indirectDraw;

        VRenderSystem.applyMVP(poseStack.last().pose(), projection);

        Renderer renderer = Renderer.getInstance();
        GraphicsPipeline pipeline = PipelineManager.getTerrainShader(terrainRenderType);
        renderer.bindGraphicsPipeline(pipeline);
        Renderer.getDrawer().bindAutoIndexBuffer(Renderer.getCommandBuffer(), 7);

        int currentFrame = Renderer.getCurrentFrame();
        Set<TerrainRenderType> allowedRenderTypes = Initializer.CONFIG.uniqueOpaqueLayer ? TerrainRenderType.COMPACT_RENDER_TYPES : TerrainRenderType.SEMI_COMPACT_RENDER_TYPES;
        if(allowedRenderTypes.contains(terrainRenderType)) {
            terrainRenderType.setCutoutUniform();

            for(Iterator<ChunkArea> iterator = this.chunkAreaQueue.iterator(isTranslucent); iterator.hasNext();) {
                ChunkArea chunkArea = iterator.next();
                var queue = chunkArea.sectionQueue;
                DrawBuffers drawBuffers = chunkArea.drawBuffers;

                renderer.uploadAndBindUBOs(pipeline);
                if(drawBuffers.getAreaBuffer(terrainRenderType) != null && queue.size() > 0) {

                    drawBuffers.bindBuffers(Renderer.getCommandBuffer(), pipeline, terrainRenderType, camX, camY, camZ);
                    renderer.uploadAndBindUBOs(pipeline);

                    if (indirectDraw)
                        drawBuffers.buildDrawBatchesIndirect(indirectBuffers[currentFrame], queue, terrainRenderType);
                    else
                        drawBuffers.buildDrawBatchesDirect(queue, terrainRenderType);
                }
            }
        }

        if(terrainRenderType == TerrainRenderType.CUTOUT || terrainRenderType == TerrainRenderType.TRIPWIRE) {
            indirectBuffers[currentFrame].submitUploads();
//            uniformBuffers.submitUploads();
        }

        //Need to reset push constants in case the pipeline will still be used for rendering
        if(!indirectDraw) {
            VRenderSystem.setChunkOffset(0, 0, 0);
            renderer.pushConstants(pipeline);
        }
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

        this.minecraft.getProfiler().pop();
        renderType.clearRenderState();

        VRenderSystem.applyMVP(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix());
<<<<<<< HEAD

        switch (terrainRenderType) {
            case CUTOUT -> {
                p.pop();
//                p.pop();
//                p.push("Render_level_2");
                p.push("entities");
            }
//            case "translucent" -> p.pop();
            case TRIPWIRE -> p.pop();
        }

=======
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
    }

    private void sortTranslucentSections(double camX, double camY, double camZ) {
        this.minecraft.getProfiler().push("translucent_sort");
        double d0 = camX - this.xTransparentOld;
        double d1 = camY - this.yTransparentOld;
        double d2 = camZ - this.zTransparentOld;
//        if (d0 * d0 + d1 * d1 + d2 * d2 > 1.0D) {
        if (d0 * d0 + d1 * d1 + d2 * d2 > 2.0D) {
            this.xTransparentOld = camX;
            this.yTransparentOld = camY;
            this.zTransparentOld = camZ;
            int j = 0;

//                for(QueueChunkInfo chunkInfo : this.sectionsInFrustum) {
//                    if (j < 15 && chunkInfo.chunk.resortTransparency(renderType, this.taskDispatcher)) {
//                        ++j;
//                    }
//                }

            Iterator<RenderSection> iterator = this.chunkQueue.iterator(false);

            while(iterator.hasNext() && j < 15) {
                RenderSection section = iterator.next();

                section.resortTransparency(this.taskDispatcher);

                ++j;
            }
        }

        this.minecraft.getProfiler().pop();
    }

    public void renderBlockEntities(PoseStack poseStack, double camX, double camY, double camZ,
                                    Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress, float gameTime) {
        Profiler2 profiler = Profiler2.getMainProfiler();
        profiler.pop();
        profiler.push("block-entities");

        MultiBufferSource bufferSource = this.renderBuffers.bufferSource();

        for(RenderSection renderSection : this.chunkQueue) {
            List<BlockEntity> list = renderSection.getCompiledSection().getRenderableBlockEntities();
            if (!list.isEmpty()) {
                for(BlockEntity blockEntity : list) {
                    BlockPos blockPos = blockEntity.getBlockPos();
                    MultiBufferSource bufferSource1 = bufferSource;
                    poseStack.pushPose();
                    poseStack.translate((double)blockPos.getX() - camX, (double)blockPos.getY() - camY, (double)blockPos.getZ() - camZ);
                    SortedSet<BlockDestructionProgress> sortedset = destructionProgress.get(blockPos.asLong());
                    if (sortedset != null && !sortedset.isEmpty()) {
                        int j1 = sortedset.last().getProgress();
                        if (j1 >= 0) {
                            PoseStack.Pose pose = poseStack.last();
                            VertexConsumer vertexconsumer = new SheetedDecalTextureGenerator(this.renderBuffers.crumblingBufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get(j1)), pose.pose(), pose.normal(), 1.0f);
                            bufferSource1 = (renderType) -> {
                                VertexConsumer vertexConsumer2 = bufferSource.getBuffer(renderType);
                                return renderType.affectsCrumbling() ? VertexMultiConsumer.create(vertexconsumer, vertexConsumer2) : vertexConsumer2;
                            };
                        }
                    }

                    this.minecraft.getBlockEntityRenderDispatcher().render(blockEntity, gameTime, poseStack, bufferSource1);
                    poseStack.popPose();
                }
            }
        }
    }

//    public UniformBuffers getUniformBuffers() { return this.uniformBuffers; }

    public void setNeedsUpdate() {
        this.needsUpdate = true;
    }

<<<<<<< HEAD
    public boolean allUpdated() {
        return !this.needsUpdate;
=======
    public boolean needsUpdate() {
        return this.needsUpdate;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
    }

    public int getVisibleSectionsCount() {
        return this.chunkQueue.size();
    }

    public void setSectionDirty(int x, int y, int z, boolean flag) {
        this.sectionGrid.setDirty(x, y, z, flag);
    }

    public void setSectionsLightReady(int x, int z) {
//        List<RenderSection> list = this.sectionGrid.getRenderSectionsAt(x, z);
//        list.forEach(section -> section.setLightReady(true));
//        this.needsUpdate = true;
    }

    public SectionGrid getSectionGrid() {
        return this.sectionGrid;
    }

    public ChunkAreaManager getChunkAreaManager() {
        return this.sectionGrid.chunkAreaManager;
    }

    public TaskDispatcher getTaskDispatcher() { return taskDispatcher; }

    public VFrustum getFrustum() { return this.frustum; }

    public short getLastFrame() { return lastFrame; }

    public String getChunkStatistics() {
        int i = this.sectionGrid.chunks.length;
//        int j = this.sectionsInFrustum.size();
        int j = this.chunkQueue.size();
        String tasksInfo = this.taskDispatcher == null ? "null" : this.taskDispatcher.getStats();
<<<<<<< HEAD
        return String.format("Chunks: %d(%d)/%d D: %d, %s", this.nonEmptyChunks, j, i, this.lastViewDistance, tasksInfo);
    }

    public void reset() {

        for (ArenaBuffer entry : DrawBuffers.indirectBuffers2.values()) {
            entry.defaultState(4);
        }

    }

    public void cleanUp() {

        DrawBuffers.indirectBuffers2.forEach((terrainRenderType, arenaBuffer) -> arenaBuffer.freeBuffer());
//        DrawBuffers.indirectBuffers2[1].forEach((terrainRenderType, arenaBuffer) -> arenaBuffer.freeBuffer());

=======
        return String.format("Chunks: %d(%d)/%d D: %d, %s", this.nonEmptyChunks, j, i, this.renderDistance, tasksInfo);
    }

    public void cleanUp() {
        if(indirectBuffers != null)
            Arrays.stream(indirectBuffers).forEach(Buffer::freeBuffer);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
    }

}
