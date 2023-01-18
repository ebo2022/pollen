package gg.moonflower.pollen.api.biome.modifier.modification.forge;

import gg.moonflower.pollen.api.biome.modifier.modification.PollinatedBiomeInfo;
import gg.moonflower.pollen.core.mixin.forge.BiomeSpecialEffectsAccessor;
import gg.moonflower.pollen.core.mixin.forge.MobSpawnSettingsBuilderAccessor;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.BiPredicate;

@ApiStatus.Internal
public class PollinatedBiomeInfoImpl implements PollinatedBiomeInfo {

    private final BiomeLoadingEvent event;
    private final Climate climate;
    private final Generation generation;
    private final SpawnSettings spawns;
    private final SpecialEffects specialEffects;

    public PollinatedBiomeInfoImpl(BiomeLoadingEvent event) {
        this.event = event;
        this.climate = new ClimateImpl();
        this.generation = new GenerationImpl();
        this.spawns = new SpawnSettingsImpl();
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
        return this.spawns;
    }

    @Override
    public SpecialEffects getSpecialEffects() {
        return this.specialEffects;
    }

    private class ClimateImpl implements Climate {

        private boolean hasBeenModified;
        private final Biome.ClimateSettings originalClimateSettings = event.getClimate();
        public Biome.Precipitation precipitation = originalClimateSettings.precipitation;
        public float temperature = originalClimateSettings.temperature;
        public Biome.TemperatureModifier temperatureModifier = originalClimateSettings.temperatureModifier;
        public float downfall = originalClimateSettings.downfall;

        @Override
        public Climate setPrecipitation(Biome.Precipitation precipitation) {
            this.hasBeenModified = true;
            this.precipitation = precipitation;
            return this;
        }

        @Override
        public Climate setTemperature(float temperature) {
            this.hasBeenModified = true;
            this.temperature = temperature;
            return this;
        }

        @Override
        public Climate setTemperatureModifier(Biome.TemperatureModifier temperatureModifier) {
            this.hasBeenModified = true;
            this.temperatureModifier = temperatureModifier;
            return this;
        }

        @Override
        public Climate setDownfall(float downfall) {
            this.hasBeenModified = true;
            this.downfall = downfall;
            return this;
        }
    }

    private class GenerationImpl implements Generation {

        private final BiomeGenerationSettingsBuilder parent = event.getGeneration();

        @Override
        public Generation addFeature(GenerationStep.Decoration decoration, Holder<PlacedFeature> feature) {
            this.parent.addFeature(decoration, feature);
            return this;
        }

        @Override
        public Generation addCarver(GenerationStep.Carving carving, Holder<ConfiguredWorldCarver<?>> feature) {
            this.parent.addCarver(carving, feature);
            return this;
        }

        @Override
        public Generation removeFeature(GenerationStep.Decoration decoration, Holder<PlacedFeature> feature) {
            this.parent.getFeatures(decoration).removeIf(holder -> holder == feature);
            return this;
        }

        @Override
        public Generation removeCarver(GenerationStep.Carving carving, Holder<ConfiguredWorldCarver<?>> feature) {
            this.parent.getCarvers(carving).removeIf(holder -> holder == feature);
            return this;
        }
    }

    private class SpawnSettingsImpl implements SpawnSettings {

        private final MobSpawnSettingsBuilder parent = event.getSpawns();

        @Override
        public SpawnSettings setCreatureProbability(float probability) {
            this.parent.creatureGenerationProbability(probability);
            return this;
        }

        @Override
        public SpawnSettings addSpawn(MobCategory category, MobSpawnSettings.SpawnerData data) {
            this.parent.addSpawn(category, data);
            return this;
        }

        @Override
        public boolean removeSpawns(BiPredicate<MobCategory, MobSpawnSettings.SpawnerData> predicate) {
            boolean removed = false;
            for (MobCategory type : this.parent.getSpawnerTypes()) {
                if (this.parent.getSpawner(type).removeIf(data -> predicate.test(type, data))) {
                    removed = true;
                }
            }
            return removed;
        }

        @Override
        public SpawnSettings setSpawnCost(EntityType<?> entityType, MobSpawnSettings.MobSpawnCost cost) {
            this.parent.addMobCharge(entityType, cost.getCharge(), cost.getEnergyBudget());
            return this;
        }

        @Override
        public SpawnSettings setSpawnCost(EntityType<?> entityType, double mass, double gravityLimit) {
            this.parent.addMobCharge(entityType, mass, gravityLimit);
            return this;
        }

        @Override
        public SpawnSettings clearSpawnCost(EntityType<?> entityType) {
            ((MobSpawnSettingsBuilderAccessor) this.parent).getMobSpawnCosts().remove(entityType);
            return this;
        }
    }

    private class SpecialEffectsImpl implements SpecialEffects {

        private final BiomeSpecialEffects parent = event.getEffects();

        @Override
        public SpecialEffects setFogColor(int color) {
            ((BiomeSpecialEffectsAccessor) this.parent).setFogColor(color);
            return this;
        }

        @Override
        public SpecialEffects setWaterColor(int color) {
            ((BiomeSpecialEffectsAccessor) this.parent).setWaterColor(color);
            return this;
        }

        @Override
        public SpecialEffects setWaterFogColor(int color) {
            ((BiomeSpecialEffectsAccessor) this.parent).setWaterFogColor(color);
            return this;
        }

        @Override
        public SpecialEffects setSkyColor(int color) {
            ((BiomeSpecialEffectsAccessor) this.parent).setSkyColor(color);
            return this;
        }

        @Override
        public SpecialEffects setFoliageColorOverride(int colorOverride) {
            ((BiomeSpecialEffectsAccessor) this.parent).setFoliageColorOverride(Optional.of(colorOverride));
            return this;
        }

        @Override
        public SpecialEffects setGrassColorOverride(int colorOverride) {
            ((BiomeSpecialEffectsAccessor) this.parent).setGrassColorOverride(Optional.of(colorOverride));
            return this;
        }

        @Override
        public SpecialEffects setGrassColorModifier(BiomeSpecialEffects.GrassColorModifier modifier) {
            ((BiomeSpecialEffectsAccessor) this.parent).setGrassColorModifier(modifier);
            return this;
        }

        @Override
        public SpecialEffects setAmbientParticle(AmbientParticleSettings settings) {
            ((BiomeSpecialEffectsAccessor) this.parent).setAmbientParticleSettings(Optional.of(settings));
            return this;
        }

        @Override
        public SpecialEffects setAmbientLoopSound(SoundEvent sound) {
            ((BiomeSpecialEffectsAccessor) this.parent).setAmbientLoopSoundEvent(Optional.of(sound));
            return this;
        }

        @Override
        public SpecialEffects setAmbientMoodSound(AmbientMoodSettings settings) {
            ((BiomeSpecialEffectsAccessor) this.parent).setAmbientMoodSettings(Optional.of(settings));
            return this;
        }

        @Override
        public SpecialEffects setAmbientAdditionsSound(AmbientAdditionsSettings settings) {
            ((BiomeSpecialEffectsAccessor) this.parent).setAmbientAdditionsSettings(Optional.of(settings));
            return this;
        }

        @Override
        public SpecialEffects setBackgroundMusic(Music music) {
            ((BiomeSpecialEffectsAccessor) this.parent).setBackgroundMusic(Optional.of(music));
            return this;
        }
    }
 }