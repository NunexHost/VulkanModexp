package net.vulkanmod.vulkan;

import com.mojang.blaze3d.pipeline.RenderTarget;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.Minecraft;
import net.vulkanmod.Initializer;
import net.vulkanmod.gl.GlFramebuffer;
import net.vulkanmod.mixin.window.WindowAccessor;
import net.vulkanmod.render.chunk.AreaUploadManager;
import net.vulkanmod.render.PipelineManager;
import net.vulkanmod.render.profiling.Profiler2;
import net.vulkanmod.vulkan.framebuffer.Framebuffer;
import net.vulkanmod.vulkan.framebuffer.RenderPass;
import net.vulkanmod.vulkan.memory.MemoryManager;
import net.vulkanmod.vulkan.passes.LegacyMainPass;
import net.vulkanmod.vulkan.passes.MainPass;
import net.vulkanmod.vulkan.queue.Queue;
import net.vulkanmod.vulkan.shader.*;
import net.vulkanmod.vulkan.shader.layout.PushConstants;
import net.vulkanmod.vulkan.texture.VTextureSelector;
import net.vulkanmod.vulkan.util.VUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.mojang.blaze3d.platform.GlConst.GL_COLOR_BUFFER_BIT;
import static com.mojang.blaze3d.platform.GlConst.GL_DEPTH_BUFFER_BIT;
import static net.vulkanmod.vulkan.Vulkan.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.EXTDebugUtils.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

public class Renderer {
    public static boolean recomp;
    private static Renderer INSTANCE;

    private static VkDevice device;

    private static boolean swapChainUpdate = false;
    public static boolean skipRendering, useMode = false;

    public static boolean effectActive,renderPassUpdate,hasCalled = false;
    private long boundPipeline;
    private int SUBMITS = 0;

    public static void initRenderer() {
        INSTANCE = new Renderer();
        INSTANCE.init();
    }

    public static Renderer getInstance() { return INSTANCE; }

    public static Drawer getDrawer() { return INSTANCE.drawer; }

    public static int getCurrentFrame() { return currentFrame; }

    public static int getCurrentImage() { return imageIndex; }

    private final Set<GraphicsPipeline> usedPipelines = new ObjectOpenHashSet<>();

    private Drawer drawer;

    private int framesNum;
    private int imagesNum;
    private List<VkCommandBuffer> commandBuffers;
    private ArrayList<Long> imageAvailableSemaphores;
    private ArrayList<Long> renderFinishedSemaphores;
    private long tSemaphore;
    private Framebuffer boundFramebuffer;
    private RenderPass boundRenderPass;

    private static int currentFrame = 0;
    private static int imageIndex;
    private VkCommandBuffer currentCmdBuffer;
    private boolean recordingCmds = false;

    private final List<Runnable> onResizeCallbacks = new ObjectArrayList<>();

    public Renderer() {
        device = Vulkan.getDevice();
        framesNum = getSwapChain().getFramesNum();
        imagesNum = getSwapChain().getImagesNum();
        addOnResizeCallback(Queue::trimCmdPools);
    }

    private void init() {
        MemoryManager.createInstance(Renderer.getFramesNum());
        Vulkan.createStagingBuffers();

        drawer = new Drawer();
        drawer.createResources(framesNum);

        Uniforms.setupDefaultUniforms();
        PipelineManager.init();
        AreaUploadManager.createInstance();

        allocateCommandBuffers();
        createSyncObjects();

        AreaUploadManager.INSTANCE.init();
    }

