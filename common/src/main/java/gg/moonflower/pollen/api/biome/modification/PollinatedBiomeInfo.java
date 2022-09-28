package gg.moonflower.pollen.api.biome.modification;

import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;

/**
 * Context to modify properties of a biome using a {@link PollinatedBiomeModifier}.
 *
 * @author ebo2022
 * @since 2.0.0
 */
public interface PollinatedBiomeInfo {

    /**
     * @return Context to modify the biome's climate settings
     */
    Climate climate();

    /**
     * @return Context to modify the biome's generation
     */
    Generation generation();

    /**
     * @return Context to modify the biome's spawn settings
     */
    Spawns spawns();

    /**
     * @return Context to modify the biome's special effects
     */
    SpecialEffects specialEffects();

    /**
     * Used to modify climate properties of a biome.
     *
     * @since 2.0.0
     */
    interface Climate {

        /**
         * Sets the type of precipitation that should fall in the biome.
         *
         * @param precipitation The precipitation type
         */
        Climate setPrecipitation(Biome.Precipitation precipitation);

        /**
         * Sets the temperature of the biome.
         *
         * @param temperature The new biome temperature
         */
        Climate setTemperature(float temperature);

        /**
         * Sets the biome's temperature modifier.
         *
         * @param temperatureModifier The new temperature modifier
         */
        Climate setTemperatureModifier(Biome.TemperatureModifier temperatureModifier);

        /**
         * Sets the downfall of the biome.
         *
         * @param downfall The new downfall amount
         */
        Climate setDownfall(float downfall);
    }

    /**
     * Used to modify generation properties of a biome.
     *
     * @since 2.0.0
     */
    interface Generation {

        Generation addFeature(GenerationStep.Decoration decoration, Holder<PlacedFeature> feature);

        Generation addCarver(GenerationStep.Carving carving, Holder<ConfiguredWorldCarver<?>> feature);

        Generation removeFeature(GenerationStep.Decoration decoration, Holder<PlacedFeature> feature);

        Generation removeCarver(GenerationStep.Carving carving, Holder<ConfiguredWorldCarver<?>> feature);
    }

    /**
     * Used to modify mob spawning properties of a biome.
     *
     * @since 2.0.0
     */
    interface Spawns {

        Spawns setCreatureProbability(float probability);

        Spawns addSpawn(MobCategory category, MobSpawnSettings.SpawnerData data);

        boolean removeSpawns(BiPredicate<MobCategory, MobSpawnSettings.SpawnerData> predicate);

        Spawns setSpawnCost(EntityType<?> entityType, MobSpawnSettings.MobSpawnCost cost);

        Spawns setSpawnCost(EntityType<?> entityType, double mass, double gravityLimit);

        Spawns clearSpawnCost(EntityType<?> entityType);
    }

    /**
     * Used to modify biome special effects.
     *
     * @since 2.0.0
     */
    interface SpecialEffects {

        SpecialEffects setFogColor(int color);

        SpecialEffects setWaterColor(int color);

        SpecialEffects setWaterFogColor(int color);

        SpecialEffects setSkyColor(int color);

        SpecialEffects setFoliageColorOverride(@Nullable Integer colorOverride);

        SpecialEffects setGrassColorOverride(@Nullable Integer colorOverride);

        SpecialEffects setGrassColorModifier(BiomeSpecialEffects.GrassColorModifier modifier);

        SpecialEffects setAmbientParticle(@Nullable AmbientParticleSettings settings);

        SpecialEffects setAmbientLoopSound(@Nullable SoundEvent sound);

        SpecialEffects setAmbientMoodSound(@Nullable AmbientMoodSettings settings);

        SpecialEffects setAmbientAdditionsSound(@Nullable AmbientAdditionsSettings settings);

        SpecialEffects setBackgroundMusic(@Nullable Music music);
    }
}
