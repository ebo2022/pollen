package gg.moonflower.pollen.impl.registry.fabric;

import gg.moonflower.pollen.api.registry.v1.PollinatedRegistry;
import gg.moonflower.pollen.impl.registry.PollinatedRegistryImpl;
import net.minecraft.core.Registry;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PollinatedRegistryImplImpl {

    public static <T> PollinatedRegistry<T> create(Registry<T> registry, String modId) {
        return PollinatedRegistry.createVanilla(registry, modId);
    }

    public static <T> PollinatedRegistry<T> create(PollinatedRegistry<T> registry, String modId) {
        return create(((PollinatedRegistryImpl.VanillaImpl<T>) registry).getRegistry(), modId);
    }
}
