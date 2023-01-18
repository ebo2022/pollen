package gg.moonflower.pollen.api.biome.modifier.forge;

import gg.moonflower.pollen.api.biome.modifier.BiomeModifierManager;
import gg.moonflower.pollen.api.biome.modifier.modification.BiomeModification;
import gg.moonflower.pollen.api.biome.modifier.modification.ModifierApplicationPhase;
import gg.moonflower.pollen.api.biome.modifier.modification.PollinatedBiomeInfo;
import gg.moonflower.pollen.api.biome.modifier.modification.forge.PollinatedBiomeInfoImpl;
import gg.moonflower.pollen.api.biome.modifier.selector.forge.BiomeSelectorContextImpl;
import gg.moonflower.pollen.core.Pollen;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Predicate;
import java.util.stream.Stream;

@ApiStatus.Internal
@Mod.EventBusSubscriber(modid = Pollen.MOD_ID)
public final class ForgeBiomeModifierSetup {

    private ForgeBiomeModifierSetup() {
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void processAdditions(BiomeLoadingEvent event) {
        runModifiersFor(event, ModifierApplicationPhase.ADDITIONS);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void processRemovals(BiomeLoadingEvent event) {
        runModifiersFor(event, ModifierApplicationPhase.REMOVALS);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void processReplacements(BiomeLoadingEvent event) {
        runModifiersFor(event, ModifierApplicationPhase.REPLACEMENTS);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void postProcessBiomes(BiomeLoadingEvent event) {
        runModifiersFor(event, ModifierApplicationPhase.POST_PROCESSING);
    }

    private static void runModifiersFor(BiomeLoadingEvent event, ModifierApplicationPhase phase) {
        if (event.getName() == null)
            return;
        BiomeModifierManager.getAllModifiers().forEach(entry -> {
            if (entry.biomeSelectionMode().test(entry.selectors(), new BiomeSelectorContextImpl(event))) {
                Stream<BiomeModification> modifications = entry.getModificationsByPhase(phase);
                if (!modifications.toList().isEmpty()) {
                    PollinatedBiomeInfo pollenContext = new PollinatedBiomeInfoImpl(event);
                    modifications.forEach(modification -> modification.accept(pollenContext));
                }
            }
        });
    }
}
