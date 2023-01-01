package gg.moonflower.pollen.api.registry.v1;

/**
 * A builder to create a custom {@link PollinatedRegistry} with extended functionality.
 *
 * @param <T>
 */
public interface PollinatedRegistryBuilder<T> {

    /**
     * Disables saving the registry to the disk.
     */
    PollinatedRegistryBuilder<T> disableSaving();

    /**
     * Disables syncing of the registry between servers and clients.
     */
    PollinatedRegistryBuilder<T> disableSync();

    /**
     * Builds the registry using the specified parameters.
     *
     * @return The built registry
     */
    PollinatedRegistry<T> build();
}
