package gg.moonflower.pollen.api.util;

import com.mojang.serialization.Lifecycle;
import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RegistryHelper {

    private static final Logger LOGGER = LogManager.getLogger();

    private RegistryHelper() {
    }

    public static <T> void injectBuiltinRegistry(Registry<T> registry) {
        injectRegistry(registry, BuiltinRegistries.REGISTRY);
        LOGGER.info("Added a custom data registry: " + registry.key().location().toString());
    }

    @SuppressWarnings("unchecked")
    private static <T> void injectRegistry(Registry<T> reg, Registry<? extends Registry<?>> rootRegistry) {
        WritableRegistry<Registry<T>> registry = (WritableRegistry<Registry<T>>) rootRegistry;
        registry.register((ResourceKey<Registry<T>>) reg.key(), reg, Lifecycle.experimental());
    }
}