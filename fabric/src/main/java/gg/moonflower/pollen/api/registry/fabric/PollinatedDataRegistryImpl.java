package gg.moonflower.pollen.api.registry.fabric;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import gg.moonflower.pollen.api.registry.PollinatedDataRegistry;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@ApiStatus.Internal
public class PollinatedDataRegistryImpl<T> extends PollinatedDataRegistry<T> {

    private final Registry<T> registry;
    private static final Set<ResourceLocation> DATA_REGISTRY_KEYS = new HashSet<>();

    private PollinatedDataRegistryImpl(ResourceKey<? extends Registry<T>> resourceKey, Registry<T> registry, Codec<T> codec, @Nullable Codec<T> networkCodec) {
        super(resourceKey, codec, networkCodec);
        this.registry = registry;
        DATA_REGISTRY_KEYS.add(resourceKey.location());
    }

    public static <T> PollinatedDataRegistry<T> create(ResourceKey<? extends Registry<T>> resourceKey, Codec<T> codec, @Nullable Codec<T> networkCodec) {
        return new PollinatedDataRegistryImpl<>(resourceKey, new MappedRegistry<>(resourceKey, Lifecycle.stable(), null), codec, networkCodec);
    }

    public Registry<T> getRegistry() {
        return this.registry;
    }

    @Override
    public <R extends T> Supplier<R> registerDefaultValue(String id, Supplier<R> object) {
        R registered = Registry.register(this.registry, new ResourceLocation(this.getModId(), id), object.get());
        return () -> registered;
    }

    public static boolean isValidDataRegistryKey(ResourceLocation location) {
        return DATA_REGISTRY_KEYS.contains(location);
    }
}
