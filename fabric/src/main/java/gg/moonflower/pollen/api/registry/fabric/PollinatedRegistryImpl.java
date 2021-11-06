package gg.moonflower.pollen.api.registry.fabric;

import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import net.minecraft.core.Registry;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class PollinatedRegistryImpl<T> {

    public static <T> PollinatedRegistry<T> create(Registry<T> registry, String modId) {
        return PollinatedRegistry.createVanilla(registry, modId);
    }
}