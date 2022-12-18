package gg.moonflower.pollen.api.registry;


/**
 * A builder to create a custom {@link PollinatedRegistry}.
 *
 * @param <T> The registry type
 * @author ebo2022
 * @since 1.6.0
 */
public interface PollinatedRegistryBuilder<T> {

    /**
     * Disables saving the registry to the disk.
     */
    PollinatedRegistryBuilder<T> disableSaving();

    /**
     * Disables syncing the registry to the client.
     */
    PollinatedRegistryBuilder<T> disableSync();

    /**
     * Builds a new {@link PollinatedRegistry} with the specified parameters.
     *
     * @return A new {@link PollinatedRegistry} backed by custom platform-specific registries
     */
    PollinatedRegistry<T> build();
}
