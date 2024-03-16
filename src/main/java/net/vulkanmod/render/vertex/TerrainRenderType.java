package net.vulkanmod.render.vertex;

import net.minecraft.client.renderer.RenderType;
<<<<<<< HEAD
=======
import net.vulkanmod.interfaces.ExtendedRenderType;
import net.vulkanmod.vulkan.VRenderSystem;
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74

import java.util.EnumSet;

public enum TerrainRenderType {
<<<<<<< HEAD
    SOLID(RenderType.solid(), 262144 /*BIG_BUFFER_SIZE*/),
    CUTOUT_MIPPED(RenderType.cutoutMipped(), 262144 /*MEDIUM_BUFFER_SIZE*/),
    CUTOUT(RenderType.cutout(), 131072 /*SMALL_BUFFER_SIZE*/),
    TRANSLUCENT(RenderType.translucent(), 131072 /*SMALL_BUFFER_SIZE*/),
    TRIPWIRE(RenderType.tripwire(), 131072 /*SMALL_BUFFER_SIZE*/);

    public static final TerrainRenderType[] VALUES = TerrainRenderType.values();

    public static final EnumSet<TerrainRenderType> ALL_RENDER_TYPES = EnumSet.of(CUTOUT_MIPPED, CUTOUT, TRANSLUCENT);

    public final int bufferSize;
    public final int initialSize;

    TerrainRenderType(RenderType renderType, int initialSize) {
        this.bufferSize = renderType.bufferSize();
        this.initialSize = initialSize;
    }
    public static EnumSet<TerrainRenderType> getActiveLayers() {
        return ALL_RENDER_TYPES;
    }

    public static TerrainRenderType getCompact(String renderType) {
        return switch (renderType)
        {
            case "solid", "cutout_mipped" -> CUTOUT_MIPPED;
            case "cutout" -> CUTOUT;
            default -> TRANSLUCENT;
        };


    }



    public static TerrainRenderType get(String renderType) {
        return switch (renderType)
        {
            case "solid" -> SOLID;
            case "cutout_mipped" -> CUTOUT_MIPPED;
            case "cutout" -> CUTOUT;
            case "tripwire" -> TRIPWIRE;
            default -> TRANSLUCENT;
        };
    }

    public int bitMask() {
        return 1 << this.ordinal();
=======
    SOLID(0.0f, 262144 /*BIG_BUFFER_SIZE*/),
    CUTOUT_MIPPED(0.5f, 262144 /*MEDIUM_BUFFER_SIZE*/),
    CUTOUT(0.1f, 131072 /*SMALL_BUFFER_SIZE*/),
    TRANSLUCENT(0.0f, 131072 /*SMALL_BUFFER_SIZE*/),
    TRIPWIRE(0.1f, 131072 /*SMALL_BUFFER_SIZE*/);

    public static final TerrainRenderType[] VALUES = TerrainRenderType.values();

    public static final EnumSet<TerrainRenderType> COMPACT_RENDER_TYPES = EnumSet.of(CUTOUT_MIPPED, TRANSLUCENT);
    public static final EnumSet<TerrainRenderType> SEMI_COMPACT_RENDER_TYPES = EnumSet.of(CUTOUT_MIPPED, CUTOUT, TRANSLUCENT);

    final float alphaCutout;
    public final int initialSize;

    TerrainRenderType(float alphaCutout, int initialSize) {
        this.alphaCutout = alphaCutout;
        this.initialSize = initialSize;
    }

    public void setCutoutUniform() {
        VRenderSystem.alphaCutout = this.alphaCutout;
    }

    public static TerrainRenderType get(RenderType renderType) {
        return ((ExtendedRenderType)renderType).getTerrainRenderType();
    }

    public static TerrainRenderType get(String name) {
        return switch (name) {
            case "solid" -> TerrainRenderType.SOLID;
            case "cutout" -> TerrainRenderType.CUTOUT;
            case "cutout_mipped" -> TerrainRenderType.CUTOUT_MIPPED;
            case "translucent" -> TerrainRenderType.TRANSLUCENT;
            case "tripwire" -> TerrainRenderType.TRIPWIRE;
            default -> null;
        };
    }

    public static RenderType getRenderType(TerrainRenderType renderType) {
        return switch (renderType) {
            case SOLID -> RenderType.solid();
            case CUTOUT -> RenderType.cutout();
            case CUTOUT_MIPPED -> RenderType.cutoutMipped();
            case TRANSLUCENT -> RenderType.translucent();
            case TRIPWIRE -> RenderType.tripwire();
        };
>>>>>>> f02a3979439dc5076424a7a907ca614b95849e74
    }
}
