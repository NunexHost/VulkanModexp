package net.vulkanmod.vulkan;

import net.vulkanmod.vulkan.framebuffer.SwapChain;
import net.vulkanmod.vulkan.memory.Buffer;
import net.vulkanmod.vulkan.memory.MemoryManager;
<<<<<<< HEAD

import net.vulkanmod.vulkan.memory.StagingBuffer;
import net.vulkanmod.vulkan.queue.QueueFamilyIndices;
=======
import net.vulkanmod.vulkan.memory.MemoryTypes;
import net.vulkanmod.vulkan.memory.StagingBuffer;
import net.vulkanmod.vulkan.queue.Queue;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
import net.vulkanmod.vulkan.shader.Pipeline;
import net.vulkanmod.vulkan.texture.VulkanImage;
import net.vulkanmod.vulkan.util.VUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.VmaAllocatorCreateInfo;
import org.lwjgl.util.vma.VmaVulkanFunctions;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
<<<<<<< HEAD
=======
import static net.vulkanmod.vulkan.queue.Queue.getQueueFamilies;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
import static net.vulkanmod.vulkan.util.VUtil.asPointerBuffer;
import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.system.MemoryStack.stackGet;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.util.vma.Vma.vmaCreateAllocator;
import static org.lwjgl.util.vma.Vma.vmaDestroyAllocator;
import static org.lwjgl.vulkan.EXTDebugUtils.*;
import static org.lwjgl.vulkan.KHRDynamicRendering.VK_KHR_DYNAMIC_RENDERING_EXTENSION_NAME;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.*;
<<<<<<< HEAD
import static org.lwjgl.vulkan.VK13.VK_API_VERSION_1_3;

public class Vulkan {
    public static final boolean ENABLE_VALIDATION_LAYERS = false;
    public static final boolean DYNAMIC_RENDERING = false;
    public static final Set<String> VALIDATION_LAYERS;

    // public static final boolean ENABLE_VALIDATION_LAYERS = true;
    // public static final boolean DYNAMIC_RENDERING = true;

