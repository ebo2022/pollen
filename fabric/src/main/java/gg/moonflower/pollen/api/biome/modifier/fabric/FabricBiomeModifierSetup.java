package gg.moonflower.pollen.api.biome.modifier.fabric;

import gg.moonflower.pollen.api.biome.modifier.BiomeModifierManager;
import gg.moonflower.pollen.api.biome.modifier.modification.ModifierApplicationPhase;
import gg.moonflower.pollen.api.biome.modifier.modification.PollinatedBiomeInfo;
import gg.moonflower.pollen.api.biome.modifier.modification.fabric.PollinatedBiomeInfoImpl;
import gg.moonflower.pollen.api.biome.modifier.selector.fabric.BiomeSelectorContextImpl;
import net.fabricmc.fabric.api.biome.v1.BiomeModification;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Predicate;
import java.util.stream.Stream;

@ApiStatus.Internal
public final class FabricBiomeModifierSetup {

    private FabricBiomeModifierSetup() {
    }

    public static void runModifiers() {
        BiomeModifierManager.entrySet().forEach(entry -> {
            BiomeModification fabricModification = BiomeModifications.create(entry.getKey());
            Predicate<BiomeSelectionContext> sharedSelector = context -> entry.getValue().biomeSelectionMode().test(entry.getValue().selectors(), new BiomeSelectorContextImpl(context));
            for (ModifierApplicationPhase phase : ModifierApplicationPhase.values()) {
                Stream<gg.moonflower.pollen.api.biome.modifier.modification.BiomeModification> modifications = entry.getValue().getModificationsByPhase(phase);
                if (!modifications.toList().isEmpty()) {
                    fabricModification.add(wrapPhase(phase), sharedSelector, modificationContext -> {
                        PollinatedBiomeInfo pollenContext = new PollinatedBiomeInfoImpl(modificationContext);
                        modifications.forEach(modification -> modification.accept(pollenContext));
                    });
                }
            }
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