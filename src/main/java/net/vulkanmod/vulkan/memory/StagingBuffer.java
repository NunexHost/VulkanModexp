package net.vulkanmod.vulkan.memory;

import net.vulkanmod.render.chunk.util.Util;
<<<<<<< HEAD
=======
import net.vulkanmod.vulkan.util.VUtil;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import static org.lwjgl.system.libc.LibCString.nmemcpy;
import static org.lwjgl.vulkan.VK10.*;

public class StagingBuffer extends Buffer {

    public StagingBuffer(int bufferSize) {
<<<<<<< HEAD
        super(VK_BUFFER_USAGE_TRANSFER_SRC_BIT, MemoryType.BAR_MEM);
=======
        super(VK_BUFFER_USAGE_TRANSFER_SRC_BIT, MemoryTypes.HOST_MEM);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
        this.usedBytes = 0;
        this.offset = 0;

        this.createBuffer(bufferSize);
    }

    public void copyBuffer(int size, ByteBuffer byteBuffer) {

        if(size > this.bufferSize - this.usedBytes) {
            resizeBuffer((this.bufferSize + size) * 2);
        }

//        VUtil.memcpy(byteBuffer, this.data.getByteBuffer(0, this.bufferSize), this.usedBytes);
        nmemcpy(this.data.get(0) + this.usedBytes, MemoryUtil.memAddress(byteBuffer), size);

        offset = usedBytes;
        usedBytes += size;

        //createVertexBuffer(vertexSize, vertexCount, byteBuffer);
    }
<<<<<<< HEAD
    public void copyBuffer2(int size, long byteBuffer) {

        if(size > this.bufferSize - this.usedBytes) {
            resizeBuffer((this.bufferSize + size) * 2);
        }

//        VUtil.memcpy(byteBuffer, this.data.getByteBuffer(0, this.bufferSize), this.usedBytes);
        nmemcpy(this.data.get(0) + this.usedBytes, (byteBuffer), size);

        offset = usedBytes;
        usedBytes += size;

        //createVertexBuffer(vertexSize, vertexCount, byteBuffer);
    }
=======
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

    public void align(int alignment) {
        int alignedValue = Util.align(usedBytes, alignment);

        if(alignedValue > this.bufferSize) {
            resizeBuffer((this.bufferSize) * 2);
        }

        usedBytes = alignedValue;
    }

    private void resizeBuffer(int newSize) {
<<<<<<< HEAD
        this.type.freeBuffer(this);
=======
        MemoryManager.getInstance().addToFreeable(this);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
        this.createBuffer(newSize);

        System.out.println("resized staging buffer to: " + newSize);
    }
}
