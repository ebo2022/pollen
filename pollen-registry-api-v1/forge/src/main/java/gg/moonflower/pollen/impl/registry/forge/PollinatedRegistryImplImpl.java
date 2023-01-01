package gg.moonflower.pollen.impl.registry.forge;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import gg.moonflower.pollen.api.registry.v1.PollinatedRegistry;
import gg.moonflower.pollen.api.registry.v1.RegistryValue;
import gg.moonflower.pollen.impl.registry.PollinatedRegistryImpl;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

@ApiStatus.Internal
public final class PollinatedRegistryImplImpl<T> extends PollinatedRegistryImpl<T> {

    private final DeferredRegister<T> registry;
    private final Supplier<Codec<T>> codec;
    private final Keyable keyable;
    private final ResourceKey<? extends Registry<T>> resourceKey;
    private final Function<ResourceLocation, T> valueGetter;
    private final IntFunction<T> valueIdGetter;
    private final Function<T, ResourceLocation> keyGetter;
    private final ToIntFunction<T> keyIdGetter;

    private PollinatedRegistryImplImpl(Registry<T> registry, String modId) {
        super(modId);
        this.registry = DeferredRegister.create(registry.key(), modId);
        this.codec = registry::byNameCodec;
        this.keyable = registry;
        this.resourceKey = registry.key();
        this.valueGetter = registry::get;
        this.valueIdGetter = registry::byId;
        this.keyGetter = registry::getKey;
        this.keyIdGetter = registry::getId;
    }

    private PollinatedRegistryImplImpl(ResourceLocation name, RegistryBuilder<T> builder) {
        super(name.getNamespace());
        this.resourceKey = ResourceKey.createRegistryKey(name);
        this.registry = DeferredRegister.create(this.resourceKey, name.getNamespace());

    }

    public static <T> PollinatedRegistryImpl<T> create(Registry<T> registry, String modId) {

    }

    public static <T> PollinatedRegistry<T> create(PollinatedRegistry<T> registry, String modId) {
    }

    @Override
    public <R extends T> RegistryValue<R> register(String id, Supplier<R> object) {
        return null;
    }

    @Override
    public @Nullable ResourceLocation getKey(T value) {
        return null;
    }

    @Override
    public int getId(@Nullable T value) {
        return 0;
    }

    @Override
    public @Nullable T get(@Nullable ResourceLocation name) {
        return null;
    }

    @Override
    public @Nullable T byId(int id) {
        return null;
    }

    @Override
    public ResourceKey<? extends Registry<T>> key() {
        return null;
    }

    @Override
    public Set<ResourceLocation> keySet() {
        return null;
    }

    @Override
    public boolean containsKey(ResourceLocation name) {
        return false;
    }

    @Override
    public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
        return null;
    }

    @Override
    public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
        return null;
    }

    @Override
    public <T> Stream<T> keys(DynamicOps<T> ops) {
        return null;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return null;
    }
}
