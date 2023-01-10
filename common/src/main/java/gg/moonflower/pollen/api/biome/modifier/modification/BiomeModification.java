package gg.moonflower.pollen.api.biome.modifier.modification;

import com.mojang.serialization.Codec;
import gg.moonflower.pollen.api.biome.modifier.BiomeModifierManager;
import gg.moonflower.pollen.api.biome.modifier.ModifierApplicationPhase;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An action a biome modifier can take to modify its selected biomes. It is only applied if the selection mode and given selectors allow for it to do so.
 *
 * @author ebo2022
 * @since
 */
public interface BiomeModification extends Consumer<PollinatedBiomeInfo> {

    Codec<BiomeModification> CODEC = BiomeModificationTypes.REGISTRY.dispatch(BiomeModification::getType, Function.identity());

    /**
     * Applies any changes that should be made to the current biome.
     *
     * @param context Context to modify the biome
     */
    @Override
    void accept(PollinatedBiomeInfo context);

    /**
     * @return The phase at which the modifier should be applied at. This depends on what the modifier does to the biome
     */
    ModifierApplicationPhase getApplicationPhase();

    /**
     * @return The type of this biome modification
     */
    Codec<? extends BiomeModification> getType();
}
