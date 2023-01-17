package gg.moonflower.pollen.api.biome.modifier.selector.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.moonflower.pollen.api.biome.modifier.selector.BiomeSelector;
import gg.moonflower.pollen.api.biome.modifier.selector.BiomeSelectorTypes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;

public record DimensionBiomeSelector(ResourceKey<LevelStem> dimensionKey) implements BiomeSelector {

    public static final Codec<DimensionBiomeSelector> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            ResourceKey.codec(Registry.LEVEL_STEM_REGISTRY).fieldOf("dimension").forGetter(DimensionBiomeSelector::dimensionKey)
    ).apply(builder, DimensionBiomeSelector::new));

    @Override
    public boolean test(Context context) {
        return context.generatesIn(this.dimensionKey);
    }

    @Override
    public Codec<DimensionBiomeSelector> getType() {
        return BiomeSelectorTypes.DIMENSION.get();
    }
}