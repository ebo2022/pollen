package gg.moonflower.pollen.api.biome.modifier.selector;

import com.mojang.serialization.Codec;
import gg.moonflower.pollen.api.biome.modifier.selector.type.BiomeListSelector;
import gg.moonflower.pollen.api.biome.modifier.selector.type.DimensionBiomeSelector;
import gg.moonflower.pollen.api.biome.modifier.selector.type.StructureBiomeSelector;
import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public final class BiomeSelectorTypes {

    public static final PollinatedRegistry<Codec<? extends BiomeSelector>> REGISTRY = PollinatedRegistry.createSimple(new ResourceLocation(Pollen.MOD_ID, "biome_selector_type"));

    public static final Supplier<Codec<BiomeListSelector>> BIOME_LIST = REGISTRY.register("biomes", () -> BiomeListSelector.CODEC);
    public static final Supplier<Codec<DimensionBiomeSelector>> DIMENSION = REGISTRY.register("spawns_in_dimension", () -> DimensionBiomeSelector.CODEC);
    public static final Supplier<Codec<StructureBiomeSelector>> STRUCTURE = REGISTRY.register("has_structure", () -> StructureBiomeSelector.CODEC);

    private BiomeSelectorTypes() {
    }
}