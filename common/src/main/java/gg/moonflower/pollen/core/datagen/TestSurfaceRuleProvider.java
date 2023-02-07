package gg.moonflower.pollen.core.datagen;

import gg.moonflower.pollen.api.datagen.provider.PollinatedSurfaceRuleProvider;
import gg.moonflower.pollen.api.levelgen.surfacerule.PollinatedSurfaceRuleSource;
import gg.moonflower.pollen.api.levelgen.surfacerule.SurfaceRuleCategory;
import gg.moonflower.pollen.api.levelgen.surfacerule.SurfaceRuleStage;
import gg.moonflower.pollen.api.util.PollinatedModContainer;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;

import java.util.List;
import java.util.function.BiConsumer;

public class TestSurfaceRuleProvider extends PollinatedSurfaceRuleProvider {

    public TestSurfaceRuleProvider(DataGenerator generator, PollinatedModContainer modContainer) {
        super(generator, modContainer);
    }

    @Override
    protected void registerRuleSources(BiConsumer<String, List<PollinatedSurfaceRuleSource.RuleProvider>> registry) {
        registry.accept("test_rules", List.of(
                new PollinatedSurfaceRuleSource.RuleProvider(
                        new PollinatedSurfaceRuleSource.InjectData(SurfaceRuleStage.AFTER_BEDROCK, 1),
                        SurfaceRuleCategory.OVERWORLD,
                        SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.PLAINS),
                                SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.POWDER_SNOW, 0.35, 0.6),
                                        SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0),
                                                SurfaceRules.state(Blocks.DIAMOND_ORE.defaultBlockState()))))
                ),
                new PollinatedSurfaceRuleSource.RuleProvider(
                        null,
                        SurfaceRuleCategory.OVERWORLD,
                        SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.FOREST),
                                SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(62), 0),
                                        SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(69), 0)),
                                                SurfaceRules.state(Blocks.WATER.defaultBlockState())
                                        )
                                )
                        ), SurfaceRules.bandlands())
                ))
        );
    }
}
