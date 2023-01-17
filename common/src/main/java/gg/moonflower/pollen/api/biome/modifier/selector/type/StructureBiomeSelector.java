package gg.moonflower.pollen.api.biome.modifier.selector.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.moonflower.pollen.api.biome.modifier.selector.BiomeSelector;
import gg.moonflower.pollen.api.biome.modifier.selector.BiomeSelectorTypes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public record StructureBiomeSelector(ResourceKey<ConfiguredStructureFeature<?, ?>> structureKey) implements BiomeSelector {

    public static final Codec<StructureBiomeSelector> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            ResourceKey.codec(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY).fieldOf("structure").forGetter(StructureBiomeSelector::structureKey)
    ).apply(builder, StructureBiomeSelector::new));

    @Override
    public boolean test(Context context) {
        return context.hasStructure(this.structureKey);
    }

    @Override
    public Codec<StructureBiomeSelector> getType() {
        return BiomeSelectorTypes.STRUCTURE.get();
    }
}