package net.vulkanmod.vulkan.memory;

import net.vulkanmod.vulkan.*;
import net.vulkanmod.vulkan.queue.CommandPool;
<<<<<<< HEAD
import static net.vulkanmod.vulkan.queue.Queue.TransferQueue;
=======
import net.vulkanmod.vulkan.queue.TransferQueue;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

import java.nio.ByteBuffer;

import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_INDIRECT_BUFFER_BIT;

public class IndirectBuffer extends Buffer {
    CommandPool.CommandBuffer commandBuffer;

    public IndirectBuffer(int size, MemoryType type) {
        super(VK_BUFFER_USAGE_INDIRECT_BUFFER_BIT, type);
        this.createBuffer(size);
    }

    public void recordCopyCmd(ByteBuffer byteBuffer) {
        int size = byteBuffer.remaining();

        if(size > this.bufferSize - this.usedBytes) {
            resizeBuffer();
        }

        if(this.type.mappable()) {
            this.type.copyToBuffer(this, size, byteBuffer);
        }
        else {
            if(commandBuffer == null)
                commandBuffer = DeviceManager.getTransferQueue().beginCommands();

            StagingBuffer stagingBuffer = Vulkan.getStagingBuffer();
            stagingBuffer.copyBuffer(size, byteBuffer);

            TransferQueue.uploadBufferCmd(commandBuffer.getHandle(), stagingBuffer.id, stagingBuffer.offset, this.getId(), this.getUsedBytes(), size);
        }

        offset = usedBytes;
        usedBytes += size;
    }

    private void resizeBuffer() {
<<<<<<< HEAD
        this.type.freeBuffer(this);
=======
        MemoryManager.getInstance().addToFreeable(this);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
        int newSize = this.bufferSize + (this.bufferSize >> 1);
        this.createBuffer(newSize);
        this.usedBytes = 0;
    }

    public void submitUploads() {
        if(commandBuffer == null)
            return;

        DeviceManager.getTransferQueue().submitCommands(commandBuffer);
        Synchronization.INSTANCE.addCommandBuffer(commandBuffer);
        commandBuffer = null;
    }

    //debug
    public ByteBuffer getByteBuffer() {
        return this.data.getByteBuffer(0, this.bufferSize);
    }
}
