package net.vulkanmod.vulkan;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.vulkanmod.vulkan.queue.CommandPool;
<<<<<<< HEAD
import static net.vulkanmod.vulkan.queue.Queue.GraphicsQueue;
import static net.vulkanmod.vulkan.queue.Queue.TransferQueue;
import net.vulkanmod.vulkan.util.VUtil;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK12;
=======
import net.vulkanmod.vulkan.queue.GraphicsQueue;
import net.vulkanmod.vulkan.queue.TransferQueue;
import net.vulkanmod.vulkan.util.VUtil;
import org.lwjgl.system.MemoryUtil;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
import org.lwjgl.vulkan.VkDevice;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;

public class Synchronization {
    private static final int ALLOCATION_SIZE = 50;

    public static final Synchronization INSTANCE = new Synchronization(ALLOCATION_SIZE);

    private final LongBuffer fences;
    private int idx = 0;

<<<<<<< HEAD
    private final ObjectArrayList<CommandPool.CommandBuffer> commandBuffers = new ObjectArrayList<>();
=======
    private ObjectArrayList<CommandPool.CommandBuffer> commandBuffers = new ObjectArrayList<>();
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

    Synchronization(int allocSize) {
        this.fences = MemoryUtil.memAllocLong(allocSize);
    }

    public synchronized void addCommandBuffer(CommandPool.CommandBuffer commandBuffer) {
        this.addFence(commandBuffer.getFence());
        this.commandBuffers.add(commandBuffer);
    }

    public synchronized void addFence(long fence) {
        if(idx == ALLOCATION_SIZE)
            waitFences();

        fences.put(idx, fence);
        idx++;
    }

    public synchronized void waitFences() {
<<<<<<< HEAD
        //TODO: TimelineSemaphore Index
=======

>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
        if(idx == 0) return;

        VkDevice device = Vulkan.getDevice();

        fences.limit(idx);

<<<<<<< HEAD

        vkWaitForFences(device, fences, true, VUtil.UINT64_MAX);
        //VK12.vkWaitSemaphores(device, tmSemWaitInfo, VUtil.UINT64_MAX)
=======
        for (int i = 0; i < idx; i++) {
            vkWaitForFences(device, fences.get(i), true, VUtil.UINT64_MAX);
        }

>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
        this.commandBuffers.forEach(CommandPool.CommandBuffer::reset);
        this.commandBuffers.clear();

        fences.limit(ALLOCATION_SIZE);
        idx = 0;
    }

    public static void waitFence(long fence) {
        VkDevice device = Vulkan.getDevice();

        vkWaitForFences(device, fence, true, VUtil.UINT64_MAX);
    }

    public static boolean checkFenceStatus(long fence) {
        VkDevice device = Vulkan.getDevice();
        return vkGetFenceStatus(device, fence) == VK_SUCCESS;
    }

}
