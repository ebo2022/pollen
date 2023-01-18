package gg.moonflower.pollen.api.biome.modifier.selector.forge;

import gg.moonflower.pollen.api.biome.modifier.selector.BiomeSelector;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.Optional;

public class BiomeSelectorContextImpl implements BiomeSelector.Context {

    private final ResourceKey<Biome> biomeKey;
    private final Holder<Biome> biomeHolder;
    private final RegistryAccess registryAccess;

    public BiomeSelectorContextImpl(BiomeLoadingEvent event) {
        this.registryAccess = Platform.getRegistryAccess().orElseThrow();
        // the event's name shouldn't be null given it was already checked before this was constructed
        this.biomeKey = ResourceKey.create(Registry.BIOME_REGISTRY, event.getName());
        this.biomeHolder = this.registryAccess.registryOrThrow(Registry.BIOME_REGISTRY).getHolderOrThrow(this.biomeKey);
    }

    @Override
    public ResourceKey<Biome> getBiomeKey() {
        return this.biomeKey;
    }

    @Override
    public Holder<Biome> getBiome() {
        return this.biomeHolder;
    }

    @Override
    public boolean hasStructure(ResourceKey<ConfiguredStructureFeature<?, ?>> structure) {
        ConfiguredStructureFeature<?, ?> structureInstance = this.registryAccess.registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY).get(structure);
        return structureInstance.biomes().contains(this.getBiome());
    }

    @Override
    public boolean generatesIn(ResourceKey<LevelStem> dimension) {
        MinecraftServer server = Platform.getRunningServer().orElseThrow();
        if (server.getWorldData() instanceof PrimaryLevelData levelData) {
            LevelStem levelStem = levelData.worldGenSettings().dimensions().get(dimension);
            if (levelStem == null)
                return false;
            return levelStem.generator().getBiomeSource().possibleBiomes().stream().anyMatch(holder -> holder == biomeHolder);
        }
        return false;
    }
}