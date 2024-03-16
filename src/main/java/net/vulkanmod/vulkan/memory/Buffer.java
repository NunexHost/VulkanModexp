package net.vulkanmod.vulkan.memory;

import org.lwjgl.PointerBuffer;
<<<<<<< HEAD
import org.lwjgl.system.MemoryUtil;

public class Buffer {
=======

public abstract class Buffer {
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
    protected long id;
    protected long allocation;

    protected int bufferSize;
    protected int usedBytes;
    protected int offset;

<<<<<<< HEAD
    protected final MemoryType type;
    protected final int usage;
    public final PointerBuffer data;
=======
    protected MemoryType type;
    protected int usage;
    protected PointerBuffer data;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

    protected Buffer(int usage, MemoryType type) {
        //TODO: check usage
        this.usage = usage;
        this.type = type;
<<<<<<< HEAD
        this.data = type.mappable() ? MemoryUtil.memAllocPointer(1) : null;
=======

>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
    }

    protected void createBuffer(int bufferSize) {
        this.type.createBuffer(this, bufferSize);

        if(this.type.mappable()) {
<<<<<<< HEAD
            MemoryManager.getInstance().Map(this.allocation, this.data);
=======
            this.data = MemoryManager.getInstance().Map(this.allocation);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
        }
    }

    public void freeBuffer() {
<<<<<<< HEAD
        this.type.freeBuffer(this);
=======
        MemoryManager.getInstance().addToFreeable(this);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
    }

    public void reset() { usedBytes = 0; }

    public long getAllocation() { return allocation; }

<<<<<<< HEAD
    public int getUsedBytes() { return usedBytes; }

    public int getOffset() { return offset; }
=======
    public long getUsedBytes() { return usedBytes; }

    public long getOffset() { return offset; }
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

    public long getId() { return id; }

    public int getBufferSize() { return bufferSize; }

    protected void setBufferSize(int size) { this.bufferSize = size; }

    protected void setId(long id) { this.id = id; }

    protected void setAllocation(long allocation) {this.allocation = allocation; }

<<<<<<< HEAD
    public BufferInfo getBufferInfo() { return new BufferInfo(this.id, this.allocation, this.bufferSize); }

    public record BufferInfo(long id, long allocation, long bufferSize) {
=======
    public BufferInfo getBufferInfo() { return new BufferInfo(this.id, this.allocation, this.bufferSize, this.type.getType()); }

    public record BufferInfo(long id, long allocation, long bufferSize, MemoryType.Type type) {
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

    }
}
