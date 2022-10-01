package gg.moonflower.pollen.api.biome.modification.forge;

import gg.moonflower.pollen.api.biome.modification.PollinatedBiomeModifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public interface BiomeModifierHook {

    void pollen_setModifiers(List<PollinatedBiomeModifier> modifiers);
}