    private void allocateCommandBuffers() {
        if(commandBuffers != null) {
            commandBuffers.forEach(commandBuffer -> vkFreeCommandBuffers(device, Vulkan.getCommandPool(), commandBuffer));
        }

        commandBuffers = new ArrayList<>(framesNum);

        try(MemoryStack stack = stackPush()) {

            VkCommandBufferAllocateInfo allocInfo = VkCommandBufferAllocateInfo.calloc(stack);
            allocInfo.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO);
            allocInfo.commandPool(getCommandPool());
            allocInfo.level(VK_COMMAND_BUFFER_LEVEL_PRIMARY);
            allocInfo.commandBufferCount(framesNum);

            PointerBuffer pCommandBuffers = stack.mallocPointer(framesNum);

            if (vkAllocateCommandBuffers(device, allocInfo, pCommandBuffers) != VK_SUCCESS) {
                throw new RuntimeException("Failed to allocate command buffers");
            }

            for (int i = 0; i < framesNum; i++) {
                commandBuffers.add(new VkCommandBuffer(pCommandBuffers.get(i), device));
            }
        }
    }

    private void createSyncObjects() {
        imageAvailableSemaphores = new ArrayList<>(framesNum);
        renderFinishedSemaphores = new ArrayList<>(framesNum);
        SUBMITS = framesNum;
        try(MemoryStack stack = stackPush()) {

            VkSemaphoreTypeCreateInfo semaphoreInfoTypeT = VkSemaphoreTypeCreateInfo.calloc(stack)
                    .sType$Default()
                    .semaphoreType(VK12.VK_SEMAPHORE_TYPE_TIMELINE)
                    .initialValue(SUBMITS);
            VkSemaphoreTypeCreateInfo semaphoreInfoTypeB = VkSemaphoreTypeCreateInfo.calloc(stack)
                    .sType$Default()
                    .semaphoreType(VK12.VK_SEMAPHORE_TYPE_BINARY)
                    .initialValue(0);

            VkSemaphoreCreateInfo semaphoreInfo2 = VkSemaphoreCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO)
                    .pNext(semaphoreInfoTypeT);
            VkSemaphoreCreateInfo semaphoreInfo = VkSemaphoreCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO)
                    .pNext(semaphoreInfoTypeB);

            LongBuffer pImageAvailableSemaphore = stack.mallocLong(1);
            LongBuffer pRenderFinishedSemaphore = stack.mallocLong(1);
            LongBuffer pRenderWaitTSemaphore = stack.mallocLong(1);

            for(int i = 0;i < framesNum; i++) {

                if(vkCreateSemaphore(device, semaphoreInfo, null, pImageAvailableSemaphore) != VK_SUCCESS
                        || vkCreateSemaphore(device, semaphoreInfo, null, pRenderFinishedSemaphore) != VK_SUCCESS) {

                    throw new RuntimeException("Failed to create synchronization objects for the frame " + i);
                }

                imageAvailableSemaphores.add(pImageAvailableSemaphore.get(0));
                renderFinishedSemaphores.add(pRenderFinishedSemaphore.get(0));

            }

            vkCreateSemaphore(device, semaphoreInfo2, null, pRenderWaitTSemaphore);
            tSemaphore=pRenderWaitTSemaphore.get(0);

        }
    }

    public void beginFrame() {
        Profiler2 p = Profiler2.getMainProfiler();
        p.pop();
        p.push("Frame_fence");
//        if(recomp)
//        {
//            waitIdle();
//            usedPipelines.forEach(graphicsPipeline -> graphicsPipeline.updateSpecConstant(SPIRVUtils.SpecConstant.USE_FOG));
//            recomp=false;
//        }
        if(renderPassUpdate)
        {
//            LegacyMainPass.PASS.mainTargetBindWrite(effectActive);
//
//            if(!effectActive)
//            {
//                LegacyMainPass.PASS.mainTargetUnbindWrite();
//            }

            updateRenderPassState();

            //Minecraft.getInstance().gameRenderer.resize(getSwapChain().getWidth(), getSwapChain().getHeight());

            useMode=effectActive;
            Initializer.LOGGER.error("Using RenderPass: "+ (useMode ? "Post Effect" : "Default"));
            renderPassUpdate = false;


//                GlFramebuffer.framebufferTexture2D(0, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_TEXTURE_2D, 1, 0);
//                GlFramebuffer.framebufferTexture2D(0, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_TEXTURE_2D, 1, 0);
        }


        if(recomp)
        {
            waitIdle();
            usedPipelines.forEach(graphicsPipeline -> graphicsPipeline.updateSpecConstant(SPIRVUtils.SpecConstant.USE_FOG));
            recomp=false;
        }


        if(swapChainUpdate) {
            recreateSwapChain();
            swapChainUpdate = false;

            if(getSwapChain().getWidth() == 0 && getSwapChain().getHeight() == 0) {
                skipRendering = true;
                Minecraft.getInstance().noRender = true;
            }
            else {
                skipRendering = false;
                Minecraft.getInstance().noRender = false;
            }
        }


        if(skipRendering)
            return;
        try(MemoryStack stack = stackPush()) {
            final int queuedFramesTimeline = framesNum-1; //Wait for the last Submitted Frames: Equal to queuedFrames - 1 z
            VkSemaphoreWaitInfo waitInfo = VkSemaphoreWaitInfo.calloc(stack)
                    .sType$Default()
                    .flags(VK12.VK_SEMAPHORE_WAIT_ANY_BIT)
                    .semaphoreCount(1)
                    .pSemaphores(stack.longs(tSemaphore))
                    .pValues(stack.longs(SUBMITS-queuedFramesTimeline));


            VK12.vkWaitSemaphores(device, waitInfo, VUtil.UINT64_MAX);
            Synchronization.waitSemaphores();
            p.pop();
            p.round();
            p.push("Begin_rendering");

        //        AreaUploadManager.INSTANCE.updateFrame();

            MemoryManager.getInstance().initFrame(currentFrame);
            drawer.setCurrentFrame(currentFrame);

            //Moved before texture updates
        //        this.vertexBuffers[currentFrame].reset();
        //        this.uniformBuffers.reset();
        //        Vulkan.getStagingBuffer(currentFrame).reset();

            resetDescriptors();

            currentCmdBuffer = commandBuffers.get(currentFrame);

            recordingCmds = true;



            IntBuffer pImageIndex = stack.mallocInt(1);

            int vkResult = vkAcquireNextImageKHR(device, Vulkan.getSwapChain().getId(), VUtil.UINT64_MAX,
                    imageAvailableSemaphores.get(currentFrame), VK_NULL_HANDLE, pImageIndex);

            if(vkResult == VK_SUBOPTIMAL_KHR ) {
                swapChainUpdate = true;
            }
            else if(vkResult == VK_ERROR_OUT_OF_DATE_KHR || swapChainUpdate) {
                swapChainUpdate = true;
                return;
            } else if(vkResult != VK_SUCCESS) {
                throw new RuntimeException("Cannot get image: " + vkResult);
            }

            imageIndex = pImageIndex.get(0);

            VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc(stack);
            beginInfo.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);
            beginInfo.flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);

            VkCommandBuffer commandBuffer = currentCmdBuffer;

            int err = vkBeginCommandBuffer(commandBuffer, beginInfo);
            if (err != VK_SUCCESS) {
                throw new RuntimeException("Failed to begin recording command buffer:" + err);
            }

            boundPipeline=0;
            LegacyMainPass.PASS.begin(commandBuffer, stack);

            vkCmdSetDepthBias(commandBuffer, 0.0F, 0.0F, 0.0F);
        }

        p.pop();
    }

    private static void updateRenderPassState() {
        RenderTarget renderTarget = Minecraft.getInstance().getMainRenderTarget();
        int i = getSwapChain().getWidth();
        int j = getSwapChain().getHeight();

        if (renderTarget.frameBufferId >= 0) {
            renderTarget.destroyBuffers();
        }

        renderTarget.createBuffers(i, j, false);
        GlFramebuffer.bindFramebuffer(36160, 0);
    }

    public void endFrame() {
        if(skipRendering)
            return;

        Profiler2 p = Profiler2.getMainProfiler();
        p.push("End_rendering");

        LegacyMainPass.PASS.end(currentCmdBuffer);

        if(!hasCalled) {
            if(effectActive) {
                scheduleRenderPassUpdate();
            }
            effectActive = false;
        }
        if(renderPassUpdate) {

            this.endRenderPass();
            GlFramebuffer.bindFramebuffer(0,0); //Avoid NPE when switching post effect modes
        }
        hasCalled=false;
        submitFrame();
        recordingCmds = false;
        p.pop();
    }

    private void submitFrame() {
        if(swapChainUpdate)
            return;

        try(MemoryStack stack = stackPush()) {

            int vkResult;

            VkTimelineSemaphoreSubmitInfo timelineInfo3 = VkTimelineSemaphoreSubmitInfo.calloc(stack)
                    .sType$Default()
                    .waitSemaphoreValueCount(2)
                    .pWaitSemaphoreValues(stack.longs(0, Synchronization.getValue()))
                    .signalSemaphoreValueCount(2)
                    .pSignalSemaphoreValues(stack.longs(0, ++SUBMITS));

            VkSubmitInfo submitInfo = VkSubmitInfo.calloc(stack);
            submitInfo.sType$Default();
            submitInfo.pNext(timelineInfo3);
            submitInfo.waitSemaphoreCount(2);
            submitInfo.pWaitSemaphores(stack.longs(imageAvailableSemaphores.get(currentFrame), Synchronization.tSemaphore));
            submitInfo.pWaitDstStageMask(stack.ints(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT, VK_PIPELINE_STAGE_ALL_COMMANDS_BIT)); //Must use Image Semaphore to signal present completion for Subpass Execution Dep: any other semaphore will not work + will cause Sync Hazards

            submitInfo.pSignalSemaphores(stack.longs(renderFinishedSemaphores.get(currentFrame), tSemaphore)); //Not using Image Semaphore to avoid submitting signaled image semaphores to vkAcquireNextImageKHR() too early

            submitInfo.pCommandBuffers(stack.pointers(currentCmdBuffer));



            Synchronization.waitSemaphores();

            if((vkResult = vkQueueSubmit(DeviceManager.getGraphicsQueue().queue(), submitInfo, 0)) != VK_SUCCESS) {

                throw new RuntimeException("Failed to submit draw command buffer: " + vkResult);
            }

            VkPresentInfoKHR presentInfo = VkPresentInfoKHR.calloc(stack);
            presentInfo.sType(VK_STRUCTURE_TYPE_PRESENT_INFO_KHR);

            presentInfo.pWaitSemaphores(stack.longs(renderFinishedSemaphores.get(currentFrame))); // VK_KHR_swapchain doesn't support timeline semaphores: must use binary fallback instead

            presentInfo.swapchainCount(1);
            presentInfo.pSwapchains(stack.longs(Vulkan.getSwapChain().getId()));

            presentInfo.pImageIndices(stack.ints(imageIndex));

            vkResult = vkQueuePresentKHR(DeviceManager.getPresentQueue().queue(), presentInfo);

            if(vkResult == VK_ERROR_OUT_OF_DATE_KHR || vkResult == VK_SUBOPTIMAL_KHR || swapChainUpdate) {
                swapChainUpdate = true;
                return;
            } else if(vkResult != VK_SUCCESS) {
                throw new RuntimeException("Failed to present swap chain image");
            }

            currentFrame = (currentFrame + 1) % framesNum;
        }
    }

    public void endRenderPass() {
        endRenderPass(currentCmdBuffer);
    }

    public void endRenderPass(VkCommandBuffer commandBuffer) {
        if(skipRendering || this.boundFramebuffer == null)
            return;

        if(!DYNAMIC_RENDERING)
            this.boundRenderPass.endRenderPass(currentCmdBuffer);
        else
            KHRDynamicRendering.vkCmdEndRenderingKHR(commandBuffer);

        this.boundRenderPass = null;
        this.boundFramebuffer = null;
    }

    public boolean beginRendering(RenderPass renderPass, Framebuffer framebuffer) {
        if(skipRendering || !recordingCmds)
            return false;

        if(this.boundFramebuffer != framebuffer) {
            this.endRenderPass(currentCmdBuffer);

            try (MemoryStack stack = stackPush()) {
                framebuffer.beginRenderPass(currentCmdBuffer, renderPass, stack);
            }

            this.boundFramebuffer = framebuffer;
        }
        return true;
    }

    public void setBoundFramebuffer(Framebuffer framebuffer) {
        this.boundFramebuffer = framebuffer;
    }

    public void resetBuffers() {
        Profiler2 p = Profiler2.getMainProfiler();
        p.push("Frame_ops");

        drawer.resetBuffers(currentFrame);

        AreaUploadManager.INSTANCE.updateFrame();
        Vulkan.getStagingBuffer().reset();
    }

    public void addUsedPipeline(GraphicsPipeline pipeline) {
        usedPipelines.add(pipeline);
    }

    public void removeUsedPipeline(GraphicsPipeline pipeline) { usedPipelines.remove(pipeline); }

    private void resetDescriptors() {
        for(Pipeline pipeline : usedPipelines) {
            pipeline.resetDescriptorPool(currentFrame);
        }

        usedPipelines.clear();
    }

    void waitForSwapChain()
    {


//        constexpr VkPipelineStageFlags t=VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            //Empty Submit
            VkSubmitInfo info = VkSubmitInfo.calloc(stack)
                    .sType$Default()
                    .pWaitSemaphores(stack.longs(imageAvailableSemaphores.get(currentFrame)))
                    .pWaitDstStageMask(stack.ints(VK_PIPELINE_STAGE_ALL_COMMANDS_BIT));

            vkQueueSubmit(DeviceManager.getGraphicsQueue().queue(), info, 0);
//            vkWaitForFences(device, inFlightFences.get(currentFrame),  true, -1);
        }
    }

    private void recreateSwapChain() {
        Vulkan.waitIdle();

        vkResetCommandPool(Vulkan.getDevice(), getCommandPool(), VK_COMMAND_BUFFER_RESET_RELEASE_RESOURCES_BIT);

        Vulkan.recreateSwapChain();

        //Semaphores need to be recreated in order to make them unsignaled
        destroySyncObjects();

        int newFramesNum = getSwapChain().getFramesNum();
        imagesNum = getSwapChain().getImagesNum();

        if(framesNum != newFramesNum) {
            AreaUploadManager.INSTANCE.waitAllUploads();

            framesNum = newFramesNum;
            MemoryManager.createInstance(newFramesNum);
            createStagingBuffers();
            allocateCommandBuffers();

            Pipeline.recreateDescriptorSets(framesNum);

            drawer.createResources(framesNum);
        }

        createSyncObjects();

        this.onResizeCallbacks.forEach(Runnable::run);
        ((WindowAccessor)(Object)Minecraft.getInstance().getWindow()).getEventHandler().resizeDisplay();

        currentFrame = 0;
    }

    public void cleanUpResources() {
        destroySyncObjects();

        drawer.cleanUpResources();

        PipelineManager.destroyPipelines();
        VTextureSelector.getWhiteTexture().free();
    }

    private void destroySyncObjects() {
        for (int i = 0; i < framesNum; ++i) {
            vkDestroySemaphore(device, imageAvailableSemaphores.get(i), null);
            vkDestroySemaphore(device, renderFinishedSemaphores.get(i), null);
        }
        vkDestroySemaphore(device, tSemaphore, null);
    }

    public void setBoundRenderPass(RenderPass boundRenderPass) {
        this.boundRenderPass = boundRenderPass;
    }

    public RenderPass getBoundRenderPass() {
        return boundRenderPass;
    }

