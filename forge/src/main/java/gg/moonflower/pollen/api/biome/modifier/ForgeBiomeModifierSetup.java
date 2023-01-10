package gg.moonflower.pollen.api.biome.modifier;

import com.mojang.serialization.Codec;
import gg.moonflower.pollen.api.biome.modifier.modification.PollinatedBiomeInfo;
import gg.moonflower.pollen.api.biome.modifier.modification.forge.PollinatedBiomeInfoImpl;
import gg.moonflower.pollen.api.biome.modifier.selector.BiomeSelector;
import gg.moonflower.pollen.api.biome.modifier.selector.forge.BiomeSelectorContextImpl;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class ForgeBiomeModifierSetup {

    private ForgeBiomeModifierSetup() {
    }

    private static Codec<Impl> EMPTY_MODIFIER_SERIALIZER;

    public static void register(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, helper -> helper.register(new ResourceLocation(Pollen.MOD_ID, "empty_modifier_serializer"), EMPTY_MODIFIER_SERIALIZER = Codec.unit(Impl.INSTANCE)));
        event.register(ForgeRegistries.Keys.BIOME_MODIFIERS, helper -> helper.register(new ResourceLocation(Pollen.MOD_ID, "forge_biome_modifier_handler"), Impl.INSTANCE));
    }

    private static BiomeModifier.Phase wrapPhase(ModifierApplicationPhase phase) {
        return switch (phase) {
            case ADDITIONS -> BiomeModifier.Phase.ADD;
            case REMOVALS -> BiomeModifier.Phase.REMOVE;
            case REPLACEMENTS -> BiomeModifier.Phase.MODIFY;
            case POST_PROCESSING -> BiomeModifier.Phase.AFTER_EVERYTHING;
        };
    }

    private static class Impl implements BiomeModifier {

        private static final Impl INSTANCE = new Impl();

        @Override
        public void modify(Holder<Biome> arg, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            BiomeSelector.Context selectionContext = new BiomeSelectorContextImpl(arg);
            PollinatedBiomeInfo biomeInfo = new PollinatedBiomeInfoImpl(builder);
            BiomeModifierManager.getAllModifiers().forEach(modifier -> {
                if (modifier.biomeSelectionMode().test(modifier.selectors(), selectionContext)) {
                    modifier.modifications().forEach(modification -> {
                        for (ModifierApplicationPhase applicationPhase : ModifierApplicationPhase.values()) {
                            if (phase == wrapPhase(modification.getApplicationPhase())) {
                                modification.accept(biomeInfo);
                            }
                        }
                    });
                }
            });
        }

        @Override
        public Codec<? extends BiomeModifier> codec() {
            if (EMPTY_MODIFIER_SERIALIZER != null) {
                return EMPTY_MODIFIER_SERIALIZER;
            } else {
                return Codec.unit(INSTANCE);
            }
        }
    }
}
