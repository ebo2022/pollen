package gg.moonflower.pollen.api.biome.modifier.modification;

import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.function.BiPredicate;

/**
 * Supplies context for biome modifications to apply their changes.
 *
 * @author ebo2022
 * @since
 */
public interface PollinatedBiomeInfo {

    /**
     * @return Context to modify biome climate data
     */
    Climate getClimate();

    /**
     * @return Context to modify special effects
     */
    SpecialEffects getSpecialEffects();

    /**
     * @return Context to modify biome feature and carver generation
     */
    Generation getGeneration();

    /**
     * @return Context to modify biome spawn settings
     */
    SpawnSettings getSpawnSettings();

    interface Climate {

        Climate setPrecipitation(Biome.Precipitation precipitation);

        Climate setTemperature(float temperature);

        Climate setTemperatureModifier(Biome.TemperatureModifier temperatureModifier);

        Climate setDownfall(float downfall);
    }

    interface SpecialEffects {

        SpecialEffects setFogColor(int color);

        SpecialEffects setWaterColor(int color);

        SpecialEffects setWaterFogColor(int color);

        SpecialEffects setSkyColor(int color);

        SpecialEffects setFoliageColorOverride(int colorOverride);

        SpecialEffects setGrassColorOverride(int colorOverride);

        SpecialEffects setGrassColorModifier(BiomeSpecialEffects.GrassColorModifier modifier);

        SpecialEffects setAmbientParticle(AmbientParticleSettings settings);

        SpecialEffects setAmbientLoopSound(SoundEvent sound);

        SpecialEffects setAmbientMoodSound(AmbientMoodSettings settings);

        SpecialEffects setAmbientAdditionsSound(AmbientAdditionsSettings settings);

        SpecialEffects setBackgroundMusic(Music music);
    }

    interface Generation {

        Generation addFeature(GenerationStep.Decoration decoration, Holder<PlacedFeature> feature);

        Generation addCarver(GenerationStep.Carving carving, Holder<ConfiguredWorldCarver<?>> feature);

        Generation removeFeature(GenerationStep.Decoration decoration, Holder<PlacedFeature> feature);

        Generation removeCarver(GenerationStep.Carving carving, Holder<ConfiguredWorldCarver<?>> feature);
    }

    interface SpawnSettings {

        SpawnSettings setCreatureProbability(float probability);

        SpawnSettings addSpawn(MobCategory category, MobSpawnSettings.SpawnerData data);

        boolean removeSpawns(BiPredicate<MobCategory, MobSpawnSettings.SpawnerData> predicate);

        SpawnSettings setSpawnCost(EntityType<?> entityType, MobSpawnSettings.MobSpawnCost cost);

        SpawnSettings setSpawnCost(EntityType<?> entityType, double mass, double gravityLimit);

        SpawnSettings clearSpawnCost(EntityType<?> entityType);
    }
}