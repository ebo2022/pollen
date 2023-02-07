package gg.moonflower.pollen.api.levelgen.surfacerule;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record PollinatedSurfaceRuleSource(List<RuleProvider> rules) {

    public static final Codec<PollinatedSurfaceRuleSource> CODEC = ExtraCodecs.nonEmptyList(RuleProvider.CODEC.listOf()).xmap(PollinatedSurfaceRuleSource::new, PollinatedSurfaceRuleSource::rules).fieldOf("rules").codec();

    public record RuleProvider(@Nullable InjectData vanillaInjectData, SurfaceRuleCategory category, SurfaceRules.RuleSource rule) {

        public static final Codec<RuleProvider> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                InjectData.CODEC.optionalFieldOf("inject").forGetter(entry -> Optional.ofNullable(entry.vanillaInjectData)),
                SurfaceRuleCategory.CODEC.fieldOf("category").forGetter(RuleProvider::category),
                SurfaceRules.RuleSource.CODEC.fieldOf("rule").forGetter(RuleProvider::rule)
        ).apply(builder, (injectData, category, rule) -> new RuleProvider(injectData.orElse(null), category, rule)));
    }

    public record InjectData(SurfaceRuleStage stage, int priority) {

        public static final Codec<InjectData> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                SurfaceRuleStage.CODEC.fieldOf("stage").forGetter(InjectData::stage),
                Codec.INT.fieldOf("priority").forGetter(InjectData::priority)
        ).apply(builder, InjectData::new));
    }
}
