package gg.moonflower.pollen.api.registry;

import com.mojang.serialization.Codec;
import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;


public abstract class PollinatedDataRegistry<T> {

    protected final ResourceKey<? extends Registry<T>> resourceKey;
    protected final Codec<T> codec;
    private boolean registered;

    @Nullable
    protected final Codec<T> networkCodec;

    protected PollinatedDataRegistry(ResourceKey<? extends Registry<T>> resourceKey, Codec<T> codec, @Nullable Codec<T> networkCodec) {
        this.resourceKey = resourceKey;
        this.codec = codec;
        this.networkCodec = networkCodec;
    }

    @ExpectPlatform
    public static <T> PollinatedDataRegistry<T> create(ResourceKey<? extends Registry<T>> resourceKey, Codec<T> codec, @Nullable Codec<T> networkCodec) {
        return Platform.error();
    }

    public static <T> PollinatedDataRegistry<T> create(ResourceKey<? extends Registry<T>> resourceKey, Codec<T> codec) {
        return create(resourceKey, codec, null);
    }

    public abstract <R extends T> Supplier<R> registerDefaultValue(String id, Supplier<R> object);

    public ResourceKey<? extends Registry<T>> key() {
        return this.resourceKey;
    }

    public String getModId() {
        return this.resourceKey.location().getNamespace();
    }

    public Codec<T> getCodec() {
        return this.codec;
    }

    @Nullable
    public Codec<T> getNetworkCodec() {
        return this.networkCodec;
    }

    public Registry<T> get(RegistryAccess registryAccess) {
        return registryAccess.registryOrThrow(this.resourceKey);
    }

    public final void register(Platform mod) {
        if (this.registered)
            throw new IllegalStateException("Cannot register a PollinatedRegistry twice!");
        this.registered = true;
        this.onRegister(mod);
    }

    @ApiStatus.OverrideOnly
    protected void onRegister(Platform mod) {
    }
}
