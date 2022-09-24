package gg.moonflower.pollen.api.util;

import com.mojang.serialization.Lifecycle;
import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;

public final class RegistryHelper {

    public static <T> void injectBuiltinRegistry(Registry<T> registry) {
        injectRegistry(registry, BuiltinRegistries.REGISTRY);
    }

    @SuppressWarnings("unchecked")
    private static <T> void injectRegistry(Registry<T> reg, Registry<? extends Registry<?>> rootRegistry) {
        WritableRegistry<Registry<T>> registry = (WritableRegistry<Registry<T>>) rootRegistry;
        registry.register((ResourceKey<Registry<T>>) reg.key(), reg, Lifecycle.experimental());
    }
}
