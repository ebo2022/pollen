package gg.moonflower.pollen.api.biome.modification;

import com.mojang.serialization.Codec;
import gg.moonflower.pollen.api.PollenRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.biome.Biome;

import java.util.function.Function;

/**
 * A simple biome modifier that can be loaded from JSON. Mods can register their own serializers using {@link PollenRegistries#BIOME_MODIFIER_SERIALIZERS}.
 *
 * @author ebo2022
 * @since 2.0.0
 */
public interface PollinatedBiomeModifier {

    Codec<PollinatedBiomeModifier> DIRECT_CODEC = PollenRegistries.BIOME_MODIFIER_SERIALIZERS.dispatch(PollinatedBiomeModifier::codec, Function.identity());
    Codec<Holder<PollinatedBiomeModifier>> CODEC = RegistryFileCodec.create(PollenRegistries.BIOME_MODIFIER_KEY, DIRECT_CODEC);
    Codec<HolderSet<PollinatedBiomeModifier>> LIST_CODEC = RegistryCodecs.homogeneousList(PollenRegistries.BIOME_MODIFIER_KEY, DIRECT_CODEC);

    /**
     * @return The codec to serialize this biome modifier
     */
    Codec<? extends PollinatedBiomeModifier> codec();

    /**
     * Applies any changes this modifier makes.
     *
     * @param biome The current biome the modifier is being applied to
     * @param phase The current modification phase
     * @param info  Context to change properties of the biome
     */
    void apply(Holder<Biome> biome, Phase phase, PollinatedBiomeInfo info);

    /**
     * Phases of biome modification.
     *
     * @since 2.0.0
     */
    enum Phase {

        /**
         * Used for adding properties without relying on other info.
         */
        ADDITIONS,

        /**
         * Used for removing properties from biomes.
         */
        REMOVALS,

        /**
         * Used for combinations of additions and removals to replace properties.
         */
        REPLACEMENTS,

        /**
         * Used for wide-reaching post-processing of biome properties.
         */
        POST_PROCESSING
    }
}
