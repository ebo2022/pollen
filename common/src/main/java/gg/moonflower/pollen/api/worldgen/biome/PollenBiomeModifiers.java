package gg.moonflower.pollen.api.worldgen.biome;

import com.mojang.serialization.Codec;
import gg.moonflower.pollen.api.PollenRegistries;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

import java.util.function.Supplier;

/**
 * Built-in biome modifier types included with Pollen.
 *
 * @author ebo2022
 * @since 1.5.0
 */
public final class PollenBiomeModifiers {

    public static final Supplier<Codec<None>> NONE_CODEC = PollenRegistries.BIOME_MODIFIER_SERIALIZERS.register("none", () -> Codec.unit(None.INSTANCE));

    private PollenBiomeModifiers() {
    }

    /**
     * An empty biome modifier intended for overriding other modifiers to disable their effects.
     * @since 1.5.0
     */
    public static class None implements PollinatedBiomeModifier {

        public static final None INSTANCE = new None();

        @Override
        public Codec<? extends PollinatedBiomeModifier> codec() {
            return NONE_CODEC.get();
        }

        // NO-OP
        @Override
        public void applyModifiers(Holder<Biome> biome, ModificationPhase phase, BiomeAttributes attributes) {
        }
    }
}
