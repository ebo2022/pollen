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
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.PrimaryLevelData;

import java.util.Optional;

public class BiomeSelectorContextImpl implements BiomeSelector.Context {

    private final Holder<Biome> biome;

    public BiomeSelectorContextImpl(Holder<Biome> biome) {
        this.biome = biome;
    }

    @Override
    public ResourceKey<Biome> getBiomeKey() {
        return this.biome.unwrapKey().orElseThrow();
    }

    @Override
    public Holder<Biome> getBiome() {
        return this.biome;
    }

    @Override
    public boolean hasStructure(ResourceKey<Structure> structure) {
        Optional<RegistryAccess> registryAccess = Platform.getRegistryAccess();
        if (registryAccess.isPresent()) {
            Structure structureInstance = registryAccess.get().registryOrThrow(Registry.STRUCTURE_REGISTRY).get(structure);
            return structureInstance.biomes().contains(this.getBiome());
        }
        return false;
    }

    @Override
    public boolean generatesIn(ResourceKey<LevelStem> dimension) {
        Optional<MinecraftServer> server = Platform.getRunningServer();
        if (server.isPresent()) {
            if (server.get().getWorldData() instanceof PrimaryLevelData levelData) {
                LevelStem levelStem = levelData.worldGenSettings().dimensions().get(dimension);
                if (levelStem == null)
                    return false;
                return levelStem.generator().getBiomeSource().possibleBiomes().stream().anyMatch(holder -> holder == biome);
            }
            return false;
        }
        return false;
    }
}
