package net.vulkanmod.render.chunk;

<<<<<<< HEAD
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import net.vulkanmod.render.chunk.util.Util;
=======
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.vulkanmod.render.chunk.util.Util;
import net.vulkanmod.vulkan.DeviceManager;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
import net.vulkanmod.vulkan.memory.*;

import java.nio.ByteBuffer;
import java.util.LinkedList;

<<<<<<< HEAD
import static net.vulkanmod.vulkan.queue.Queue.GraphicsQueue;
import static net.vulkanmod.vulkan.queue.Queue.TransferQueue;
=======
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT;

public class AreaBuffer {
    private final MemoryType memoryType;
    private final int usage;

    private final LinkedList<Segment> freeSegments = new LinkedList<>();
<<<<<<< HEAD
    private final Int2ReferenceOpenHashMap<Segment> usedSegments = new Int2ReferenceOpenHashMap<>();
=======
    private final Reference2ReferenceOpenHashMap<Segment, Segment> usedSegments = new Reference2ReferenceOpenHashMap<>();
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

    private final int elementSize;

    private Buffer buffer;

    int size;
    int used;

    public AreaBuffer(int usage, int size, int elementSize) {

        this.usage = usage;
        this.elementSize = elementSize;
<<<<<<< HEAD
        this.memoryType = MemoryType.GPU_MEM;
=======
        this.memoryType = MemoryTypes.GPU_MEM;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

        this.buffer = this.allocateBuffer(size);
        this.size = size;

        freeSegments.add(new Segment(0, size));
    }

    private Buffer allocateBuffer(int size) {
<<<<<<< HEAD

        return this.usage == VK_BUFFER_USAGE_VERTEX_BUFFER_BIT ? new VertexBuffer(size, memoryType) : new IndexBuffer(size, memoryType);
    }

    public void upload(ByteBuffer byteBuffer, Segment uploadSegment, int idx) {
        //free old segment


        Segment segment = this.usedSegments.get(idx);
        final int size = byteBuffer.remaining();

        if (size % elementSize != 0)
            throw new RuntimeException("unaligned byteBuffer");

        if(segment == null || size>segment.size) {
            this.setSegmentFree(idx);


            segment = findSegment(size);

            if (segment.size - size > 0) {
                freeSegments.add(new Segment(segment.offset + size, segment.size - size));
            }

            usedSegments.put(idx, new Segment(segment.offset, size));
            this.used += size;
        }

        AreaUploadManager.INSTANCE.uploadAsync(uploadSegment, this.buffer.getId(), segment.offset, size, byteBuffer);
=======
        int bufferSize = size;

        Buffer buffer;
        if(this.usage == VK_BUFFER_USAGE_VERTEX_BUFFER_BIT) {
            buffer = new VertexBuffer(bufferSize, memoryType);
        } else {
            buffer = new IndexBuffer(bufferSize, memoryType);
        }
        return buffer;
    }

    public synchronized void upload(ByteBuffer byteBuffer, Segment uploadSegment) {
        //free old segment
        if(uploadSegment.offset != -1) {
            this.setSegmentFree(uploadSegment);
        }

        int size = byteBuffer.remaining();

        if(size % elementSize != 0)
            throw new RuntimeException("unaligned byteBuffer");

        Segment segment = findSegment(size);

        if(segment.size - size > 0) {
            freeSegments.add(new Segment(segment.offset + size, segment.size - size));
        }

        usedSegments.put(uploadSegment, new Segment(segment.offset, size));

        Buffer dst = this.buffer;
        AreaUploadManager.INSTANCE.uploadAsync(uploadSegment, dst.getId(), segment.offset, size, byteBuffer);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

        uploadSegment.offset = segment.offset;
        uploadSegment.size = size;
        uploadSegment.status = Segment.PENDING_BIT;

<<<<<<< HEAD

=======
        this.used += size;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

    }

