package net.vulkanmod.mixin.texture;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.resources.ResourceLocation;
<<<<<<< HEAD
import net.vulkanmod.Initializer;
=======
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
import net.vulkanmod.render.texture.SpriteUtil;
import net.vulkanmod.vulkan.DeviceManager;
import net.vulkanmod.vulkan.Renderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(TextureManager.class)
public abstract class MTextureManager {


    @Shadow @Final private Set<Tickable> tickableTextures;


    @Shadow public abstract AbstractTexture getTexture(ResourceLocation resourceLocation, AbstractTexture abstractTexture);

    /**
     * @author
     */
    @Overwrite
    public void tick() {
<<<<<<< HEAD
        if(Renderer.skipRendering|| !Initializer.CONFIG.animations)
=======
        if(Renderer.skipRendering)
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
            return;

        //Debug D
        if(SpriteUtil.shouldUpload())
            DeviceManager.getGraphicsQueue().startRecording();
        for (Tickable tickable : this.tickableTextures) {
            tickable.tick();
        }
        if(SpriteUtil.shouldUpload()) {
            SpriteUtil.transitionLayouts(DeviceManager.getGraphicsQueue().getCommandBuffer().getHandle());
            DeviceManager.getGraphicsQueue().endRecordingAndSubmit();
//            Synchronization.INSTANCE.waitFences();
        }
    }

    /**
     * @author
     */
    @Overwrite
    public void release(ResourceLocation id) {
        AbstractTexture abstractTexture = this.getTexture(id, MissingTextureAtlasSprite.getTexture());
        if (abstractTexture != MissingTextureAtlasSprite.getTexture()) {
            //TODO: delete
            abstractTexture.releaseId();
        }
    }
}
