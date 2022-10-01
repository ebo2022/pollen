package gg.moonflower.pollen.api.biome.modification.fabric;

import gg.moonflower.pollen.api.PollenRegistries;
import gg.moonflower.pollen.api.biome.modification.PollinatedBiomeModifier;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

@ApiStatus.Internal
public final class FabricBiomeModifierManager {

    private static final Logger LOGGER = LogManager.getLogger();

    private FabricBiomeModifierManager() {
    }

    public static void runModifiers(RegistryAccess registryAccess) {
        int modifiersApplied = 0;
        Registry<PollinatedBiomeModifier> modifiers = PollenRegistries.BIOME_MODIFIERS.get(registryAccess);
        for (Map.Entry<ResourceKey<PollinatedBiomeModifier>, PollinatedBiomeModifier> entry : modifiers.entrySet()) {
            for (ModificationPhase phase : ModificationPhase.values()) {
                ResourceLocation modifierLocation = entry.getKey().location();
                PollinatedBiomeModifier modifier = entry.getValue();
                BiomeModifications.create(modifierLocation).add(phase, selector -> modifier.test(selector.getBiomeRegistryEntry(), wrapPhase(phase)), (selector, context) -> modifier.apply(new PollinatedBiomeInfoImpl(context)));
            }
            modifiersApplied++;
        }
        LOGGER.info("Loaded " + modifiersApplied + " biome modifiers from server data");
    }

    private static PollinatedBiomeModifier.Phase wrapPhase(ModificationPhase phase) {
        return switch (phase) {
            case ADDITIONS -> PollinatedBiomeModifier.Phase.ADDITIONS;
            case REMOVALS -> PollinatedBiomeModifier.Phase.REMOVALS;
            case REPLACEMENTS -> PollinatedBiomeModifier.Phase.REPLACEMENTS;
            case POST_PROCESSING -> PollinatedBiomeModifier.Phase.POST_PROCESSING;
        };
    }
}