    public Segment findSegment(int size) {
        Segment segment = null;
        int i = 0;
        int idx = 0;
        int t = Integer.MAX_VALUE;
        for(Segment segment1 : freeSegments) {

            if(segment1.size >= size && segment1.size < t) {
                segment = segment1;
                t = segment1.size;
                idx = i;
            }
            ++i;
        }

        if(segment == null) {
            return this.reallocate(size);
        }

        freeSegments.remove(idx);

        return segment;
    }

    public Segment reallocate(int uploadSize) {
        int oldSize = this.size;
        int increment = this.size >> 1;

        //Try to increase size increment 8 times
        for(int i = 0; i < 8 && increment <= uploadSize; ++i) {
<<<<<<< HEAD
            increment <<= 1;
=======
            increment *= 2;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
        }

        if(increment <= uploadSize)
            throw new RuntimeException(String.format("Size increment %d <= %d (Upload size)", increment, uploadSize));

        int newSize = oldSize + increment;

        Buffer buffer = this.allocateBuffer(newSize);

<<<<<<< HEAD
        AreaUploadManager.INSTANCE.swapBuffers(this.buffer.getId(), buffer.getId());
        AreaUploadManager.INSTANCE.waitAllUploads();

        //Sync upload
        TransferQueue.uploadBufferImmediate(this.buffer.getId(), 0, buffer.getId(), 0, this.buffer.getBufferSize());
=======
        AreaUploadManager.INSTANCE.submitUploads();
        AreaUploadManager.INSTANCE.waitAllUploads();

        //Sync upload
        DeviceManager.getTransferQueue().uploadBufferImmediate(this.buffer.getId(), 0, buffer.getId(), 0, this.buffer.getBufferSize());
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
        this.buffer.freeBuffer();
        this.buffer = buffer;

        this.size = newSize;

        int offset = Util.align(oldSize, elementSize);

<<<<<<< HEAD
        return new Segment(offset, increment);
    }

    public void setSegmentFree(int index) {
        Segment segment = usedSegments.remove(index);
=======
        Segment segment = new Segment(offset, increment);
        return segment;
    }

    public synchronized void setSegmentFree(Segment uploadSegment) {
        Segment segment = usedSegments.remove(uploadSegment);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

        if(segment == null)
            return;

        this.freeSegments.add(segment);
        this.used -= segment.size;
    }

    public long getId() {
        return this.buffer.getId();
    }

    public void freeBuffer() {
        this.buffer.freeBuffer();
//        this.globalBuffer.freeSubAllocation(subAllocation);
    }

    public static class Segment {
        public static final byte PENDING_BIT = 0x1;
        public static final byte READY_BIT = 0x2;

        int offset, size;
        byte status;

        public Segment() {
            reset();
        }

        private Segment(int offset, int size) {
            this.offset = offset;
            this.size = size;
            this.status = 0;
        }

        public void reset() {
            this.offset = -1;
            this.size = -1;
<<<<<<< HEAD

=======
            this.status = 0;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
        }

        public int getOffset() {
            return offset;
        }

        public int getSize() {
            return size;
        }

        void setPending() {
            this.status = PENDING_BIT;
        }

        public boolean isPending() {
            return (this.status & PENDING_BIT) != 0;
        }

        public void setReady() {
            this.status = READY_BIT;
        }

        public boolean isReady() {
            return (this.status & READY_BIT) != 0;
        }

    }

//    //Debug
//    public List<Segment> findConflicts(int offset) {
//        List<Segment> segments = new ArrayList<>();
//        Segment segment = this.usedSegments.get(offset);
//
//        for(Segment s : this.usedSegments.values()) {
//            if((s.offset >= segment.offset && s.offset < (segment.offset + segment.size))
//              || (segment.offset >= s.offset && segment.offset < (s.offset + s.size))) {
//                segments.add(s);
//            }
//        }
//
//        return segments;
//    }

    public static boolean checkRanges(Segment s1, Segment s2) {
        return (s1.offset >= s2.offset && s1.offset < (s2.offset + s2.size)) || (s2.offset >= s1.offset && s2.offset < (s1.offset + s1.size));
    }

<<<<<<< HEAD
=======
    public Segment getSegment(int offset) {
        return this.usedSegments.get(offset);
    }
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
}
