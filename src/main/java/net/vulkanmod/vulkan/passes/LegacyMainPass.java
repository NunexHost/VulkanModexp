package net.vulkanmod.vulkan.passes;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import net.vulkanmod.vulkan.Renderer;
import net.vulkanmod.vulkan.VRenderSystem;
import net.vulkanmod.vulkan.Vulkan;
import net.vulkanmod.vulkan.framebuffer.SwapChain;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkViewport;

import static org.lwjgl.vulkan.VK10.*;

public class LegacyMainPass implements MainPass {
<<<<<<< HEAD
    public static final LegacyMainPass PASS = new LegacyMainPass();

    @Override
    public void begin(VkCommandBuffer commandBuffer, MemoryStack stack) {
        if(!Renderer.useMode) //DefaultMainPass mode
        {
            SwapChain swapChain = Vulkan.getSwapChain();
//            swapChain.colorAttachmentLayout(stack, commandBuffer, Renderer.getCurrentImage());
=======
    public static LegacyMainPass PASS = new LegacyMainPass();

    private RenderTarget mainTarget;

    LegacyMainPass() {
        this.mainTarget = Minecraft.getInstance().getMainRenderTarget();
    }

    @Override
    public void begin(VkCommandBuffer commandBuffer, MemoryStack stack) {
    }

    @Override
    public void mainTargetBindWrite() {
        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
        mainTarget.bindWrite(true);
    }

    @Override
    public void mainTargetUnbindWrite() {
        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
        mainTarget.unbindWrite();
    }

    @Override
    public void end(VkCommandBuffer commandBuffer) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            Renderer.getInstance().endRenderPass(commandBuffer);

            RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
            mainTarget.bindRead();

            SwapChain swapChain = Vulkan.getSwapChain();
            swapChain.colorAttachmentLayout(stack, commandBuffer, Renderer.getCurrentImage());
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

            swapChain.beginRenderPass(commandBuffer, stack);
            Renderer.getInstance().setBoundFramebuffer(swapChain);

            VkViewport.Buffer pViewport = swapChain.viewport(stack);
            vkCmdSetViewport(commandBuffer, 0, pViewport);

            VkRect2D.Buffer pScissor = swapChain.scissor(stack);
            vkCmdSetScissor(commandBuffer, 0, pScissor);
<<<<<<< HEAD
        }
    }

    @Override
    public void mainTargetBindWrite(boolean bl) {
        if(Renderer.useMode) { //LegacyMainPass mode
            RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
            mainTarget.bindWrite(bl);
        }
    }

    @Override
    public void mainTargetUnbindWrite() {
        if(Renderer.useMode) //LegacyMainPass mode
        {
            RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
            mainTarget.unbindWrite();
        }
    }

    @Override
    public void end(VkCommandBuffer commandBuffer) {
        if(Renderer.useMode) { //LegacyMainPass mode
            try (MemoryStack stack = MemoryStack.stackPush()) {
                Renderer.getInstance().endRenderPass(commandBuffer);

                RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
                mainTarget.bindRead();

                SwapChain swapChain = Vulkan.getSwapChain();
                swapChain.colorAttachmentLayout(stack, commandBuffer, Renderer.getCurrentImage());

                swapChain.beginRenderPass(commandBuffer, stack);
                Renderer.getInstance().setBoundFramebuffer(swapChain);

                VkViewport.Buffer pViewport = swapChain.viewport(stack);
                vkCmdSetViewport(commandBuffer, 0, pViewport);

                VkRect2D.Buffer pScissor = swapChain.scissor(stack);
                vkCmdSetScissor(commandBuffer, 0, pScissor);

                VRenderSystem.disableBlend();
                Minecraft.getInstance().getMainRenderTarget().blitToScreen(swapChain.getWidth(), swapChain.getHeight());
            }
=======

            VRenderSystem.disableBlend();
            Minecraft.getInstance().getMainRenderTarget().blitToScreen(swapChain.getWidth(), swapChain.getHeight());
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
        }

        Renderer.getInstance().endRenderPass(commandBuffer);

        int result = vkEndCommandBuffer(commandBuffer);
        if(result != VK_SUCCESS) {
            throw new RuntimeException("Failed to record command buffer:" + result);
        }
    }
}
