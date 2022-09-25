package gg.moonflower.pollen.core.mixin.fabric;

import com.google.common.collect.ImmutableMap;
import gg.moonflower.pollen.api.event.events.registry.RegisterDataAttachmentsEvent;
import gg.moonflower.pollen.api.registry.RegistryDataAttachment;
import gg.moonflower.pollen.api.util.RegistryHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(RegistryAccess.class)
public interface RegistryAccessMixin {

    @Inject(method = "method_30531", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/RegistryAccess;put(Lcom/google/common/collect/ImmutableMap$Builder;Lnet/minecraft/resources/ResourceKey;Lcom/mojang/serialization/Codec;)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void attachData(CallbackInfoReturnable<ImmutableMap<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>>> ci, ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> builder) {
        RegisterDataAttachmentsEvent.EVENT.invoker().registerAttachments(new RegisterDataAttachmentsEvent.Registry() {
            @Override
            public <T> void register(RegistryDataAttachment<T> attachment) {
                RegistryHelper.injectBuiltinRegistry(attachment.vanillaRegistry());
                BuiltinRegistriesAccessor.getLoaders().put(attachment.key().location(), () -> Holder.direct(attachment.defaultValueSupplier().get()));
                builder.put(attachment.key(), constructRegistryData(attachment));
            }
        });
    }

    @Unique
    private static <T> RegistryAccess.RegistryData<T> constructRegistryData(RegistryDataAttachment<T> registryDataAttachment) {
        return new RegistryAccess.RegistryData<>(registryDataAttachment.key(), registryDataAttachment.codec(), null);
    }
}
