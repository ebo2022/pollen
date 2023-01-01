package gg.moonflower.pollen.impl.registry;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.base.platform.Platform;
import gg.moonflower.pollen.api.registry.v1.*;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
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

@ApiStatus.Internal
public abstract class PollinatedRegistryImpl<T> implements PollinatedRegistry<T> {

    protected final String modId;
    private boolean registered;

    protected PollinatedRegistryImpl(String modId) {
        this.modId = modId;
    }

    @ExpectPlatform
    public static <T> PollinatedRegistry<T> create(Registry<T> registry, String modId) {
        return Platform.error();
    }

    @ExpectPlatform
    public static <T> PollinatedRegistry<T> create(PollinatedRegistry<T> registry, String modId) {
        return Platform.error();
    }

    @Override
    public String getModId() {
        return modId;
    }

    @Override
    public final void register(Platform mod) {
        if (this.registered)
            throw new IllegalStateException("Cannot register a PollinatedRegistry twice!");
        this.registered = true;
        this.onRegister(mod);
    }

    @ApiStatus.Internal
    public static class VanillaImpl<T> extends PollinatedRegistryImpl<T> {

        private final Registry<T> registry;
        private final Codec<T> codec;
        private final Set<RegistryValue<T>> entries = new HashSet<>();
        private final Set<RegistryValue<T>> entriesView = Collections.unmodifiableSet(entries);

        public VanillaImpl(Registry<T> registry, String modId) {
            super(modId);
            this.registry = registry;
            this.codec = this.registry.byNameCodec();
        }

        public Registry<T> getRegistry() {
            return registry;
        }

        @Override
        public <R extends T> RegistryValue<R> register(String id, Supplier<R> object) {
            ResourceLocation name = new ResourceLocation(this.modId, id);
            return new VanillaRegistryValue<>(Registry.register(this.registry, name, object.get()), this.registry, name);
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
        public Collection<RegistryValue<T>> entries() {
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
