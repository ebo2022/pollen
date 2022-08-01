package gg.moonflower.pollen.core.worldgen.biome;

import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ImmutableClimateSettings implements ClimateSettings {
    protected final Biome.ClimateSettings climateSettings;

    public ImmutableClimateSettings(Biome biome) {
        this(BiomeHooks.extractClimateSettings(biome));
    }

    public ImmutableClimateSettings(Biome.ClimateSettings climateSettings) {
        this.climateSettings = climateSettings;
    }

    @Override
    public Biome.Precipitation getPrecipitation() {
        return climateSettings.precipitation();
    }

    @Override
    public float getTemperature() {
        return climateSettings.temperature();
    }

    @Override
    public Biome.TemperatureModifier getTemperatureModifier() {
        return climateSettings.temperatureModifier();
    }

    @Override
    public float getDownfall() {
        return climateSettings.downfall();
    }
}
