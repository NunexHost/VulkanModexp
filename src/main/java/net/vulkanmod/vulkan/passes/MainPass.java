package net.vulkanmod.vulkan.passes;

import com.mojang.blaze3d.pipeline.RenderTarget;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandBuffer;

public interface MainPass {

    void begin(VkCommandBuffer commandBuffer, MemoryStack stack);
    void end(VkCommandBuffer commandBuffer);

<<<<<<< HEAD
    default void mainTargetBindWrite(boolean bl) {}
=======
    default void mainTargetBindWrite() {}
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

    default void mainTargetUnbindWrite() {}
}
