package net.vulkanmod.mixin.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import net.minecraft.client.renderer.ShaderInstance;
import net.vulkanmod.interfaces.ShaderMixed;
import net.vulkanmod.vulkan.Renderer;
import net.vulkanmod.vulkan.shader.GraphicsPipeline;
<<<<<<< HEAD
=======
import net.vulkanmod.vulkan.shader.Pipeline;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BufferUploader.class)
public class BufferUploaderM {

    /**
     * @author
     */
    @Overwrite
    public static void reset() {}

    /**
     * @author
     */
    @Overwrite
    public static void drawWithShader(BufferBuilder.RenderedBuffer buffer) {
        RenderSystem.assertOnRenderThread();
        buffer.release();

        BufferBuilder.DrawState parameters = buffer.drawState();

        Renderer renderer = Renderer.getInstance();
<<<<<<< HEAD
        //TODO: maybe manage suballocs per pipeline + skip uploads if size+contents is the exact same
=======

>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
        if(parameters.vertexCount() <= 0)
            return;

        ShaderInstance shaderInstance = RenderSystem.getShader();
        //Used to update legacy shader uniforms
        //TODO it would be faster to allocate a buffer from stack and set all values
        shaderInstance.apply();

        GraphicsPipeline pipeline = ((ShaderMixed)(shaderInstance)).getPipeline();
<<<<<<< HEAD
        boolean x = renderer.bindGraphicsPipeline(pipeline);
        {
            renderer.uploadAndBindUBOs(pipeline, x);
        }
=======
        renderer.bindGraphicsPipeline(pipeline);
        renderer.uploadAndBindUBOs(pipeline);
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
        Renderer.getDrawer().draw(buffer.vertexBuffer(), parameters.mode(), parameters.format(), parameters.vertexCount());
    }

}
