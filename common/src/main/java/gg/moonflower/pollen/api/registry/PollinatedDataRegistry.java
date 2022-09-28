package gg.moonflower.pollen.api.registry;

import com.mojang.serialization.Codec;
import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * An abstracted registry that can register values from datapacks using a {@link Codec}.
 * <p>Since datapacks vary between saves, values of this registry need to be taken from a server {@link RegistryAccess}.
 * <p><code>data/[namespace]/[registry_namespace]/[registry_path]</code>
 * @param <T> The object type
 * @author ebo2022
 * @since 2.0.0
 */
public abstract class PollinatedDataRegistry<T> {

    protected final ResourceKey<? extends Registry<T>> resourceKey;
    protected final Codec<T> codec;
    private boolean registered;

    @Nullable
    protected final Codec<T> networkCodec;

    protected PollinatedDataRegistry(ResourceKey<? extends Registry<T>> resourceKey, Codec<T> codec, @Nullable Codec<T> networkCodec) {
        this.resourceKey = resourceKey;
        this.codec = codec;
        this.networkCodec = networkCodec;
    }

    /**
     * Creates a {@link PollinatedDataRegistry} backed by platform-specific implementations of data registries.
     * <p>Fabric users should bind this registry using a <code>PollinatedDataRegistryLoader</code> for full functionality.
     *
     * @param resourceKey  The resource key representing this registry
     * @param codec        A codec to deserialize registry values loaded from JSON
     * @param networkCodec An optional network codec to sync data between servers and clients
     * @param <T>          The registry type
     * @return A {@link PollinatedDataRegistry} backed by platform-specific implementations of data registries
     */
    @ExpectPlatform
    public static <T> PollinatedDataRegistry<T> create(ResourceKey<? extends Registry<T>> resourceKey, Codec<T> codec, @Nullable Codec<T> networkCodec) {
        return Platform.error();
    }

    /**
     * Creates a {@link PollinatedDataRegistry} backed by platform-specific implementations of data registries.
     * <p>Fabric users should bind this registry using a <code>PollinatedDataRegistryLoader</code> for full functionality.
     *
     * @param resourceKey  The resource key representing this registry
     * @param codec        A codec to deserialize registry values loaded from JSON
     * @param <T>          The registry type
     * @return A {@link PollinatedDataRegistry} backed by platform-specific implementations of data registries
     */
    public static <T> PollinatedDataRegistry<T> create(ResourceKey<? extends Registry<T>> resourceKey, Codec<T> codec) {
        return create(resourceKey, codec, null);
    }

    /**
     * Registers a global value for this registry that is persistent across all saves.
     *
     * @param id     The id of the object
     * @param object The object to register
     * @param <R>    The registry type
     * @return The registered default value wrapped by a {@link Supplier}
     */
    public abstract <R extends T> Supplier<R> registerDefaultValue(String id, Supplier<R> object);

    /**
     * @return The key of this registry
     */
    public ResourceKey<? extends Registry<T>> key() {
        return this.resourceKey;
    }

    /**
     * @return The id of the mod this registry is for
     */
    public String getModId() {
        return this.resourceKey.location().getNamespace();
    }

    /**
     * @return The codec used to serialize and deserialize json values
     */
    public Codec<T> getCodec() {
        return this.codec;
    }

    /**
     * @return The codec to sync data between servers or clients, or <code>null</code> if none is present
     */
    @Nullable
    public Codec<T> getNetworkCodec() {
        return this.networkCodec;
    }

    /**
     * Gets an instance of this registry from a {@link RegistryAccess}.
     *
     * @param registryAccess The registry access to get the registry from
     * @return A registry containing both default values and ones loaded from datapacks
     */
    public Registry<T> get(RegistryAccess registryAccess) {
        return registryAccess.registryOrThrow(this.resourceKey);
    }

    /**
     * Initializes the registry for a {@link Platform} and registers any default values.
     *
     * @param mod The {@link Platform} to register the registry to
     * @throws IllegalStateException if the registry has already been registered
     */
    public final void register(Platform mod) {
        if (this.registered)
            throw new IllegalStateException("Cannot register a PollinatedRegistry twice!");
        this.registered = true;
        this.onRegister(mod);
    }

    @ApiStatus.OverrideOnly
    protected void onRegister(Platform mod) {
    }
}
