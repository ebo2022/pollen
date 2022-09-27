package gg.moonflower.pollen.api.registry.forge;

import com.mojang.serialization.Codec;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.platform.forge.ForgePlatform;
import gg.moonflower.pollen.api.registry.PollinatedDataRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@ApiStatus.Internal
public class PollinatedDataRegistryImpl<T> extends PollinatedDataRegistry<T> {

    private final DeferredRegister<T> registry;

    private PollinatedDataRegistryImpl(ResourceKey<? extends Registry<T>> resourceKey, Codec<T> codec, @Nullable Codec<T> networkCodec) {
        super(resourceKey, codec, networkCodec);
        this.registry = DeferredRegister.create(resourceKey, this.getModId());
        this.registry.makeRegistry(() -> new RegistryBuilder<T>().disableSaving().dataPackRegistry(this.codec, this.networkCodec));
    }

    public static <T> PollinatedDataRegistry<T> create(ResourceKey<? extends Registry<T>> resourceKey, Codec<T> codec, @Nullable Codec<T> networkCodec) {
        return new PollinatedDataRegistryImpl<>(resourceKey, codec, networkCodec);
    }

    @Override
    public <R extends T> Supplier<R> registerDefaultValue(String id, Supplier<R> object) {
        return this.registry.register(id, object);
    }

    @Override
    protected void onRegister(Platform mod) {
        this.registry.register(((ForgePlatform) mod).getEventBus());
    }
}
