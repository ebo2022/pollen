package gg.moonflower.pollen.api.biome.modifier.selector;

import com.mojang.serialization.Codec;
import gg.moonflower.pollen.api.biome.modifier.selector.BiomeSelector;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.GenerationStep;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public static final Codec<BiomeSelectionMode> CODEC = StringRepresentable.fromEnum(BiomeSelectionMode::values, BiomeSelectionMode::byName);
    private static final Map<String, BiomeSelectionMode> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(BiomeSelectionMode::getSerializedName, Function.identity()));
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

    @Nullable
    public static BiomeSelectionMode byName(String string) {
        return BY_NAME.get(string);
    }
}