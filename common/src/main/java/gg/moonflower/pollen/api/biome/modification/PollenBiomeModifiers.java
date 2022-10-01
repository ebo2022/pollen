package gg.moonflower.pollen.api.biome.modification;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.EitherCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.moonflower.pollen.api.PollenRegistries;
import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public final class PollenBiomeModifiers {

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
        public Codec<? extends PollinatedBiomeModifier> codec() {
            return null;
        }

        @Override
        public boolean test(Holder<Biome> biome, Phase phase) {
            return this.biomes.contains(biome) && phase == Phase.ADDITIONS;
        }

        @Override
        public void apply(PollinatedBiomeInfo info) {
            this.features.forEach(feature -> info.generation().addFeature(this.step, feature));
        }
    }
}
