package gg.moonflower.pollen.api.biome.modifier.selector.fabric;

import gg.moonflower.pollen.api.biome.modifier.selector.BiomeSelector;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class BiomeSelectorContextImpl implements BiomeSelector.Context {

    private final BiomeSelectionContext parent;

    public BiomeSelectorContextImpl(BiomeSelectionContext parent) {
        this.parent = parent;
    }

    @Override
    public ResourceLocation getBiomeKey() {
        return this.parent.getBiomeKey().location();
    }

    @Override
    public boolean hasStructure(ResourceKey<ConfiguredStructureFeature<?, ?>> structure) {
        return this.parent.validForStructure(structure);
    }

    @Override
    public boolean generatesIn(ResourceKey<LevelStem> dimension) {
        return this.parent.canGenerateIn(dimension);
    }
}