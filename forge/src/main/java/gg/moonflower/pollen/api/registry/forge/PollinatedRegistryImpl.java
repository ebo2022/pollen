package gg.moonflower.pollen.api.registry.forge;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.platform.forge.ForgePlatform;
import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import gg.moonflower.pollen.api.registry.RegistryProperties;
import gg.moonflower.pollen.api.registry.RegistryValue;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.registries.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApiStatus.Internal
public final class PollinatedRegistryImpl<T> extends PollinatedRegistry<T> {

    private final Supplier<IForgeRegistry<T>> forgeRegistry;
    private final DeferredRegister<T> register;
    private final Codec<T> codec;
    private final ResourceKey<? extends Registry<T>> resourceKey;
    private final Set<RegistryValue<T>> entries = new HashSet<>();
    private final Set<RegistryValue<T>> entriesView = Collections.unmodifiableSet(this.entries);

    private PollinatedRegistryImpl(DeferredRegister<T> deferredRegister, Supplier<IForgeRegistry<T>> forgeRegistrySupplier, Codec<T> codec, ResourceKey<? extends Registry<T>> resourceKey, String modId) {
        super(modId);
        this.register = DeferredRegister.create(resourceKey, modId);
        this.forgeRegistry = forgeRegistrySupplier;
        this.codec = codec;
        this.resourceKey = resourceKey;
    }

    private PollinatedRegistryImpl(IForgeRegistry<T> forgeRegistry, String modId) {
        super(modId);
        this.register = DeferredRegister.create(forgeRegistry, modId);
        this.forgeRegistry = () -> forgeRegistry;
        this.codec = forgeRegistry.getCodec();
        this.resourceKey = forgeRegistry.getRegistryKey();
    }

    private PollinatedRegistryImpl(ResourceLocation forgeRegistryId, RegistryProperties<T> properties) {
        super(forgeRegistryId.getNamespace());
        this.resourceKey = ResourceKey.createRegistryKey(forgeRegistryId);
        this.register = DeferredRegister.create(this.resourceKey, forgeRegistryId.getNamespace());
        this.forgeRegistry = this.register.makeRegistry(() -> {
            RegistryBuilder<T> builder = new RegistryBuilder<>();
            if (!properties.shouldSave())
                builder.disableSaving();
            if (!properties.shouldSync())
                builder.disableSync();
            List<RegistryProperties.OnAdd<T>> onAdd = properties.getOnAdd();
            if (!onAdd.isEmpty())
                builder.onAdd((internal, registryManager, i, key, object, old) -> onAdd.forEach(c -> c.onAdd(i, key.location(), object)));
            return builder;
        });
        this.codec = ExtraCodecs.lazyInitializedCodec(() -> this.forgeRegistry.get().getCodec());
    }

    public static <T> PollinatedRegistry<T> create(ResourceKey<? extends Registry<T>> registryKey, String modId) {
        IForgeRegistry<T> forgeRegistry = RegistryManager.ACTIVE.getRegistry(registryKey);
        return forgeRegistry != null ? new PollinatedRegistryImpl<>(forgeRegistry, modId) : PollinatedRegistry.createVanilla(registryKey, modId);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> PollinatedRegistry<T> create(PollinatedRegistry<T> forgeRegistry, String modId) {
        if (forgeRegistry instanceof PollinatedRegistry.VanillaImpl)
            return createVanilla(((PollinatedRegistry.VanillaImpl<T>) forgeRegistry).getRegistry(), modId);
        PollinatedRegistryImpl<?> impl = (PollinatedRegistryImpl<?>) forgeRegistry;
        return new PollinatedRegistryImpl(impl.register, impl.forgeRegistry, impl.codec, impl.resourceKey, modId);
    }

    @SafeVarargs
    public static <T> PollinatedRegistry<T> create(ResourceLocation registryId, RegistryProperties<T> properties, T... typeGetter) {
        return new PollinatedRegistryImpl<>(registryId, properties);
    }

    // This accounts for if the forgeRegistry isn't initialized yet since custom forge registries are added later
    private <R> R getIfInitialized(Function<IForgeRegistry<T>, R> getter, Supplier<R> orElse) {
        IForgeRegistry<T> registry = this.forgeRegistry.get();
        return registry == null ? orElse.get() : getter.apply(registry);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends T> RegistryValue<R> register(String id, Supplier<? extends R> object) {
        RegistryObject<R> registered = this.register.register(id, object);
        RegistryValue<R> value = new RegistryValue<>() {

            @Override
            public R get() {
                return registered.get();
            }

            @Override
            public Optional<Holder<R>> getHolder() {
                return registered.getHolder();
            }

            @Override
            public boolean isPresent() {
                return registered.isPresent();
            }

            @Override
            public ResourceLocation getId() {
                return registered.getId();
            }

            @Override
            public ResourceKey<R> getKey() {
                return registered.getKey();
            }
        };
        this.entries.add((RegistryValue<T>) value);
        return value;
    }

    @Nullable
    @Override
    public ResourceLocation getKey(T value) {
        return this.getIfInitialized(registry -> registry.getKey(value), null);
    }

    @Override
    public int getId(@Nullable T value) {
        return this.getIfInitialized(registry -> ((ForgeRegistry<T>) registry).getID(value), () -> 0);
    }

    @Nullable
    @Override
    public T get(@Nullable ResourceLocation name) {
        return this.getIfInitialized(registry -> registry.getValue(name), () -> null);
    }

    @Nullable
    @Override
    public T byId(int id) {
        return this.getIfInitialized(registry -> ((ForgeRegistry<T>) registry).getValue(id), () -> null);
    }

    @Override
    public ResourceKey<? extends Registry<T>> key() {
        return resourceKey;
    }

    @Override
    public int size() {
        return this.getIfInitialized(registry -> registry.getEntries().size(), () -> 0);
    }

    @Override
    public Set<ResourceLocation> keySet() {
        return this.getIfInitialized(IForgeRegistry::getKeys, Collections::emptySet);
    }

    @Override
    public Set<Map.Entry<ResourceKey<T>, T>> entrySet() {
        return this.getIfInitialized(IForgeRegistry::getEntries, Collections::emptySet);
    }

    @Override
    public boolean containsKey(ResourceLocation name) {
        return this.getIfInitialized(registry -> registry.containsKey(name), () -> false);
    }

    @Override
    public Collection<RegistryValue<T>> getModEntries() {
        return this.entriesView;
    }

    @Override
    protected void onRegister(Platform mod) {
        this.register.register(((ForgePlatform) mod).getEventBus());
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
        return this.getIfInitialized(registry -> registry.getKeys().stream().map(location -> ops.createString(location.toString())), Stream::empty);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return this.getIfInitialized(IForgeRegistry::iterator, Collections::emptyIterator);
    }
}
