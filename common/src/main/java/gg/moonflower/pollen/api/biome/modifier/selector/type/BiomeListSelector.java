package gg.moonflower.pollen.api.biome.modifier.selector.type;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.EitherCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.moonflower.pollen.api.biome.modifier.selector.BiomeSelector;
import gg.moonflower.pollen.api.biome.modifier.selector.BiomeSelectorTypes;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public record BiomeListSelector(HolderSet<Biome> biomes) implements BiomeSelector {

    public static final Codec<BiomeListSelector> CODEC = RecordCodecBuilder.create(builder -> builder.group(
           Biome.LIST_CODEC.fieldOf("biomes").forGetter(BiomeListSelector::biomes)
    ).apply(builder, BiomeListSelector::new));

    @Override
    public boolean test(Context context) {
        return this.biomes.contains(context.getBiome());
    }

    @Override
    public Codec<BiomeListSelector> getType() {
        return BiomeSelectorTypes.BIOME_LIST.get();
    }
}