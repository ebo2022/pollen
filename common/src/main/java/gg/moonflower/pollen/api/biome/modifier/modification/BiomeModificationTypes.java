package gg.moonflower.pollen.api.biome.modifier.modification;

import com.mojang.serialization.Codec;
import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.resources.ResourceLocation;

/**
 * Default biome modification types included with Pollen.
 * <p>Mods can add their own serializers by creating a {@link PollinatedRegistry} that registers to {@link #REGISTRY}.
 *
 * @author ebo2022
 * @since
 */
public final class BiomeModificationTypes {

    public static final PollinatedRegistry<Codec<? extends BiomeModification>> REGISTRY = PollinatedRegistry.createSimple(new ResourceLocation(Pollen.MOD_ID, "biome_modification_type"));

    private BiomeModificationTypes() {
    }
}