//    public void setMainPass(MainPass mainPass) { this.mainPass = mainPass; }

    public MainPass getMainPass() { return LegacyMainPass.PASS; }

    public void addOnResizeCallback(Runnable runnable) {
        this.onResizeCallbacks.add(runnable);
    }

    public boolean bindGraphicsPipeline(GraphicsPipeline pipeline) {
        VkCommandBuffer commandBuffer = currentCmdBuffer;

        //Debug
        if(boundRenderPass == null)
            LegacyMainPass.PASS.mainTargetBindWrite(true);

        PipelineState currentState = PipelineState.getCurrentPipelineState(boundRenderPass);
        final long handle = pipeline.getHandle(currentState);
        if(boundPipeline==handle) {
            return false;
        }
        vkCmdBindPipeline(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, handle);
        boundPipeline=handle;
//        if(usedPipelines.contains(pipeline))
//        {
////            Initializer.LOGGER.warn("Double Bind: "+pipeline.name);
//            return true;
//        }

        addUsedPipeline(pipeline);
        return true;
    }

    public void uploadAndBindUBOs(Pipeline pipeline, boolean shouldUpdate) {
        VkCommandBuffer commandBuffer = currentCmdBuffer;
        pipeline.bindDescriptorSets(commandBuffer, currentFrame, shouldUpdate);
    }

    public void pushConstants(Pipeline pipeline) {
        VkCommandBuffer commandBuffer = currentCmdBuffer;

        PushConstants pushConstants = pipeline.getPushConstants();

        try (MemoryStack stack = stackPush()) {
            ByteBuffer buffer = stack.malloc(pushConstants.getSize());
            long ptr = MemoryUtil.memAddress0(buffer);
            pushConstants.update(ptr);

            nvkCmdPushConstants(commandBuffer, pipeline.getLayout(), VK_SHADER_STAGE_VERTEX_BIT, 0, pushConstants.getSize(), ptr);
        }

    }

    public static void setDepthBias(float units, float factor) {
        VkCommandBuffer commandBuffer = INSTANCE.currentCmdBuffer;

        vkCmdSetDepthBias(commandBuffer, units, 0.0f, factor);
    }

    public static void clearAttachments(int v) {
        Framebuffer framebuffer = Renderer.getInstance().boundFramebuffer;
        if(framebuffer == null)
            return;

        clearAttachments(v, framebuffer.getWidth(), framebuffer.getHeight());
    }

    public static void clearAttachments(int v, int width, int height) {
        if(skipRendering)
            return;

        try(MemoryStack stack = stackPush()) {
            //ClearValues have to be different for each attachment to clear,
            //it seems it uses the same buffer: color and depth values override themselves
            VkClearValue colorValue = VkClearValue.calloc(stack);
            colorValue.color().float32(VRenderSystem.clearColor);

            VkClearValue depthValue = VkClearValue.calloc(stack);
            depthValue.depthStencil().set(VRenderSystem.clearDepth, 0); //Use fast depth clears if possible

            int attachmentsCount = v == (GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT) ? 2 : 1;
            final VkClearAttachment.Buffer pAttachments = VkClearAttachment.malloc(attachmentsCount, stack);
            switch (v) {
                case GL_DEPTH_BUFFER_BIT -> {

                    VkClearAttachment clearDepth = pAttachments.get(0);
                    clearDepth.aspectMask(VK_IMAGE_ASPECT_DEPTH_BIT);
                    clearDepth.colorAttachment(0);
                    clearDepth.clearValue(depthValue);
                }
                case GL_COLOR_BUFFER_BIT -> {

                    VkClearAttachment clearColor = pAttachments.get(0);
                    clearColor.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
                    clearColor.colorAttachment(0);
                    clearColor.clearValue(colorValue);
                }
                case GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT -> {

                    VkClearAttachment clearColor = pAttachments.get(0);
                    clearColor.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
                    clearColor.colorAttachment(0);
                    clearColor.clearValue(colorValue);

                    VkClearAttachment clearDepth = pAttachments.get(1);
                    clearDepth.aspectMask(VK_IMAGE_ASPECT_DEPTH_BIT);
                    clearDepth.colorAttachment(0);
                    clearDepth.clearValue(depthValue);
                }
                default -> throw new RuntimeException("unexpected value");
            }

            //Rect to clear
            VkRect2D renderArea = VkRect2D.malloc(stack);
            renderArea.offset().set(0, 0);
            renderArea.extent().set(width, height);

            VkClearRect.Buffer pRect = VkClearRect.malloc(1, stack);
            pRect.rect(renderArea);
            pRect.baseArrayLayer(0);
            pRect.layerCount(1);

            vkCmdClearAttachments(INSTANCE.currentCmdBuffer, pAttachments, pRect);
        }
    }

    public static void setViewport(int x, int y, int width, int height) {
        if(!INSTANCE.recordingCmds)
            return;

        try(MemoryStack stack = stackPush()) {
            VkViewport.Buffer viewport = VkViewport.malloc(1, stack);
            viewport.x(x);
            viewport.y(height + y);
            viewport.width(width);
            viewport.height(-height);
            viewport.minDepth(0.0f);
            viewport.maxDepth(1.0f);

            VkRect2D.Buffer scissor = VkRect2D.malloc(1, stack);
            scissor.offset().set(0, 0);
            scissor.extent().set(width, Math.abs(height));

            vkCmdSetViewport(INSTANCE.currentCmdBuffer, 0, viewport);
            vkCmdSetScissor(INSTANCE.currentCmdBuffer, 0, scissor);
        }
    }

    public static void resetViewport() {
        if(!effectActive) scheduleRenderPassUpdate();
        effectActive=hasCalled=true;
        try(MemoryStack stack = stackPush()) {
            int width = getSwapChain().getWidth();
            int height = getSwapChain().getHeight();

            VkViewport.Buffer viewport = VkViewport.malloc(1, stack);
            viewport.x(0.0f);
            viewport.y(height);
            viewport.width(width);
            viewport.height(-height);
            viewport.minDepth(0.0f);
            viewport.maxDepth(1.0f);

            vkCmdSetViewport(INSTANCE.currentCmdBuffer, 0, viewport);
        }
    }

    public static void setScissor(int x, int y, int width, int height) {
        if(INSTANCE.boundFramebuffer == null)
            return;

        try(MemoryStack stack = stackPush()) {
            int framebufferHeight = INSTANCE.boundFramebuffer.getHeight();

            VkRect2D.Buffer scissor = VkRect2D.malloc(1, stack);
            scissor.offset().set(x, framebufferHeight - (y + height));
            scissor.extent().set(width, height);

            vkCmdSetScissor(INSTANCE.currentCmdBuffer, 0, scissor);
        }
    }

    public static void resetScissor() {
        if(INSTANCE.boundFramebuffer == null)
            return;

        try(MemoryStack stack = stackPush()) {
            VkRect2D.Buffer scissor = INSTANCE.boundFramebuffer.scissor(stack);
            vkCmdSetScissor(INSTANCE.currentCmdBuffer, 0, scissor);
        }
    }

    public static void pushDebugSection(String s) {
        if(Vulkan.ENABLE_VALIDATION_LAYERS) {
            VkCommandBuffer commandBuffer = INSTANCE.currentCmdBuffer;

            try(MemoryStack stack = stackPush()) {
                VkDebugUtilsLabelEXT markerInfo = VkDebugUtilsLabelEXT.calloc(stack);
                markerInfo.sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_LABEL_EXT);
                ByteBuffer string = stack.UTF8(s);
                markerInfo.pLabelName(string);
                vkCmdBeginDebugUtilsLabelEXT(commandBuffer, markerInfo);
            }
        }
    }

    public static void popDebugSection() {
        if(Vulkan.ENABLE_VALIDATION_LAYERS) {
            VkCommandBuffer commandBuffer = INSTANCE.currentCmdBuffer;

            vkCmdEndDebugUtilsLabelEXT(commandBuffer);
        }
    }

    public static void popPushDebugSection(String s) {
        popDebugSection();
        pushDebugSection(s);
    }

    public static int getFramesNum() { return INSTANCE.framesNum; }

    public static VkCommandBuffer getCommandBuffer() { return INSTANCE.currentCmdBuffer; }

    public static boolean isRecording() { return INSTANCE.recordingCmds; }

    public static void scheduleSwapChainUpdate() { swapChainUpdate = true; }
    public static void scheduleRenderPassUpdate() { renderPassUpdate = true; }
}
