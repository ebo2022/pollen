package gg.moonflower.pollen.api.biome.modifier;

import com.mojang.serialization.Codec;
import gg.moonflower.pollen.api.biome.modifier.selector.BiomeSelector;
import net.minecraft.util.StringRepresentable;

import java.util.List;
import java.util.function.BiPredicate;

/**
 * Determines how a biome modifier should handle results from its selectors.
 *
 * @author ebo2022
 * @since
 */
public enum BiomeSelectionMode implements StringRepresentable, BiPredicate<List<BiomeSelector>, BiomeSelector.Context> {

    /**
     * The biome modifier will apply if any selector is true.
     */
    ANY_TRUE("any_true", (selectors, context) -> {
        for (BiomeSelector selector : selectors)
            if (selector.test(context))
                return true;
        return false;
    }),

    /**
     * The biome modifier will apply if all selectors are true.
     */
    ALL_TRUE("all_true", (selectors, context) -> {
        for (BiomeSelector selector : selectors)
            if (!selector.test(context))
                return false;
        return true;
    });

    private final BiPredicate<List<BiomeSelector>, BiomeSelector.Context> predicate;
    private final String name;
    public static final Codec<BiomeSelectionMode> CODEC = StringRepresentable.fromEnum(BiomeSelectionMode::values);

    BiomeSelectionMode(String name, BiPredicate<List<BiomeSelector>, BiomeSelector.Context> predicate) {
        this.name = name;
        this.predicate = predicate;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    @Override
    public boolean test(List<BiomeSelector> biomeSelectors, BiomeSelector.Context context) {
        return this.predicate.test(biomeSelectors, context);
    }
}
