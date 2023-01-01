package gg.moonflower.pollen.api.registry.v1;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.Lifecycle;
import gg.moonflower.pollen.api.base.platform.Platform;
import gg.moonflower.pollen.impl.registry.PollinatedRegistryImpl;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * An abstracted registry for wrapping platform-specific registries.
 *
 * @param <T> The object type
 * @author Jackson
 * @since 1.0.0
 */
public interface PollinatedRegistry<T> extends Codec<T>, Keyable, Iterable<T> {

    /**
     * Creates an {@link PollinatedRegistry} backed by a platform-specific registry.
     * <p>Forge users: If there's no ForgeRegistry for the object type, this will return a {@link PollinatedRegistryImpl.VanillaImpl}.
     *
     * @param registry The registry to register objects to.
     * @param modId    The mod id to register to.
     * @param <T>      The registry type.
     * @return A {@link PollinatedRegistry} backed by a platform-specific registry.
     */
    static <T> PollinatedRegistry<T> create(Registry<T> registry, String modId) {
        return PollinatedRegistryImpl.create(registry, modId);
    }

    /**
     * Creates a {@link PollinatedRegistry} backed by a platform-specific registry. This should only be used to register to another mod's registry.
     *
     * @param registry The registry to register objects to.
     * @param modId    The mod id to register to.
     * @param <T>      The registry type.
     * @return A {@link PollinatedRegistry} backed by a platform-specific registry.
     */
    static <T> PollinatedRegistry<T> create(PollinatedRegistry<T> registry, String modId) {
        return PollinatedRegistryImpl.create(registry, modId);
    }

    /**
     * Creates a {@link PollinatedRegistry} backed by a {@link Registry}.
     * <p>Users should always use {@link PollinatedRegistry#create(Registry, String)}.
     * <p>This is for very specific cases where vanilla registries must strictly be used and {@link PollinatedRegistry#create(Registry, String)} can't do what you need.
     *
     * @param registry The registry to register objects to.
     * @param modId    The mod id to register to.
     * @param <T>      The registry type.
     * @return A {@link PollinatedRegistry} backed by a {@link Registry}.
     */
    static <T> PollinatedRegistry<T> createVanilla(Registry<T> registry, String modId) {
        return new PollinatedRegistryImpl.VanillaImpl<>(registry, modId);
    }

    /**
     * Creates a {@link PollinatedRegistry} for registering blocks and item blocks. The mod id from the item registry is used as the id for the block registry.
     *
     * @param itemRegistry The registry to add items to
     * @return A specialized block registry that can register items
     */
    static PollinatedBlockRegistry createBlock(PollinatedRegistry<Item> itemRegistry) {
        return new PollinatedBlockRegistry(PollinatedRegistry.create(Registry.BLOCK, itemRegistry.getModId()), itemRegistry);
    }

    /**
     * Creates a {@link PollinatedRegistry} for registering fluids.
     *
     * @param domain The domain of the mod
     * @return A specialized fluid registry that can fully handle fluids
     */
    static PollinatedFluidRegistry createFluid(String domain) {
        return new PollinatedFluidRegistry(PollinatedRegistry.create(Registry.FLUID, domain));
    }

    /**
     * Creates a {@link PollinatedRegistry} for registering entities and Ai. Ai registries are automatically set to use the domain provided.
     *
     * @param domain The domain of the mod
     * @return A specialized entity registry that can also register Ai
     */
    static PollinatedEntityRegistry createEntity(String domain) {
        return new PollinatedEntityRegistry(PollinatedRegistry.create(Registry.ENTITY_TYPE, domain));
    }

    /**
     * @return The id of the mod this registry is for
     */
    String getModId();

    /**
     * Registers an object.
     *
     * @param id     The id of the object.
     * @param object The object to register.
     * @param <R>    The registry type.
     * @return The registered object in a {@link Supplier}.
     */
    <R extends T> RegistryValue<R> register(String id, Supplier<R> object);

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
    default  <R extends T> RegistryValue<R> registerConditional(String id, Supplier<R> dummy, Supplier<R> object, boolean register) {
        return this.register(id, register ? object : dummy);
    }

    /**
     * Retrieves the key for the specified value.
     *
     * @param value The value to get the key for
     * @return A key for that value or <code>null</code> if this registry doesn't contain that value
     */
    @Nullable
    ResourceLocation getKey(T value);

    /**
     * Retrieves the id for the specified value. This can only be used for a custom registry.
     *
     * @param value The value to get the id for
     * @return An id for that value or <code>null</code> if this registry doesn't contain that id
     */
    int getId(@Nullable T value);

    /**
     * Retrieves the value for the specified key.
     *
     * @param name The key to get the value for
     * @return A value for that key or <code>null</code> if this registry doesn't contain a value with that name
     */
    @Nullable
    T get(@Nullable ResourceLocation name);

    /**
     * Retrieves the value for the specified id. This can only be used for a custom registry.
     *
     * @param id The id to get the value for
     * @return A value for that id or <code>null</code> if this registry doesn't contain a value with that id
     */
    @Nullable
    T byId(int id);

    /**
     * Retrieves the value for the specified key.
     *
     * @param name The key to get the value for
     * @return A value for that key
     */
    default Optional<T> getOptional(@Nullable ResourceLocation name) {
        return Optional.ofNullable(this.get(name));
    }

    /**
     * @return The key of this registry
     */
    ResourceKey<? extends Registry<T>> key();

    /**
     * Retrieves the value for the specified id.
     *
     * @param id The id to get the value for
     * @return A value for that id
     */
    default Optional<T> byIdOptional(int id) {
        return Optional.ofNullable(this.byId(id));
    }

    /**
     * @return A set of all registered keys in the registry
     */
    Set<ResourceLocation> keySet();

    /**
     * @return A stream of all values in the registry
     */
    default Stream<T> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    /**
     * Checks to see if a value with the specified name exists.
     *
     * @param name The name of the key to get
     * @return Whether that value exists
     */
    boolean containsKey(ResourceLocation name);

    /**
     * @return A set of all values added by this registry
     */
    Collection<RegistryValue<T>> entries();

    /**
     * Initializes the registry for a {@link Platform}.
     *
     * @param mod The {@link Platform} to register the registry onto.
     * @throws IllegalStateException if the registry has already been registered.
     */
    void register(Platform mod);

    @ApiStatus.OverrideOnly
    default void onRegister(Platform mod) {
    }
}
