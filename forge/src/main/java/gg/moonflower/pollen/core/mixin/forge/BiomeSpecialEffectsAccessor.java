package gg.moonflower.pollen.core.mixin.forge;

import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(BiomeSpecialEffects.class)
public interface BiomeSpecialEffectsAccessor {

    @Accessor("fogColor")
    void setFogColor(int fogColor);

    @Accessor("waterColor")
    void setWaterColor(int waterColor);

    @Accessor("waterFogColor")
    void setWaterFogColor(int waterFogColor);

    @Accessor("skyColor")
    void setSkyColor(int skyColor);

    @Accessor("foliageColorOverride")
    void setFoliageColorOverride(Optional<Integer> foliageColorOverride);

    @Accessor("grassColorOverride")
    void setGrassColorOverride(Optional<Integer> grassColorOverride);

    @Accessor("grassColorModifier")
    void setGrassColorModifier(BiomeSpecialEffects.GrassColorModifier grassColorModifier);

    @Accessor("ambientParticleSettings")
    void setAmbientParticleSettings(Optional<AmbientParticleSettings> ambientParticleSettings);

    @Accessor("ambientLoopSoundEvent")
    void setAmbientLoopSoundEvent(Optional<SoundEvent> ambientLoopSoundEvent);

    @Accessor("ambientMoodSettings")
    void setAmbientMoodSettings(Optional<AmbientMoodSettings> ambientMoodSettings);

    @Accessor("ambientAdditionsSettings")
    void setAmbientAdditionsSettings(Optional<AmbientAdditionsSettings> ambientAdditionsSettings);

    @Accessor("backgroundMusic")
    void setBackgroundMusic(Optional<Music> backgroundMusic);
}
