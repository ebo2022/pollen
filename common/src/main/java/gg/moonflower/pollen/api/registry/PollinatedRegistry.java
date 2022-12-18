package gg.moonflower.pollen.api.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * An abstracted registry for wrapping or creating custom platform-specific registries.
 *
 * @param <T> The object type
 * @author Jackson
 * @since 1.0.0
 */
public abstract class PollinatedRegistry<T> implements Codec<T>, Keyable, Iterable<T> {

    protected final String modId;
    private boolean registered;

    protected PollinatedRegistry(String modId) {
        this.modId = modId;
    }

    /**
     * Creates a {@link PollinatedRegistry} that registers to an existing vanilla registry.
     *
     * @param registry The registry to register objects to.
     * @param modId    The mod id to register to.
     * @param <T>      The registry type.
     * @return A {@link PollinatedRegistry} that registers to an existing vanilla registry
     */
    @ExpectPlatform
    public static <T> PollinatedRegistry<T> create(Registry<T> registry, String modId) {
        return Platform.error();
    }

    /**
     * Creates a {@link PollinatedRegistry} that registers to another mod's custom registry.
     * <p>Entries added by the parent registry will not be included in the list returned by {@link #getValues()}.
     *
     * @param registry The registry to register objects to.
     * @param modId    The mod id to register to.
     * @param <T>      The registry type.
     * @return A {@link PollinatedRegistry} that registers to another mod's custom registry
     */
    @ExpectPlatform
    public static <T> PollinatedRegistry<T> create(PollinatedRegistry<T> registry, String modId) {
        return Platform.error();
    }

    /**
     * Creates a builder to make a custom pollinated registry that doesn't exist yet.
     * <p>The parent class of the built registry should implement {@link CustomRegistryBootstrap}.
     * <p>If you are creating a registry with simple behavior, use {@link #createSimple(ResourceLocation, Object[])} instead.
     *
     * @param name The name of the registry
     * @param <T> The object type
     * @return A new {@link PollinatedRegistryBuilder}
     */
    @SafeVarargs
    @ExpectPlatform
    public static <T> PollinatedRegistryBuilder<T> builder(ResourceLocation name, T... typeGetter) {
        return Platform.error();
    }

    /**
     * Creates a simple custom registry.
     *
     * @param registryId The registry {@link ResourceLocation} used as the registry id
     * @param <T>        The type stored in the Registry
     * @return A custom {@link PollinatedRegistry} backed by a platform-specific registry
     */
    @SafeVarargs
    public static <T> PollinatedRegistry<T> createSimple(ResourceLocation registryId, T... typeGetter) {
        return builder(registryId, typeGetter).build();
    }

    /**
     * @return The id of the mod this registry is for
     */
    public String getModId() {
        return modId;
    }

    /**
     * Registers an object.
     *
     * @param id     The id of the object.
     * @param object The object to register.
     * @param <R>    The registry type.
     * @return The registered object in a {@link Supplier}.
     */
    public abstract <R extends T> Value<R> register(String id, Supplier<R> object);

    /**
     * Registers an object or a dummy object based on a condition.
     *
     * @param id       The id of the object.
     * @param dummy    The object to register if the condition is false.
     * @param object   The object to register if the condition is true.
     * @param register Whether the object should be registered or the dummy should be registered.
     * @param <R>      The registry type.
     * @return The registered object in a {@link Supplier}
     */
    public <R extends T> Value<R> registerConditional(String id, Supplier<R> dummy, Supplier<R> object, boolean register) {
        return this.register(id, register ? object : dummy);
    }

    /**
     * Retrieves the key for the specified value.
     *
     * @param value The value to get the key for
     * @return A key for that value or <code>null</code> if this registry doesn't contain that value
     */
    @Nullable
    public abstract ResourceLocation getKey(T value);

    /**
     * Retrieves the id for the specified value.
     *
     * @param value The value to get the id for
     * @return An id for that value or <code>null</code> if this registry doesn't contain that id
     */
    public abstract int getId(@Nullable T value);

    /**
     * Retrieves the value for the specified key.
     *
     * @param name The key to get the value for
     * @return A value for that key or <code>null</code> if this registry doesn't contain a value with that name
     */
    @Nullable
    public abstract T get(@Nullable ResourceLocation name);

    /**
     * Retrieves the value for the specified id.
     *
     * @param id The id to get the value for
     * @return A value for that id or <code>null</code> if this registry doesn't contain a value with that id
     */
    @Nullable
    public abstract T byId(int id);

    /**
     * Retrieves the value for the specified key.
     *
     * @param name The key to get the value for
     * @return A value for that key
     */
    public Optional<T> getOptional(@Nullable ResourceLocation name) {
        return Optional.ofNullable(this.get(name));
    }

    /**
     * @return The key of this registry
     */
    public abstract ResourceKey<? extends Registry<T>> key();

    /**
     * Retrieves the value for the specified id.
     *
     * @param id The id to get the value for
     * @return A value for that id
     */
    public Optional<T> byIdOptional(int id) {
        return Optional.ofNullable(this.byId(id));
    }

    /**
     * @return A set of all registered keys in the registry
     */
    public abstract Set<ResourceLocation> keySet();

    /**
     * @return A stream of all values in the registry
     */
    public Stream<T> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    /**
     * Checks to see if a value with the specified name exists.
     *
     * @param name The name of the key to get
     * @return Whether that value exists
     */
    public abstract boolean containsKey(ResourceLocation name);

    /**
     * @return The set of registry values that were added by this registry
     */
    public abstract Collection<Value<T>> getValues();

    /**
     * Initializes the registry for a {@link Platform}.
     *
     * @param mod The {@link Platform} to register the registry onto.
     * @throws IllegalStateException if the registry has already been registered.
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

    /**
     * A wrapper for querying info on objects registered in a {@link PollinatedRegistry}.
     *
     * @param <T> The object type
     * @author ebo2022
     * @since 1.6.0
     */
    public interface Value<T> extends Supplier<T> {

        /**
         * @return The wrapped object from the parent registry
         */
        @Override
        T get();

        default Optional<T> getOptional() {
            return this.isPresent() ? Optional.of(this.get()) : Optional.empty();
        }

        /**
         * @return An optional vanilla {@link Holder} pointing to the held value to be used for vanilla compatibility
         */
        Optional<Holder<T>> getHolder();

        /**
         * @return Whether the held value is present
         */
        boolean isPresent();

        default void ifPresent(Consumer<? super T> consumer) {
            if (isPresent())
                consumer.accept(get());
        }

        /**
         * @return The name of the registered object
         */
        ResourceLocation getId();

        /**
         * @return A {@link ResourceKey} pointing to the registered object
         */
        ResourceKey<T> getKey();

        default Stream<T> stream() {
            return isPresent() ? Stream.of(get()) : Stream.of();
        }
    }
}
