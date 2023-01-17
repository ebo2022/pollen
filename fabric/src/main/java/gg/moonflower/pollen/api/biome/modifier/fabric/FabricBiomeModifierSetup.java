package gg.moonflower.pollen.api.biome.modifier.fabric;

import gg.moonflower.pollen.api.biome.modifier.BiomeModifierManager;
import gg.moonflower.pollen.api.biome.modifier.modification.ModifierApplicationPhase;
import gg.moonflower.pollen.api.biome.modifier.modification.fabric.PollinatedBiomeInfoImpl;
import gg.moonflower.pollen.api.biome.modifier.selector.fabric.BiomeSelectorContextImpl;
import net.fabricmc.fabric.api.biome.v1.BiomeModification;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Predicate;

@ApiStatus.Internal
public final class FabricBiomeModifierSetup {

    private FabricBiomeModifierSetup() {
    }

    public static void runModifiers() {
        BiomeModifierManager.entrySet().forEach(entry -> {
            BiomeModification fabricModification = BiomeModifications.create(entry.getKey());
            Predicate<BiomeSelectionContext> sharedSelector = context -> entry.getValue().biomeSelectionMode().test(entry.getValue().selectors(), new BiomeSelectorContextImpl(context));
            entry.getValue().modifications().forEach(modification -> {
                for (ModifierApplicationPhase phase : ModifierApplicationPhase.values()) {
                    if (phase == modification.getApplicationPhase()) {
                        fabricModification.add(wrapPhase(phase), sharedSelector, ctx -> modification.accept(new PollinatedBiomeInfoImpl(ctx)));
                    }
                }
            });
        });
    }

    private static ModificationPhase wrapPhase(ModifierApplicationPhase phase) {
        return switch (phase) {
            case ADDITIONS -> ModificationPhase.ADDITIONS;
            case REMOVALS -> ModificationPhase.REMOVALS;
            case REPLACEMENTS -> ModificationPhase.REPLACEMENTS;
            case POST_PROCESSING -> ModificationPhase.POST_PROCESSING;
        };
    }
}