package gg.moonflower.pollen.api.biome.modifier.selector.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.moonflower.pollen.api.biome.modifier.selector.BiomeSelector;
import gg.moonflower.pollen.api.biome.modifier.selector.BiomeSelectorTypes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public record StructureBiomeSelector(ResourceKey<Structure> structureKey) implements BiomeSelector {

    public static final Codec<StructureBiomeSelector> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            ResourceKey.codec(Registry.STRUCTURE_REGISTRY).fieldOf("structure").forGetter(StructureBiomeSelector::structureKey)
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
