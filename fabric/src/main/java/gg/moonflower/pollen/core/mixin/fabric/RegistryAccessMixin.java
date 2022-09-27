package gg.moonflower.pollen.core.mixin.fabric;

import com.google.common.collect.ImmutableMap;
import gg.moonflower.pollen.api.registry.PollinatedDataRegistry;
import gg.moonflower.pollen.api.registry.fabric.PollinatedDataRegistryImpl;
import gg.moonflower.pollen.api.registry.fabric.PollinatedDataRegistryLoader;
import gg.moonflower.pollen.api.util.RegistryHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(RegistryAccess.class)
public interface RegistryAccessMixin {
    @Inject(method = "method_30531", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void putPollenBuiltinRegistries(CallbackInfoReturnable<ImmutableMap<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>>> cir, ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> builder) {
        FabricLoader.getInstance().getEntrypoints(PollinatedDataRegistryLoader.ID, PollinatedDataRegistryLoader.class).forEach(loader -> loader.applyRegistries(new PollinatedDataRegistryLoader.Factory() {
            @Override
            public <T> void bindRegistry(PollinatedDataRegistry<T> registry) {
                registerBuiltin(registry);
                builder.put(registry.key(), createRegistryAccessData(registry));
            }
        }));
    }

    @Unique
    private static <T> RegistryAccess.RegistryData<T> createRegistryAccessData(PollinatedDataRegistry<T> registry) {
        PollinatedDataRegistryImpl<T> impm = (PollinatedDataRegistryImpl<T>) registry;
        return new RegistryAccess.RegistryData<>(registry.key(), registry.getCodec(), registry.getNetworkCodec());
    }

    @Unique
    private static <T> void registerBuiltin(PollinatedDataRegistry<T> registry) {
        RegistryHelper.injectBuiltinRegistry(((PollinatedDataRegistryImpl<T>) registry).getRegistry());
    }
}