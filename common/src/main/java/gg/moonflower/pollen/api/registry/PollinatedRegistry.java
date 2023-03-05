package gg.moonflower.pollen.api.registry;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.Lifecycle;
import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.core.*;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Allows mods to access and register to platform-specific registries.
 *
 * @param <T> The object type
 * @author Jackson
 * @since 1.0.0
 */
public abstract class PollinatedRegistry<T> implements Codec<T>, Keyable, IdMap<T> {

    protected final String modId;
    private boolean registered;

    protected PollinatedRegistry(String modId) {
        this.modId = modId;
    }

    /**
     * Creates a {@link PollinatedRegistry} for an existing vanilla registry.
     *
     * @param registryKey The {@link ResourceKey} representing the registry
     * @param modId       The mod id to register to
     * @param <T> The registry type
     * @return A new {@link PollinatedRegistry} for the given registry key
     */
    @ExpectPlatform
    public static <T> PollinatedRegistry<T> create(ResourceKey<? extends Registry<T>> registryKey, String modId) {
        return Platform.error();
    }

    /**
     * Creates a {@link PollinatedRegistry} for the specified {@link Registry}.
     *
     * @param registry The registry to register objects to.
     * @param modId    The mod id to register to.
     * @param <T>      The registry type.
     * @deprecated Use {@link #create(ResourceKey, String)}. It doesn't require an instance of the vanilla registry to be used
     * @return A {@link PollinatedRegistry} backed by a platform-specific registry.
     */
    //TODO remove this in a later version
    @Deprecated
    public static <T> PollinatedRegistry<T> create(Registry<T> registry, String modId) {
        return create(registry.key(), modId);
    }

    /**
     * Creates a {@link PollinatedRegistry} that registers to another mod's registry.
     *
     * @param registry The registry to register objects to.
     * @param modId    The mod id to register to.
     * @param <T>      The registry type.
     * @return A {@link PollinatedRegistry} backed by a platform-specific registry.
     */
    @ExpectPlatform
    public static <T> PollinatedRegistry<T> create(PollinatedRegistry<T> registry, String modId) {
        return Platform.error();
    }

    /**
     * Creates a custom {@link PollinatedRegistry} backed by platform-specific implementations.
     * <p>This is for cases where a custom registry is needed alongside more specific behavior such as syncing between servers and clients.
     *
     * @param registryId The registry {@link ResourceLocation} used as the registry id
     * @param properties The properties to define how the registry behaves
     * @param <T>        The registry type
     * @return A {@link PollinatedRegistry} backed by the platform-specific custom registry
     */
    @SafeVarargs
    @ExpectPlatform
    public static <T> PollinatedRegistry<T> create(ResourceLocation registryId, RegistryProperties<T> properties, T... typeGetter) {
        return Platform.error();
    }

    /**
     * Creates a custom {@link PollinatedRegistry} backed by a vanilla {@link MappedRegistry}.
     * <p>This is for cases where a custom registry is needed but extra features, such as automatic syncing between servers and clients, are not.
     *
     * @param registryId The registry {@link ResourceLocation} used as the registry id
     * @param <T>        The type stored in the Registry
     * @return A {@link PollinatedRegistry} backed by a vanilla {@link MappedRegistry}
     */
    public static <T> PollinatedRegistry<T> createSimple(ResourceLocation registryId) {
        return createVanilla(new MappedRegistry<>(ResourceKey.createRegistryKey(registryId), Lifecycle.stable(), null), registryId.getNamespace());
    }

    /**
     * Creates a custom {@link PollinatedRegistry} backed by a vanilla {@link DefaultedRegistry}.
     * <p>This is for cases where a custom registry is needed but extra features, such as automatic syncing between servers and clients, are not.
     *
     * @param registryId The registry {@link ResourceLocation} used as the registry id
     * @param defaultId  The default registry id. This is what the registry returns if there is a null entry
     * @param <T>        The type stored in the Registry
     * @return A {@link PollinatedRegistry} backed by a vanilla {@link DefaultedRegistry}
     */
    public static <T> PollinatedRegistry<T> createDefaulted(ResourceLocation registryId, ResourceLocation defaultId) {
        return createVanilla(new DefaultedRegistry<>(defaultId.toString(), ResourceKey.createRegistryKey(registryId), Lifecycle.stable(), null), registryId.getNamespace());
    }

