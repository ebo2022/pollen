package gg.moonflower.pollen.api.registry.resource;

import net.minecraft.resources.ResourceLocation;

/**
 * Various ids for default reload listeners.
 *
 * @author ebo2022
 * @see PollinatedPreparableReloadListener
 * @since
 */
public final class ReloadListenerKeys {

    private ReloadListenerKeys() {
    }

    // client
    public static final ResourceLocation SOUNDS = new ResourceLocation("minecraft:sounds");
    public static final ResourceLocation FONTS = new ResourceLocation("minecraft:fonts");
    public static final ResourceLocation MODELS = new ResourceLocation("minecraft:models");
    public static final ResourceLocation LANGUAGES = new ResourceLocation("minecraft:languages");
    public static final ResourceLocation TEXTURES = new ResourceLocation("minecraft:textures");

    // server
    public static final ResourceLocation TAGS = new ResourceLocation("minecraft:tags");
    public static final ResourceLocation RECIPES = new ResourceLocation("minecraft:recipes");
    public static final ResourceLocation ADVANCEMENTS = new ResourceLocation("minecraft:advancements");
    public static final ResourceLocation FUNCTIONS = new ResourceLocation("minecraft:functions");
    public static final ResourceLocation LOOT_TABLES = new ResourceLocation("minecraft:loot_tables");
}
