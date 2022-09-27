package gg.moonflower.pollen.api.registry.fabric;

import gg.moonflower.pollen.api.registry.PollinatedDataRegistry;

/**
 *
 */
@FunctionalInterface
public interface PollinatedDataRegistryLoader {

    String ID = "pollen-data-registry-loader";

    void applyRegistries(Factory factory);

    interface Factory {

        <T> void bindRegistry(PollinatedDataRegistry<T> registry);
    }
}
