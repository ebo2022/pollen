package gg.moonflower.pollen.api.biome.modifier.selector;

import gg.moonflower.pollen.api.biome.modifier.selector.BiomeSelector;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class BiomeSelectorContextImpl implements BiomeSelector.Context {

    private final BiomeSelectionContext parent;

    public BiomeSelectorContextImpl(BiomeSelectionContext parent) {
        this.parent = parent;
    }

    @Override
    public ResourceKey<Biome> getBiomeKey() {
        return this.parent.getBiomeKey();
    }

    @Override
    public Holder<Biome> getBiome() {
        return this.parent.getBiomeRegistryEntry();
    }

    @Override
    public boolean hasStructure(ResourceKey<Structure> structure) {
        return this.parent.validForStructure(structure);
    }

    @Override
    public boolean generatesIn(ResourceKey<LevelStem> dimension) {
        return this.parent.canGenerateIn(dimension);
    }
}
