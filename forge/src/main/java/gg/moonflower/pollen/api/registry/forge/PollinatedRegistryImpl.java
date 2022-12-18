package gg.moonflower.pollen.api.registry.forge;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.platform.forge.ForgePlatform;
import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import gg.moonflower.pollen.api.registry.PollinatedRegistryBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApiStatus.Internal
public final class PollinatedRegistryImpl<T> extends PollinatedRegistry<T> {

    private final DeferredRegister<T> registry;
    private final Supplier<Codec<T>> codec;
    private final Keyable keyable;
    private final ResourceKey<? extends Registry<T>> resourceKey;
    private final Function<ResourceLocation, T> valueGetter;
    private final IntFunction<T> valueIdGetter;
    private final Function<T, ResourceLocation> keyGetter;
    private final ToIntFunction<T> keyIdGetter;

    // Constructor for custom built registries
     PollinatedRegistryImpl(ResourceLocation name, RegistryBuilder<T> builder) {
        super(name.getNamespace());
        this.resourceKey = ResourceKey.createRegistryKey(name);
        this.registry = DeferredRegister.create(this.resourceKey, name.getNamespace());
        Supplier<IForgeRegistry<T>> delegator = this.registry.makeRegistry(() -> builder);
        this.codec = () -> delegator.get().getCodec();
        this.keyable = new Keyable() {
            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return delegator.get().getKeys().stream().map(location -> ops.createString(location.toString()));
            }
        };
        this.valueGetter = location -> delegator.get().getValue(location);
        this.valueIdGetter = value -> ((ForgeRegistry<T>) delegator.get()).getValue(value);
        this.keyGetter = value -> delegator.get().getKey(value);
        this.keyIdGetter = value -> ((ForgeRegistry<T>) delegator.get()).getID(value);
    }

    // Constructor for registries that are descendants of a parent registry
    private PollinatedRegistryImpl(PollinatedRegistryImpl<T> copyFrom, String modId) {
        super(modId);
        this.resourceKey = copyFrom.resourceKey;
        this.registry = DeferredRegister.create(resourceKey, modId);
        this.codec = copyFrom.codec;
        this.keyable = copyFrom.keyable;
        this.valueGetter = copyFrom.valueGetter;
        this.valueIdGetter = copyFrom.valueIdGetter;
        this.keyGetter = copyFrom.keyGetter;
        this.keyIdGetter = copyFrom.keyIdGetter;
    }

    // Constructor for existing registries; check if a forge registry is present first
    private PollinatedRegistryImpl(Registry<T> registry, String modId) {
        super(modId);
        this.registry = DeferredRegister.create(registry.key(), modId);
        IForgeRegistry<T> forgeRegistry = RegistryManager.ACTIVE.getRegistry(registry.key());
        if (forgeRegistry != null) {
            this.codec = forgeRegistry::getCodec;
            this.keyable = new Keyable() {
                @Override
                public <T> Stream<T> keys(DynamicOps<T> ops) {
                    return forgeRegistry.getKeys().stream().map(location -> ops.createString(location.toString()));
                }
            };
            this.resourceKey = forgeRegistry.getRegistryKey();
            this.valueGetter = forgeRegistry::getValue;
            this.valueIdGetter = ((ForgeRegistry<T>) forgeRegistry)::getValue;
            this.keyGetter = forgeRegistry::getKey;
            this.keyIdGetter = ((ForgeRegistry<T>) forgeRegistry)::getID;
        } else {
            this.codec = registry::byNameCodec;
            this.keyable = registry;
            this.resourceKey = registry.key();
            this.valueGetter = registry::get;
            this.valueIdGetter = registry::byId;
            this.keyGetter = registry::getKey;
            this.keyIdGetter = registry::getId;
        }
    }

    public static <T> PollinatedRegistry<T> create(Registry<T> registry, String modId) {
        return new PollinatedRegistryImpl<>(registry, modId);
    }

    public static <T> PollinatedRegistry<T> create(PollinatedRegistry<T> registry, String modId) {
       return new PollinatedRegistryImpl<>((PollinatedRegistryImpl<T>) registry, modId);
    }

    @SafeVarargs
    public static <T> PollinatedRegistryBuilder<T> builder(ResourceLocation name, T... typeGetter) {
        return new PollinatedRegistryBuilderImpl<>(name);
    }

    @Override
    public <R extends T> Value<R> register(String id, Supplier<R> object) {
        return new ValueImpl<>(this.registry.register(id, object));
    }

    @Nullable
    @Override
    public ResourceLocation getKey(T value) {
        return this.keyGetter.apply(value);
    }

    @Override
    public int getId(@Nullable T value) {
        return this.keyIdGetter.applyAsInt(value);
    }

    @Nullable
    @Override
    public T get(@Nullable ResourceLocation name) {
        return this.valueGetter.apply(name);
    }

    @Nullable
    @Override
    public T byId(int id) {
        return this.valueIdGetter.apply(id);
    }

    @Override
    public ResourceKey<? extends Registry<T>> key() {
        return resourceKey;
    }

    @Override
    public Set<ResourceLocation> keySet() {
        return this.registry.getEntries().stream().map(RegistryObject::getId).collect(Collectors.toSet());
    }

    @Override
    public boolean containsKey(ResourceLocation name) {
        return this.registry.getEntries().stream().anyMatch(object -> object.getId().equals(name));
    }

    @Override
    public Collection<Value<T>> getValues() {
        return this.registry.getEntries().stream().map(ValueImpl::new).collect(Collectors.toList());
    }

    @Override
    protected void onRegister(Platform mod) {
        this.registry.register(((ForgePlatform) mod).getEventBus());
    }

    @Override
    public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
        return this.codec.get().decode(ops, input);
    }

    @Override
    public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
        return this.codec.get().encode(input, ops, prefix);
    }

    @Override
    public <T1> Stream<T1> keys(DynamicOps<T1> ops) {
        return this.keyable.keys(ops);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return this.registry.getEntries().stream().filter(RegistryObject::isPresent).map(RegistryObject::get).iterator();
    }

    private static class ValueImpl<T> implements Value<T> {

        private final RegistryObject<T> parent;

        private ValueImpl(RegistryObject<T> parent) {
            this.parent = parent;
        }

        @Override
        public T get() {
            return this.parent.get();
        }

        @Override
        public Optional<Holder<T>> getHolder() {
            return this.parent.getHolder();
        }

        @Override
        public boolean isPresent() {
            return this.parent.isPresent();
        }

        @Override
        public ResourceLocation getId() {
            return this.parent.getId();
        }

        @Override
        public ResourceKey<T> getKey() {
            return this.parent.getKey();
        }
    }
}
