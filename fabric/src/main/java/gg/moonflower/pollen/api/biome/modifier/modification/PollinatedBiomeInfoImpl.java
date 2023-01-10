package gg.moonflower.pollen.api.biome.modifier.modification;

import com.mojang.datafixers.util.Either;
import gg.moonflower.pollen.api.biome.modifier.modification.PollinatedBiomeInfo;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.BiPredicate;

@ApiStatus.Internal
public class PollinatedBiomeInfoImpl implements PollinatedBiomeInfo {

    private final BiomeModificationContext context;
    private final Climate climate;
    private final Generation generation;
    private final SpawnSettings spawnSettings;
    private final SpecialEffects specialEffects;

    public PollinatedBiomeInfoImpl(BiomeModificationContext context) {
        this.context = context;
        this.climate = new ClimateImpl();
        this.generation = new GenerationImpl();
        this.spawnSettings = new SpawnSettingsImpl();
        this.specialEffects = new SpecialEffectsImpl();
    }

    @Override
    public Climate getClimate() {
        return this.climate;
    }

    @Override
    public Generation getGeneration() {
        return this.generation;
    }

    @Override
    public SpawnSettings getSpawnSettings() {
        return this.spawnSettings;
    }

    @Override
    public SpecialEffects getSpecialEffects() {
        return this.specialEffects;
    }

    private class ClimateImpl implements Climate {

        private final BiomeModificationContext.WeatherContext parent = context.getWeather();

        @Override
        public Climate setPrecipitation(Biome.Precipitation precipitation) {
            this.parent.setPrecipitation(precipitation);
            return this;
        }

        @Override
        public Climate setTemperature(float temperature) {
            this.parent.setTemperature(temperature);
            return this;
        }

        @Override
        public Climate setTemperatureModifier(Biome.TemperatureModifier temperatureModifier) {
            this.parent.setTemperatureModifier(temperatureModifier);
            return this;
        }

        @Override
        public Climate setDownfall(float downfall) {
            this.parent.setDownfall(downfall);
            return this;
        }
    }

    private class GenerationImpl implements Generation {

        private final BiomeModificationContext.GenerationSettingsContext parent = context.getGenerationSettings();

        @Override
        public Generation addFeature(GenerationStep.Decoration decoration, Holder<PlacedFeature> feature) {
            Either<ResourceKey<PlacedFeature>, PlacedFeature> value = feature.unwrap();
            if (value.left().isPresent()) {
                this.parent.addFeature(decoration, value.left().get());
            } else {
                this.parent.addBuiltInFeature(decoration, value.right().get());
            }
            return this;
        }

        @Override
        public Generation addCarver(GenerationStep.Carving carving, Holder<ConfiguredWorldCarver<?>> feature) {
            Either<ResourceKey<ConfiguredWorldCarver<?>>, ConfiguredWorldCarver<?>> value = feature.unwrap();
            if (value.left().isPresent()) {
                this.parent.addCarver(carving, value.left().get());
            } else {
                this.parent.addBuiltInCarver(carving, value.right().get());
            }
            return this;
        }

        @Override
        public Generation removeFeature(GenerationStep.Decoration decoration, Holder<PlacedFeature> feature) {
            Either<ResourceKey<PlacedFeature>, PlacedFeature> value = feature.unwrap();
            if (value.left().isPresent()) {
                this.parent.removeFeature(decoration, value.left().get());
            } else {
                this.parent.removeBuiltInFeature(decoration, value.right().get());
            }
            return this;
        }

        @Override
        public Generation removeCarver(GenerationStep.Carving carving, Holder<ConfiguredWorldCarver<?>> feature) {
            Either<ResourceKey<ConfiguredWorldCarver<?>>, ConfiguredWorldCarver<?>> value = feature.unwrap();
            if (value.left().isPresent()) {
                this.parent.removeCarver(carving, value.left().get());
            } else {
                this.parent.removeBuiltInCarver(carving, value.right().get());
            }
            return this;
        }
    }

    public class SpawnSettingsImpl implements SpawnSettings {

        private final BiomeModificationContext.SpawnSettingsContext parent = context.getSpawnSettings();

        @Override
        public SpawnSettings setCreatureProbability(float probability) {
            this.parent.setCreatureSpawnProbability(probability);
            return this;
        }

        @Override
        public SpawnSettings addSpawn(MobCategory category, MobSpawnSettings.SpawnerData data) {
            this.parent.addSpawn(category, data);
            return this;
        }

        @Override
        public boolean removeSpawns(BiPredicate<MobCategory, MobSpawnSettings.SpawnerData> predicate) {
            return this.parent.removeSpawns(predicate);
        }

        @Override
        public SpawnSettings setSpawnCost(EntityType<?> entityType, MobSpawnSettings.MobSpawnCost cost) {
            this.parent.setSpawnCost(entityType, cost.getCharge(), cost.getEnergyBudget());
            return this;
        }

        @Override
        public SpawnSettings setSpawnCost(EntityType<?> entityType, double mass, double gravityLimit) {
            this.parent.setSpawnCost(entityType, mass, gravityLimit);
            return this;
        }

        @Override
        public SpawnSettings clearSpawnCost(EntityType<?> entityType) {
            this.parent.clearSpawnCost(entityType);
            return this;
        }
    }

    public class SpecialEffectsImpl implements SpecialEffects {

        private final BiomeModificationContext.EffectsContext parent = context.getEffects();

        @Override
        public SpecialEffects setFogColor(int color) {
            this.parent.setFogColor(color);
            return this;
        }

        @Override
        public SpecialEffects setWaterColor(int color) {
            this.parent.setWaterColor(color);
            return this;
        }

        @Override
        public SpecialEffects setWaterFogColor(int color) {
            this.parent.setWaterFogColor(color);
            return this;
        }

        @Override
        public SpecialEffects setSkyColor(int color) {
            this.parent.setSkyColor(color);
            return this;
        }

        @Override
        public SpecialEffects setFoliageColorOverride(int colorOverride) {
            this.parent.setFoliageColor(Optional.of(colorOverride));
            return this;
        }

        @Override
        public SpecialEffects setGrassColorOverride(int colorOverride) {
            this.parent.setGrassColor(Optional.of(colorOverride));
            return this;
        }

        @Override
        public SpecialEffects setGrassColorModifier(BiomeSpecialEffects.GrassColorModifier modifier) {
            this.parent.setGrassColorModifier(modifier);
            return this;
        }

        @Override
        public SpecialEffects setAmbientParticle(AmbientParticleSettings settings) {
            this.parent.setParticleConfig(Optional.ofNullable(settings));
            return this;
        }

        @Override
        public SpecialEffects setAmbientLoopSound(SoundEvent sound) {
            this.parent.setAmbientSound(Optional.ofNullable(sound));
            return this;
        }

        @Override
        public SpecialEffects setAmbientMoodSound(AmbientMoodSettings settings) {
            this.parent.setMoodSound(Optional.ofNullable(settings));
            return this;
        }

        @Override
        public SpecialEffects setAmbientAdditionsSound(AmbientAdditionsSettings settings) {
            this.parent.setAdditionsSound(Optional.ofNullable(settings));
            return this;
        }

        @Override
        public SpecialEffects setBackgroundMusic(Music music) {
            this.parent.setMusic(music);
            return this;
        }
    }
}