    static {
        if (ENABLE_VALIDATION_LAYERS) {
            VALIDATION_LAYERS = new HashSet<>();
            VALIDATION_LAYERS.add("VK_LAYER_KHRONOS_validation");
            // VALIDATION_LAYERS.add("VK_LAYER_KHRONOS_synchronization2");
=======
import static org.lwjgl.vulkan.VK12.VK_API_VERSION_1_2;

public class Vulkan {

    public static final boolean ENABLE_VALIDATION_LAYERS = false;
//    public static final boolean ENABLE_VALIDATION_LAYERS = true;

//    public static final boolean DYNAMIC_RENDERING = true;
    public static final boolean DYNAMIC_RENDERING = false;

    public static final Set<String> VALIDATION_LAYERS;
    static {
        if(ENABLE_VALIDATION_LAYERS) {
            VALIDATION_LAYERS = new HashSet<>();
            VALIDATION_LAYERS.add("VK_LAYER_KHRONOS_validation");
//            VALIDATION_LAYERS.add("VK_LAYER_KHRONOS_synchronization2");

>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
        } else {
            // We are not going to use it, so we don't create it
            VALIDATION_LAYERS = null;
        }
    }

    static final Set<String> REQUIRED_EXTENSION = DYNAMIC_RENDERING ? Stream.of(
            VK_KHR_SWAPCHAIN_EXTENSION_NAME, VK_KHR_DYNAMIC_RENDERING_EXTENSION_NAME)
            .collect(toSet())
            : Stream.of(
                    VK_KHR_SWAPCHAIN_EXTENSION_NAME)
            .collect(toSet());

    private static int debugCallback(int messageSeverity, int messageType, long pCallbackData, long pUserData) {
<<<<<<< HEAD
        VkDebugUtilsMessengerCallbackDataEXT callbackData = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);

        String m;
        if ((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT) != 0) {
            m = "\u001B[31m" + callbackData.pMessageString();
            // System.err.println("Stack dump:");
            // Thread.dumpStack();
        } else {
            m = callbackData.pMessageString();
        }

        System.err.println(m);
        if ((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT) != 0)
=======

        VkDebugUtilsMessengerCallbackDataEXT callbackData = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);

        String s;
        if((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT) != 0) {
            s = "\u001B[31m" + callbackData.pMessageString();

//            System.err.println("Stack dump:");
//            Thread.dumpStack();
        } else {
            s = callbackData.pMessageString();
        }

        System.err.println(s);

        if((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT) != 0)
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
            System.nanoTime();

        return VK_FALSE;
    }

<<<<<<< HEAD
    private static int createDebugUtilsMessengerEXT(
        VkInstance instance,
        VkDebugUtilsMessengerCreateInfoEXT createInfo,
        VkAllocationCallbacks allocationCallbacks,
        LongBuffer pDebugMessenger
    ) {
        if (vkGetInstanceProcAddr(instance, "vkCreateDebugUtilsMessengerEXT") != NULL) {
            return vkCreateDebugUtilsMessengerEXT(instance, createInfo, allocationCallbacks, pDebugMessenger);
        }
        return VK_ERROR_EXTENSION_NOT_PRESENT;
    }

    private static void destroyDebugUtilsMessengerEXT(
        VkInstance instance, long debugMessenger,
        VkAllocationCallbacks allocationCallbacks
    ) {
        if (vkGetInstanceProcAddr(instance, "vkDestroyDebugUtilsMessengerEXT") != NULL) {
            vkDestroyDebugUtilsMessengerEXT(instance, debugMessenger, allocationCallbacks);
        }
=======
    private static int createDebugUtilsMessengerEXT(VkInstance instance, VkDebugUtilsMessengerCreateInfoEXT createInfo,
                                                    VkAllocationCallbacks allocationCallbacks, LongBuffer pDebugMessenger) {

        if(vkGetInstanceProcAddr(instance, "vkCreateDebugUtilsMessengerEXT") != NULL) {
            return vkCreateDebugUtilsMessengerEXT(instance, createInfo, allocationCallbacks, pDebugMessenger);
        }

        return VK_ERROR_EXTENSION_NOT_PRESENT;
    }

    private static void destroyDebugUtilsMessengerEXT(VkInstance instance, long debugMessenger, VkAllocationCallbacks allocationCallbacks) {

        if(vkGetInstanceProcAddr(instance, "vkDestroyDebugUtilsMessengerEXT") != NULL) {
            vkDestroyDebugUtilsMessengerEXT(instance, debugMessenger, allocationCallbacks);
        }

>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
    }

    public static VkDevice getDevice() {
        return DeviceManager.device;
    }

    public static long getAllocator() {
        return allocator;
    }

    public static long window;

    private static VkInstance instance;
    private static long debugMessenger;
    private static long surface;

    private static SwapChain swapChain;

    private static long commandPool;
    private static VkCommandBuffer immediateCmdBuffer;
    private static long immediateFence;

    private static long allocator;

    private static StagingBuffer[] stagingBuffers;

    public static boolean use24BitsDepthFormat = true;
    private static int DEFAULT_DEPTH_FORMAT = 0;

    public static void initVulkan(long window) {
<<<<<<< HEAD
        System.out.println("Initializing Vulkan!");
        
=======
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
        createInstance();
        setupDebugMessenger();
        createSurface(window);

        DeviceManager.pickPhysicalDevice(instance);
        DeviceManager.createLogicalDevice();

        createVma();
<<<<<<< HEAD
        // MemoryTypes.createMemoryTypes();
=======
        MemoryTypes.createMemoryTypes();
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

        createCommandPool();
        allocateImmediateCmdBuffer();

        setupDepthFormat();
        createSwapChain();
        Renderer.initRenderer();
<<<<<<< HEAD
    }

    static void createStagingBuffers() {
        if (stagingBuffers != null)
            freeStagingBuffers();

        stagingBuffers = new StagingBuffer[Renderer.getFramesNum()];
        for (int i = 0; i < stagingBuffers.length; ++i) {
=======

    }

    static void createStagingBuffers() {
        if(stagingBuffers != null) {
            freeStagingBuffers();
        }

        stagingBuffers = new StagingBuffer[Renderer.getFramesNum()];

        for(int i = 0; i < stagingBuffers.length; ++i) {
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
            stagingBuffers[i] = new StagingBuffer(30 * 1024 * 1024);
        }
    }

    static void setupDepthFormat() {
        DEFAULT_DEPTH_FORMAT = DeviceManager.findDepthFormat(use24BitsDepthFormat);
    }

    private static void createSwapChain() {
        swapChain = new SwapChain();
    }

    public static void recreateSwapChain() {
        swapChain.recreateSwapChain();
    }

    public static void waitIdle() {
        vkDeviceWaitIdle(DeviceManager.device);
    }

    public static void cleanUp() {
        vkDeviceWaitIdle(DeviceManager.device);
        vkDestroyCommandPool(DeviceManager.device, commandPool, null);
        vkDestroyFence(DeviceManager.device, immediateFence, null);

        Pipeline.destroyPipelineCache();

        Renderer.getInstance().cleanUpResources();
        swapChain.cleanUp();

        freeStagingBuffers();

        try {
            MemoryManager.getInstance().freeAllBuffers();
        } catch (Exception e) {
            e.printStackTrace();
        }

        vmaDestroyAllocator(allocator);

        DeviceManager.destroy();
        destroyDebugUtilsMessengerEXT(instance, debugMessenger, null);
        KHRSurface.vkDestroySurfaceKHR(instance, surface, null);
        vkDestroyInstance(instance, null);
    }

    private static void freeStagingBuffers() {
        Arrays.stream(stagingBuffers).forEach(Buffer::freeBuffer);
    }

    private static void createInstance() {
<<<<<<< HEAD
        if (ENABLE_VALIDATION_LAYERS && !checkValidationLayerSupport())
            throw new RuntimeException("Validation requested but not supported");

        try (MemoryStack stack = stackPush()) {
            // Use calloc to initialize the structs with 0s. Otherwise, the program can crash due to random values
=======

        if(ENABLE_VALIDATION_LAYERS && !checkValidationLayerSupport()) {
            throw new RuntimeException("Validation requested but not supported");
        }

        try(MemoryStack stack = stackPush()) {

            // Use calloc to initialize the structs with 0s. Otherwise, the program can crash due to random values

>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
            VkApplicationInfo appInfo = VkApplicationInfo.calloc(stack);

            appInfo.sType(VK_STRUCTURE_TYPE_APPLICATION_INFO);
            appInfo.pApplicationName(stack.UTF8Safe("VulkanMod"));
            appInfo.applicationVersion(VK_MAKE_VERSION(1, 0, 0));
            appInfo.pEngineName(stack.UTF8Safe("VulkanMod Engine"));
            appInfo.engineVersion(VK_MAKE_VERSION(1, 0, 0));
<<<<<<< HEAD
            appInfo.apiVersion(VK_API_VERSION_1_3);
            
            VkInstanceCreateInfo createInfo = VkInstanceCreateInfo.calloc(stack);
=======
            appInfo.apiVersion(VK_API_VERSION_1_2);

            VkInstanceCreateInfo createInfo = VkInstanceCreateInfo.calloc(stack);

>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
            createInfo.sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO);
            createInfo.pApplicationInfo(appInfo);
            // enabledExtensionCount is implicitly set when you call ppEnabledExtensionNames
            createInfo.ppEnabledExtensionNames(getRequiredExtensions());

<<<<<<< HEAD
            if (ENABLE_VALIDATION_LAYERS) {
=======
            if(ENABLE_VALIDATION_LAYERS) {

>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
                createInfo.ppEnabledLayerNames(asPointerBuffer(VALIDATION_LAYERS));

                VkDebugUtilsMessengerCreateInfoEXT debugCreateInfo = VkDebugUtilsMessengerCreateInfoEXT.calloc(stack);
                populateDebugMessengerCreateInfo(debugCreateInfo);
                createInfo.pNext(debugCreateInfo.address());
            }

            // We need to retrieve the pointer of the created instance
            PointerBuffer instancePtr = stack.mallocPointer(1);

<<<<<<< HEAD
            if (vkCreateInstance(createInfo, null, instancePtr) != VK_SUCCESS)
                throw new RuntimeException("Failed to create instance");
=======
            if(vkCreateInstance(createInfo, null, instancePtr) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create instance");
            }
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

            instance = new VkInstance(instancePtr.get(0), createInfo);
        }
    }

    static boolean checkValidationLayerSupport() {
<<<<<<< HEAD
        try (MemoryStack stack = stackPush()) {
=======

        try(MemoryStack stack = stackPush()) {

>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
            IntBuffer layerCount = stack.ints(0);

            vkEnumerateInstanceLayerProperties(layerCount, null);

            VkLayerProperties.Buffer availableLayers = VkLayerProperties.malloc(layerCount.get(0), stack);

            vkEnumerateInstanceLayerProperties(layerCount, availableLayers);

            Set<String> availableLayerNames = availableLayers.stream()
                    .map(VkLayerProperties::layerNameString)
                    .collect(toSet());

            return availableLayerNames.containsAll(Vulkan.VALIDATION_LAYERS);
        }
    }

    private static void populateDebugMessengerCreateInfo(VkDebugUtilsMessengerCreateInfoEXT debugCreateInfo) {
        debugCreateInfo.sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT);
<<<<<<< HEAD
        // debugCreateInfo.messageSeverity(VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT);
        debugCreateInfo.messageSeverity(VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT);
        debugCreateInfo.messageType(VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT);
        // debugCreateInfo.messageType(VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT);
        debugCreateInfo.pfnUserCallback(Vulkan::debugCallback);
    }
    
    private static void setupDebugMessenger() {
        if (!ENABLE_VALIDATION_LAYERS) return;

        try (MemoryStack stack = stackPush()) {
=======
//        debugCreateInfo.messageSeverity(VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT);
        debugCreateInfo.messageSeverity(VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT);
        debugCreateInfo.messageType(VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT);
//        debugCreateInfo.messageType(VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT);
        debugCreateInfo.pfnUserCallback(Vulkan::debugCallback);
    }

    private static void setupDebugMessenger() {

        if(!ENABLE_VALIDATION_LAYERS) {
            return;
        }

        try(MemoryStack stack = stackPush()) {

>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
            VkDebugUtilsMessengerCreateInfoEXT createInfo = VkDebugUtilsMessengerCreateInfoEXT.calloc(stack);

            populateDebugMessengerCreateInfo(createInfo);

            LongBuffer pDebugMessenger = stack.longs(VK_NULL_HANDLE);

<<<<<<< HEAD
            if (createDebugUtilsMessengerEXT(instance, createInfo, null, pDebugMessenger) != VK_SUCCESS)
                throw new RuntimeException("Failed to set up debug messenger");
=======
            if(createDebugUtilsMessengerEXT(instance, createInfo, null, pDebugMessenger) != VK_SUCCESS) {
                throw new RuntimeException("Failed to set up debug messenger");
            }
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

            debugMessenger = pDebugMessenger.get(0);
        }
    }

    private static void createSurface(long handle) {
        window = handle;

<<<<<<< HEAD
        try (MemoryStack stack = stackPush()) {
            LongBuffer pSurface = stack.longs(VK_NULL_HANDLE);

            if (glfwCreateWindowSurface(instance, window, null, pSurface) != VK_SUCCESS)
                throw new RuntimeException("Failed to create window surface");
            
=======
        try(MemoryStack stack = stackPush()) {

            LongBuffer pSurface = stack.longs(VK_NULL_HANDLE);

            if(glfwCreateWindowSurface(instance, window, null, pSurface) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create window surface");
            }

>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
            surface = pSurface.get(0);
        }
    }

    private static void createVma() {
<<<<<<< HEAD
        try (MemoryStack stack = stackPush()) {
=======
        try(MemoryStack stack = stackPush()) {

>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
            VmaVulkanFunctions vulkanFunctions = VmaVulkanFunctions.calloc(stack);
            vulkanFunctions.set(instance, DeviceManager.device);

            VmaAllocatorCreateInfo allocatorCreateInfo = VmaAllocatorCreateInfo.calloc(stack);
            allocatorCreateInfo.physicalDevice(DeviceManager.physicalDevice);
            allocatorCreateInfo.device(DeviceManager.device);
            allocatorCreateInfo.pVulkanFunctions(vulkanFunctions);
            allocatorCreateInfo.instance(instance);
<<<<<<< HEAD
            allocatorCreateInfo.vulkanApiVersion(VK_API_VERSION_1_3);

            PointerBuffer pAllocator = stack.pointers(VK_NULL_HANDLE);

            if (vmaCreateAllocator(allocatorCreateInfo, pAllocator) != VK_SUCCESS)
                throw new RuntimeException("Failed to create command pool");
=======
            allocatorCreateInfo.vulkanApiVersion(VK_API_VERSION_1_2);

            PointerBuffer pAllocator = stack.pointers(VK_NULL_HANDLE);

            if (vmaCreateAllocator(allocatorCreateInfo, pAllocator) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create command pool");
            }
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

            allocator = pAllocator.get(0);
        }
    }

    private static void createCommandPool() {
<<<<<<< HEAD
        try (MemoryStack stack = stackPush()) {
            VkCommandPoolCreateInfo poolInfo = VkCommandPoolCreateInfo.calloc(stack);
            poolInfo.sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO);
            poolInfo.queueFamilyIndex(QueueFamilyIndices.graphicsFamily);
            poolInfo.flags(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT);

            LongBuffer pCommandPool = stack.mallocLong(1);
            if (vkCreateCommandPool(DeviceManager.device, poolInfo, null, pCommandPool) != VK_SUCCESS)
                throw new RuntimeException("Failed to create command pool");
=======

        try(MemoryStack stack = stackPush()) {

            Queue.QueueFamilyIndices queueFamilyIndices = getQueueFamilies();

            VkCommandPoolCreateInfo poolInfo = VkCommandPoolCreateInfo.calloc(stack);
            poolInfo.sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO);
            poolInfo.queueFamilyIndex(queueFamilyIndices.graphicsFamily);
            poolInfo.flags(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT);

            LongBuffer pCommandPool = stack.mallocLong(1);

            if (vkCreateCommandPool(DeviceManager.device, poolInfo, null, pCommandPool) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create command pool");
            }
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

            commandPool = pCommandPool.get(0);
        }
    }

    private static void allocateImmediateCmdBuffer() {
<<<<<<< HEAD
        try (MemoryStack stack = stackPush()) {
=======
        try(MemoryStack stack = stackPush()) {

>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
            VkCommandBufferAllocateInfo allocInfo = VkCommandBufferAllocateInfo.calloc(stack);
            allocInfo.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO);
            allocInfo.level(VK_COMMAND_BUFFER_LEVEL_PRIMARY);
            allocInfo.commandPool(commandPool);
            allocInfo.commandBufferCount(1);

            PointerBuffer pCommandBuffer = stack.mallocPointer(1);
            vkAllocateCommandBuffers(DeviceManager.device, allocInfo, pCommandBuffer);
            immediateCmdBuffer = new VkCommandBuffer(pCommandBuffer.get(0), DeviceManager.device);

            VkFenceCreateInfo fenceInfo = VkFenceCreateInfo.calloc(stack);
            fenceInfo.sType(VK_STRUCTURE_TYPE_FENCE_CREATE_INFO);
            fenceInfo.flags(VK_FENCE_CREATE_SIGNALED_BIT);

            LongBuffer pFence = stack.mallocLong(1);
            vkCreateFence(DeviceManager.device, fenceInfo, null, pFence);
            vkResetFences(DeviceManager.device,  pFence.get(0));

            immediateFence = pFence.get(0);
        }
    }

    public static VkCommandBuffer beginImmediateCmd() {
        try (MemoryStack stack = stackPush()) {
            VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc(stack);
            beginInfo.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);

            vkBeginCommandBuffer(immediateCmdBuffer, beginInfo);
        }
        return immediateCmdBuffer;
    }

    public static void endImmediateCmd() {
        try (MemoryStack stack = stackPush()) {
            vkEndCommandBuffer(immediateCmdBuffer);

            VkSubmitInfo submitInfo = VkSubmitInfo.calloc(stack);
            submitInfo.sType(VK_STRUCTURE_TYPE_SUBMIT_INFO);
            submitInfo.pCommandBuffers(stack.pointers(immediateCmdBuffer));

            vkQueueSubmit(DeviceManager.getGraphicsQueue().queue(), submitInfo, immediateFence);

            vkWaitForFences(DeviceManager.device, immediateFence, true, VUtil.UINT64_MAX);
            vkResetFences(DeviceManager.device, immediateFence);
            vkResetCommandBuffer(immediateCmdBuffer, 0);
        }
<<<<<<< HEAD
    }

    private static PointerBuffer getRequiredExtensions() {
        PointerBuffer glfwExtensions = glfwGetRequiredInstanceExtensions();

        if (ENABLE_VALIDATION_LAYERS) {
            MemoryStack stack = stackGet();
=======

    }

    private static PointerBuffer getRequiredExtensions() {

        PointerBuffer glfwExtensions = glfwGetRequiredInstanceExtensions();

        if(ENABLE_VALIDATION_LAYERS) {

            MemoryStack stack = stackGet();

>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
            PointerBuffer extensions = stack.mallocPointer(glfwExtensions.capacity() + 1);

            extensions.put(glfwExtensions);
            extensions.put(stack.UTF8(VK_EXT_DEBUG_UTILS_EXTENSION_NAME));

            // Rewind the buffer before returning it to reset its position back to 0
            return extensions.rewind();
        }

        return glfwExtensions;
    }

    public static void setVsync(boolean b) {
<<<<<<< HEAD
        if (swapChain.isVsync() != b) {
=======
        if(swapChain.isVsync() != b) {
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
            Renderer.scheduleSwapChainUpdate();
            swapChain.setVsync(b);
        }
    }

    public static int getDefaultDepthFormat() {
        return DEFAULT_DEPTH_FORMAT;
    }

<<<<<<< HEAD
    public static long getSurface() {
        return surface;
    }

    public static SwapChain getSwapChain() {
        return swapChain;
    }

    public static VkExtent2D getSwapchainExtent() {
        return swapChain.getExtent();
    }

    public static List<VulkanImage> getSwapChainImages() {
        return swapChain.getImages();
    }

    public static long getCommandPool() {
        return commandPool;
    }

    public static StagingBuffer getStagingBuffer() {
        return stagingBuffers[Renderer.getCurrentFrame()];
    }

    public static DeviceInfo getDeviceInfo() {
        return DeviceManager.deviceInfo;
    }
=======
    public static long getSurface() { return surface; }

    public static SwapChain getSwapChain() { return swapChain; }

    public static VkExtent2D getSwapchainExtent()
    {
        return swapChain.getExtent();
    }

    public static List<VulkanImage> getSwapChainImages() { return swapChain.getImages(); }

    public static long getCommandPool()
    {
        return commandPool;
    }

    public static StagingBuffer getStagingBuffer() { return stagingBuffers[Renderer.getCurrentFrame()]; }

    public static DeviceInfo getDeviceInfo() { return DeviceManager.deviceInfo; }
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
}

