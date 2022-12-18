package gg.moonflower.pollen.api.registry;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;

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
     * Enables content to be added to the registry via JSON files in datapacks.
     * <p>The folder where content is registered from depends on the resource location provided as the registry name.
     *
     * @param codec        The codec to deserialize content from JSON
     * @param networkCodec An optional network codec to sync content between servers and clients
     */
    PollinatedRegistryBuilder<T> dataPackRegistry(Codec<T> codec, @Nullable Codec<T> networkCodec);

    /**
     * Enables content to be added to the registry via JSON files in datapacks.
     * <p>The folder where content is registered from depends on the resource location provided as the registry name.
     *
     * @param codec The codec to deserialize content from JSON
     */
    default PollinatedRegistryBuilder<T> dataPackRegistry(Codec<T> codec) {
        return this.dataPackRegistry(codec, null);
    }

    /**
     * Builds a new {@link PollinatedRegistry} with the specified parameters.
     *
     * @return A new {@link PollinatedRegistry} backed by custom platform-specific registries
     */
    PollinatedRegistry<T> build();
}
