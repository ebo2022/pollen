package gg.moonflower.pollen.api;

import com.mojang.serialization.Codec;
import gg.moonflower.pollen.api.biome.modification.PollinatedBiomeModifier;
import gg.moonflower.pollen.api.entity.PollinatedBoatType;
import gg.moonflower.pollen.api.registry.PollinatedDataRegistry;
import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * @since 1.4.0
 */
public final class PollenRegistries {

    public static final PollinatedRegistry<PollinatedBoatType> BOAT_TYPE_REGISTRY = PollinatedRegistry.createSimple(new ResourceLocation(Pollen.MOD_ID, "boat_type"));
    public static final PollinatedRegistry<Codec<? extends PollinatedBiomeModifier>> BIOME_MODIFIER_SERIALIZERS = PollinatedRegistry.createSimple(new ResourceLocation(Pollen.MOD_ID, "biome_modifier_serializers"));
    public static final ResourceKey<? extends Registry<PollinatedBiomeModifier>> BIOME_MODIFIER_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Pollen.MOD_ID, "biome_modifiers"));
    public static final PollinatedDataRegistry<PollinatedBiomeModifier> BIOME_MODIFIERS = PollinatedDataRegistry.create(BIOME_MODIFIER_KEY, PollinatedBiomeModifier.DIRECT_CODEC);

    private PollenRegistries(){
    }
}
