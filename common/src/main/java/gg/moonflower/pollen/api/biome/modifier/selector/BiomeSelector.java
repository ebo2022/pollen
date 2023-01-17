package gg.moonflower.pollen.api.biome.modifier.selector;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A predicate that determines what biomes a biome modifier should apply to. The result is used differently depending on the modifier's {@link BiomeSelectionMode}.
 *
 * @author ebo2022
 * @since
 */
public interface BiomeSelector extends Predicate<BiomeSelector.Context> {

    Codec<BiomeSelector> CODEC = BiomeSelectorTypes.REGISTRY.dispatch(BiomeSelector::getType, Function.identity());

    /**
     * Used to check if a biome modifier can be applied in conjunction with its selection mode.
     *
     * @param context Context for the current biome being checked
     * @return Whether modifications should be applied given the context
     */
    @Override
    boolean test(Context context);

    /**
     * @return The type of this biome selector
     */
    Codec<? extends BiomeSelector> getType();

    /**
     * Context used during the selection process of biome modifiers.
     *
     * @since
     */
    interface Context {

        /**
         * @return The key of the current biome
         */
        ResourceLocation getBiomeKey();

        /**
         * Checks if the specified structure can generate in the current biome.
         *
         * @param structure The structure to check
         * @return Whether the structure can generate in the biome
         */
        boolean hasStructure(ResourceKey<ConfiguredStructureFeature<?, ?>> structure);

        /**
         * Checks if the current biome generates in the specified dimension.
         *
         * @param dimension The dimension to check
         * @return Whether the biome can spawn in the dimention
         */
        boolean generatesIn(ResourceKey<LevelStem> dimension);
    }
}