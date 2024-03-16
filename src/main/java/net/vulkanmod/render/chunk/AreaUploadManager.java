package net.vulkanmod.render.chunk;

<<<<<<< HEAD
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
=======
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.vulkanmod.vulkan.*;
import net.vulkanmod.vulkan.memory.StagingBuffer;
import net.vulkanmod.vulkan.queue.CommandPool;
<<<<<<< HEAD
import static net.vulkanmod.vulkan.queue.Queue.GraphicsQueue;
import static org.lwjgl.vulkan.VK10.*;

import java.nio.ByteBuffer;

=======
import net.vulkanmod.vulkan.queue.Queue;
import net.vulkanmod.vulkan.queue.TransferQueue;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkMemoryBarrier;

import java.nio.ByteBuffer;

import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_TRANSFER_BIT;

>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
public class AreaUploadManager {
    public static final int FRAME_NUM = 2;
    public static AreaUploadManager INSTANCE;

    public static void createInstance() {
        INSTANCE = new AreaUploadManager();
    }

<<<<<<< HEAD
    private final ObjectArrayList<AreaBuffer.Segment>[] recordedUploads = new ObjectArrayList[FRAME_NUM];
    private final CommandPool.CommandBuffer[] commandBuffers = new CommandPool.CommandBuffer[FRAME_NUM];

    Long2ObjectArrayMap< ObjectArrayFIFOQueue<SubCopyCommand>> dstBuffers = new Long2ObjectArrayMap<>(8);


=======
    Queue queue = DeviceManager.getTransferQueue();

    ObjectArrayList<AreaBuffer.Segment>[] recordedUploads;
    CommandPool.CommandBuffer[] commandBuffers;

    LongOpenHashSet dstBuffers = new LongOpenHashSet();
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

    int currentFrame;

    public void init() {
<<<<<<< HEAD
=======
        this.commandBuffers = new CommandPool.CommandBuffer[FRAME_NUM];
        this.recordedUploads = new ObjectArrayList[FRAME_NUM];
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

        for (int i = 0; i < FRAME_NUM; i++) {
            this.recordedUploads[i] = new ObjectArrayList<>();
        }
    }

<<<<<<< HEAD

    public void swapBuffers(long srcBuffer, long dstBuffer)
    {
        if(!dstBuffers.containsKey(srcBuffer)) return;// throw new RuntimeException("NOBuffer");
        dstBuffers.put(dstBuffer,dstBuffers.remove(srcBuffer));
    }

    public void submitUploads() {
        if(dstBuffers.isEmpty()) return;
        if(this.recordedUploads[this.currentFrame].isEmpty()) {
            return;
        }
        if(commandBuffers[currentFrame] == null)
        {
            this.commandBuffers[currentFrame] = GraphicsQueue.beginCommands();
//            GraphicsQueue.GigaBarrier(this.commandBuffers[currentFrame].getHandle(), VK_PIPELINE_STAGE_VERTEX_INPUT_BIT, VK_PIPELINE_STAGE_TRANSFER_BIT);
        }
        //Using Graphics Queue as uploads are used immediately + is recommended by AMD: https://gpuopen.com/learn/rdna-performance-guide/#copying
        GraphicsQueue.MultiBufferBarriers(this.commandBuffers[currentFrame].getHandle(),
                dstBuffers.keySet(),
                VK_ACCESS_INDEX_READ_BIT,
                0,
                VK_PIPELINE_STAGE_VERTEX_INPUT_BIT,
                VK_PIPELINE_STAGE_TRANSFER_BIT);


           

        GraphicsQueue.uploadBufferCmds(this.commandBuffers[currentFrame], Vulkan.getStagingBuffer().getId(), dstBuffers.long2ObjectEntrySet());
            
        


        dstBuffers.clear();
        
//        GraphicsQueue.GigaBarrier(this.commandBuffers[currentFrame].getHandle());
        GraphicsQueue.submitCommands(this.commandBuffers[currentFrame]);
    }

    public void uploadAsync(AreaBuffer.Segment uploadSegment, long bufferId, int dstOffset, int bufferSize, ByteBuffer src) {




        StagingBuffer stagingBuffer = Vulkan.getStagingBuffer();
        stagingBuffer.copyBuffer(bufferSize, src);



        if(!dstBuffers.containsKey(bufferId))
        {
            dstBuffers.put(bufferId, new ObjectArrayFIFOQueue<>(32));
        }
        dstBuffers.get(bufferId).enqueue(new SubCopyCommand(stagingBuffer.getOffset(), dstOffset, bufferSize));
=======
    public synchronized void submitUploads() {
        if(this.recordedUploads[this.currentFrame].isEmpty())
            return;

        queue.submitCommands(this.commandBuffers[currentFrame]);
    }

    public void uploadAsync(AreaBuffer.Segment uploadSegment, long bufferId, long dstOffset, long bufferSize, ByteBuffer src) {

        if(commandBuffers[currentFrame] == null)
            this.commandBuffers[currentFrame] = queue.beginCommands();

        VkCommandBuffer commandBuffer = commandBuffers[currentFrame].getHandle();

        StagingBuffer stagingBuffer = Vulkan.getStagingBuffer();
        stagingBuffer.copyBuffer((int) bufferSize, src);

        if(!dstBuffers.add(bufferId)) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                VkMemoryBarrier.Buffer barrier = VkMemoryBarrier.calloc(1, stack);
                barrier.sType$Default();
                barrier.srcAccessMask(VK_ACCESS_TRANSFER_WRITE_BIT);
                barrier.dstAccessMask(VK_ACCESS_TRANSFER_WRITE_BIT);

                vkCmdPipelineBarrier(commandBuffer,
                        VK_PIPELINE_STAGE_TRANSFER_BIT, VK_PIPELINE_STAGE_TRANSFER_BIT,
                        0,
                        barrier,
                        null,
                        null);
            }

            dstBuffers.clear();
        }

        TransferQueue.uploadBufferCmd(commandBuffer, stagingBuffer.getId(), stagingBuffer.getOffset(), bufferId, dstOffset, bufferSize);

>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
        this.recordedUploads[this.currentFrame].add(uploadSegment);
    }

    public void updateFrame() {
        this.currentFrame = (this.currentFrame + 1) % FRAME_NUM;
        waitUploads(this.currentFrame);

        this.dstBuffers.clear();
    }

    private void waitUploads(int frame) {
        CommandPool.CommandBuffer commandBuffer = commandBuffers[frame];
        if(commandBuffer == null)
            return;
        Synchronization.waitFence(commandBuffers[frame].getFence());

        for(AreaBuffer.Segment uploadSegment : this.recordedUploads[frame]) {
            uploadSegment.setReady();
        }

        this.commandBuffers[frame].reset();
        this.commandBuffers[frame] = null;
        this.recordedUploads[frame].clear();
    }

    public synchronized void waitAllUploads() {
        for(int i = 0; i < this.commandBuffers.length; ++i) {
            waitUploads(i);
        }
    }

}
