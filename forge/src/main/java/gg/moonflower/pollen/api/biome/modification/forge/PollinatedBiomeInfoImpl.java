package gg.moonflower.pollen.api.biome.modification.forge;

import gg.moonflower.pollen.api.biome.modification.PollinatedBiomeInfo;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiPredicate;

@ApiStatus.Internal
public class PollinatedBiomeInfoImpl implements PollinatedBiomeInfo {

    private final ModifiableBiomeInfo.BiomeInfo.Builder info;
    private final Climate climate;
    private final Generation generation;
    private final Spawns spawns;
    private final SpecialEffects specialEffects;

    public PollinatedBiomeInfoImpl(ModifiableBiomeInfo.BiomeInfo.Builder info) {
        this.info = info;
        this.climate = new ClimateImpl();
        this.generation = new GenerationImpl();
        this.spawns = new SpawnsImpl();
        this.specialEffects = new SpecialEffectsImpl();
    }

    @Override
    public Climate climate() {
        return this.climate;
    }

    @Override
    public Generation generation() {
        return this.generation;
    }

    @Override
    public Spawns spawns() {
        return this.spawns;
    }

    @Override
    public SpecialEffects specialEffects() {
        return this.specialEffects;
    }

    private class ClimateImpl implements Climate {

        private final ClimateSettingsBuilder parent = info.getClimateSettings();

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

        private final BiomeGenerationSettingsBuilder parent = info.getGenerationSettings();

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

    private class SpawnsImpl implements Spawns {

        private final MobSpawnSettingsBuilder parent = info.getMobSpawnSettings();

        @Override
        public Spawns setCreatureProbability(float probability) {
            this.parent.creatureGenerationProbability(probability);
            return this;
        }

        @Override
        public Spawns addSpawn(MobCategory category, MobSpawnSettings.SpawnerData data) {
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
        public Spawns setSpawnCost(EntityType<?> entityType, MobSpawnSettings.MobSpawnCost cost) {
            this.parent.addMobCharge(entityType, cost.getCharge(), cost.getEnergyBudget());
            return this;
        }

        @Override
        public Spawns setSpawnCost(EntityType<?> entityType, double mass, double gravityLimit) {
            this.parent.addMobCharge(entityType, mass, gravityLimit);
            return this;
        }

        @Override
        public Spawns clearSpawnCost(EntityType<?> entityType) {
            this.parent.mobSpawnCosts.remove(entityType);
            return this;
        }
    }

    private class SpecialEffectsImpl implements SpecialEffects {

        private final BiomeSpecialEffectsBuilder parent = info.getSpecialEffects();

        @Override
        public SpecialEffects setFogColor(int color) {
            this.parent.fogColor(color);
            return this;
        }

        @Override
        public SpecialEffects setWaterColor(int color) {
            this.parent.waterColor(color);
            return this;
        }

        @Override
        public SpecialEffects setWaterFogColor(int color) {
            this.parent.waterFogColor(color);
            return this;
        }

        @Override
        public SpecialEffects setSkyColor(int color) {
            this.parent.skyColor(color);
            return this;
        }

        @Override
        public SpecialEffects setFoliageColorOverride(@Nullable Integer colorOverride) {
            this.parent.foliageColorOverride = Optional.ofNullable(colorOverride);
            return this;
        }

        @Override
        public SpecialEffects setGrassColorOverride(@Nullable Integer colorOverride) {
            this.parent.grassColorOverride = Optional.ofNullable(colorOverride);
            return this;
        }

        @Override
        public SpecialEffects setGrassColorModifier(BiomeSpecialEffects.GrassColorModifier modifier) {
            this.parent.grassColorModifier(modifier);
            return this;
        }

        @Override
        public SpecialEffects setAmbientParticle(@Nullable AmbientParticleSettings settings) {
            this.parent.ambientParticle = Optional.ofNullable(settings);
            return this;
        }

        @Override
        public SpecialEffects setAmbientLoopSound(@Nullable SoundEvent sound) {
            this.parent.ambientLoopSoundEvent = Optional.ofNullable(sound);
            return this;
        }

        @Override
        public SpecialEffects setAmbientMoodSound(@Nullable AmbientMoodSettings settings) {
            this.parent.ambientMoodSettings = Optional.ofNullable(settings);
            return this;
        }

        @Override
        public SpecialEffects setAmbientAdditionsSound(@Nullable AmbientAdditionsSettings settings) {
            this.parent.ambientAdditionsSettings = Optional.ofNullable(settings);
            return this;
        }

        @Override
        public SpecialEffects setBackgroundMusic(@Nullable Music music) {
            this.parent.backgroundMusic = Optional.ofNullable(music);
            return this;
        }
    }
 }
