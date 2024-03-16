package net.vulkanmod.vulkan.util;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

<<<<<<< HEAD
public record MappedBuffer(ByteBuffer buffer, long ptr)
{
=======
public class MappedBuffer {

    public final ByteBuffer buffer;
    public final long ptr;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

    public static MappedBuffer createFromBuffer(ByteBuffer buffer) {
        return new MappedBuffer(buffer, MemoryUtil.memAddress0(buffer));
    }
<<<<<<< HEAD

    public MappedBuffer(ByteBuffer buffer1) {
        this(buffer1, MemoryUtil.memAddress0(buffer1));
=======
    MappedBuffer(ByteBuffer buffer, long ptr) {
        this.buffer = buffer;
        this.ptr = ptr;
    }

    public MappedBuffer(int size) {
        this.buffer = MemoryUtil.memAlloc(size);
        this.ptr = MemoryUtil.memAddress0(this.buffer);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
    }

    public void putFloat(int idx, float f) {
        VUtil.UNSAFE.putFloat(ptr + idx, f);
    }

    public void putInt(int idx, int f) {
        VUtil.UNSAFE.putInt(ptr + idx, f);
    }

    public float getFloat(int idx) {
        return VUtil.UNSAFE.getFloat(ptr + idx);
    }

    public int getInt(int idx) {
        return VUtil.UNSAFE.getInt(ptr + idx);
    }
}
