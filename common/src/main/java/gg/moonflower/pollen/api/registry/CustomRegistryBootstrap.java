package gg.moonflower.pollen.api.registry;

import org.jetbrains.annotations.ApiStatus;

import java.util.ServiceLoader;

/**
 * Denotes that a class holds custom {@link PollinatedRegistry}s. Used to enable certain functionality during early modloading on Fabric.
 *
 * @author ebo2022
 * @since 1.6.0
 */
public interface CustomRegistryBootstrap {

    @ApiStatus.Internal
    @SuppressWarnings("ALL")
    static void load() {
        // getName() loads the class in early
        ServiceLoader.load(CustomRegistryBootstrap.class).forEach(instance -> instance.getClass().getName());
    }
}
