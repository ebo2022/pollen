package gg.moonflower.pollen.api.registry.fabric;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import gg.moonflower.pollen.api.registry.PollinatedRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

@ApiStatus.Internal
public final class PollinatedRegistryImpl<T> extends PollinatedRegistry<T> {

    private final Registry<T> registry;
    private final Codec<T> codec;

    private final Set<Value<T>> entries = new HashSet<>();
    private final Set<Value<T>> entriesView = Collections.unmodifiableSet(entries);

    public static <T> PollinatedRegistry<T> create(Registry<T> registry, String modId) {
        return new PollinatedRegistryImpl<>(registry, modId);
    }

    public static <T> PollinatedRegistry<T> create(PollinatedRegistry<T> registry, String modId) {
        return new PollinatedRegistryImpl<>(((PollinatedRegistryImpl<T>) registry).getRegistry(), modId);
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T> PollinatedRegistryBuilder<T> builder(ResourceLocation name, T... typeGetter) {
        return new PollinatedRegistryBuilderImpl<>(FabricRegistryBuilder.createSimple((Class<T>) typeGetter.getClass().getComponentType(), name), name);
    }

    PollinatedRegistryImpl(Registry<T> registry, String modId) {
        super(modId);
        this.registry = registry;
        this.codec = this.registry.byNameCodec();
    }

    public Registry<T> getRegistry() {
        return registry;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends T> Value<R> register(String id, Supplier<R> object) {
        ResourceLocation name = new ResourceLocation(this.modId, id);
        R registered = Registry.register(this.registry, name, object.get());
        ValueImpl<T, R> value = new ValueImpl<>(name, this.registry, registered);
        this.entries.add((Value<T>) value);
        return new ValueImpl<>(name, this.registry, registered);
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
    public Set<ResourceLocation> keySet() {
        return this.registry.keySet();
    }

    @Override
    public boolean containsKey(ResourceLocation name) {
        return this.registry.containsKey(name);
    }

    @Override
    public Collection<Value<T>> getValues() {
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

    @SuppressWarnings("unchecked")
    private static class ValueImpl<T, R extends T> implements Value<R> {

        private final R value;
        private final Registry<T> registry;
        private final ResourceKey<R> key;
        private final ResourceLocation name;

        private ValueImpl(ResourceLocation name, Registry<T> registry, R value) {
            this.registry = registry;
            this.value = value;
            this.name = name;
            this.key = ResourceKey.create((ResourceKey<? extends Registry<R>>) registry.key(), this.name);
        }

        @Override
        public R get() {
            return this.value;
        }

        @Override
        public Optional<Holder<R>> getHolder() {
            return Optional.ofNullable((Holder<R>) registry.getHolder((ResourceKey<T>) this.key).orElse(null));
        }

        @Override
        public boolean isPresent() {
            return this.registry.containsKey(this.name);
        }

        @Override
        public ResourceLocation getId() {
            return this.name;
        }

        @Override
        public ResourceKey<R> getKey() {
            return this.key;
        }
    }
}
