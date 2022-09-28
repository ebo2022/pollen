package gg.moonflower.pollen.api.biome.modification;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.moonflower.pollen.api.PollenRegistries;
import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

/**
 * Default biome modifier types included with Pollen.
 *
 * @author ebo2022
 * @since 2.0.0
 */
public final class PollenBiomeModifiers {

    @ApiStatus.Internal
    public static final PollinatedRegistry<Codec<? extends PollinatedBiomeModifier>> DEFAULT_SERIALIZERS = PollinatedRegistry.create(PollenRegistries.BIOME_MODIFIER_SERIALIZERS, Pollen.MOD_ID);

    private PollenBiomeModifiers() {
    }

    public static final Supplier<Codec<AddFeaturesModifier>> ADD_FEATURES_CODEC = DEFAULT_SERIALIZERS.register("add_features", () ->
            RecordCodecBuilder.create(builder -> builder.group(
                    Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddFeaturesModifier::biomes),
                    PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(AddFeaturesModifier::features),
                    GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(AddFeaturesModifier::step)
            ).apply(builder, AddFeaturesModifier::new))
    );

    public record AddFeaturesModifier(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, GenerationStep.Decoration step) implements PollinatedBiomeModifier {

        @Override
        public void apply(Holder<Biome> biome, Phase phase, PollinatedBiomeInfo info) {
            if (phase == Phase.ADDITIONS && this.biomes.contains(biome)) {
                PollinatedBiomeInfo.Generation generationSettings = info.generation();
                this.features.forEach(holder -> generationSettings.addFeature(this.step, holder));
            }
        }

        @Override
        public Codec<? extends PollinatedBiomeModifier> codec() {
            return ADD_FEATURES_CODEC.get();
        }
    }
}