    /**
     * Creates a {@link PollinatedRegistry} backed by a {@link Registry}.
     * <p>Users should always use {@link PollinatedRegistry#create(ResourceKey, String)}.
     * <p>This is for very specific cases where vanilla registries must strictly be used and {@link PollinatedRegistry#create(Registry, String)} can't do what you need.
     *
     * @param registryKey The {@link ResourceKey} of the registry
     * @param modId       The mod id to register to
     * @param <T>         The registry type
     * @return A {@link PollinatedRegistry} backed by a {@link Registry}.
     */
    @SuppressWarnings("unchecked")
    public static <T> PollinatedRegistry<T> createVanilla(ResourceKey<? extends Registry<T>> registryKey, String modId) {
        //TODO this if-chain should be replaced with BuiltInRegistries.get in 1.19.3+
        Registry<T> registry = (Registry<T>) Registry.REGISTRY.get(registryKey.location());
        if (registry != null)
            return createVanilla(registry, modId);
        Registry<T> builtinRegistry = (Registry<T>) BuiltinRegistries.REGISTRY.get(registryKey.location());
        if (builtinRegistry != null)
            return createVanilla(builtinRegistry, modId);
        throw new IllegalArgumentException("Registry " + registryKey.location() + " doesn't exist");
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
    public static <T> PollinatedRegistry<T> createVanilla(Registry<T> registry, String modId) {
        return new VanillaImpl<>(registry, modId);
    }

    /**
     * Creates a {@link PollinatedRegistry} for registering blocks and item blocks. The mod id from the item registry is used as the id for the block registry.
     *
     * @param itemRegistry The registry to add items to
     * @return A specialized block registry that can register items
     */
    public static PollinatedBlockRegistry createBlock(PollinatedRegistry<Item> itemRegistry) {
        return new PollinatedBlockRegistry(create(Registry.BLOCK, itemRegistry.getModId()), itemRegistry);
    }

    /**
     * Creates a {@link PollinatedRegistry} for registering fluids.
     *
     * @param domain The domain of the mod
     * @return A specialized fluid registry that can fully handle fluids
     */
    public static PollinatedFluidRegistry createFluid(String domain) {
        return new PollinatedFluidRegistry(create(Registry.FLUID, domain));
    }

    /**
     * Creates a {@link PollinatedRegistry} for registering entities and Ai. Ai registries are automatically set to use the domain provided.
     *
     * @param domain The domain of the mod
     * @return A specialized entity registry that can also register Ai
     */
    public static PollinatedEntityRegistry createEntity(String domain) {
        return new PollinatedEntityRegistry(create(Registry.ENTITY_TYPE, domain));
    }

    /**
     * Creates a {@link PollinatedRegistry} for registering command arguments.
     *
     * @param domain The domain of the mod
     * @return A specialized command registry that can registry argument types
     */
    public static PollinatedCommandArgumentRegistry createCommandArgument(String domain) {
        return new PollinatedCommandArgumentRegistry(create(Registry.COMMAND_ARGUMENT_TYPE, domain));
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
    public abstract <R extends T> RegistryValue<R> register(String id, Supplier<? extends R> object);

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
    public <R extends T> RegistryValue<R> registerConditional(String id, Supplier<? extends R> dummy, Supplier<? extends R> object, boolean register) {
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
     * Retrieves the id for the specified value. This can only be used for a custom registry.
     *
     * @param value The value to get the id for
     * @return An id for that value or <code>null</code> if this registry doesn't contain that id
     */
    @Override
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
     * Retrieves the value for the specified id. This can only be used for a custom registry.
     *
     * @param id The id to get the value for
     * @return A value for that id or <code>null</code> if this registry doesn't contain a value with that id
     */
    @Nullable
    @Override
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
     * @return The size of the registry
     */
    @Override
    public abstract int size();

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
     * @return A set of all registered entries in the registry
     */
    public abstract Set<Map.Entry<ResourceKey<T>, T>> entrySet();

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
     * @return A collection of all registry values that were registered by this pollinated registry
     */
    public abstract Collection<RegistryValue<T>> getModEntries();

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

    @ApiStatus.Internal
    public static class VanillaImpl<T> extends PollinatedRegistry<T> {

        private final Registry<T> registry;
        private final Codec<T> codec;
        private final Set<RegistryValue<T>> entries = new HashSet<>();
        private final Set<RegistryValue<T>> entriesView = Collections.unmodifiableSet(this.entries);

        private VanillaImpl(Registry<T> registry, String modId) {
            super(modId);
            this.registry = registry;
            this.codec = this.registry.byNameCodec();
        }

        public Registry<T> getRegistry() {
            return registry;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <R extends T> RegistryValue<R> register(String id, Supplier<? extends R> object) {
            ResourceLocation name = new ResourceLocation(this.modId, id);
            R registered = Registry.register(this.registry, name, object.get());
            return new RegistryValue<>() {

                ResourceKey<R> objectKey = ResourceKey.create((ResourceKey<? extends Registry<R>>) registry.key(), name);

                @Override
                public R get() {
                    return registered;
                }

                @Override
                public Optional<Holder<R>> getHolder() {
                    Holder<R> holder = (Holder<R>) registry.getHolder((ResourceKey<T>) objectKey).orElse(null);
                    return Optional.ofNullable(holder);
                }

                @Override
                public boolean isPresent() {
                    return registry.containsKey(name);
                }

                @Override
                public ResourceLocation getId() {
                    return name;
                }

                @Override
                public ResourceKey<R> getKey() {
                    return objectKey;
                }
            };
        }

        @Nullable
        @Override
        public ResourceLocation getKey(T value) {
            return this.registry.getKey(value);
        }

        @Override
        public int getId(@Nullable T value) {
            return this.registry.getId(value);
        }

        @Nullable
        @Override
        public T get(@Nullable ResourceLocation name) {
            return this.registry.get(name);
        }

        @Nullable
        @Override
        public T byId(int id) {
            return this.registry.byId(id);
        }

        @Override
        public ResourceKey<? extends Registry<T>> key() {
            return this.registry.key();
        }

        @Override
        public int size() {
            return this.registry.size();
        }

        @Override
        public Set<ResourceLocation> keySet() {
            return this.registry.keySet();
        }

        @Override
        public Set<Map.Entry<ResourceKey<T>, T>> entrySet() {
            return this.registry.entrySet();
        }

        @Override
        public boolean containsKey(ResourceLocation name) {
            return this.registry.containsKey(name);
        }

        @Override
        public Collection<RegistryValue<T>> getModEntries() {
            return this.entriesView;
        }

        @Override
        public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
            return this.codec.decode(ops, input);
        }

        @Override
        public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
            return this.codec.encode(input, ops, prefix);
        }

        @Override
        public <T1> Stream<T1> keys(DynamicOps<T1> ops) {
            return this.registry.keys(ops);
        }

        @NotNull
        @Override
        public Iterator<T> iterator() {
            return this.registry.iterator();
        }
    }
}
