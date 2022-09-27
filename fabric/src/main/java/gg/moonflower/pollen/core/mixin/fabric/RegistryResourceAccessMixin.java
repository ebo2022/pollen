package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.api.registry.fabric.PollinatedDataRegistryImpl;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/resources/RegistryResourceAccess$1")
public class RegistryResourceAccessMixin {

    // Allow pollen data registries to use the same file structure as Forge. Avoids having to do it separately on each platform
    @Inject(method = "registryDirPath(Lnet/minecraft/resources/ResourceLocation;)Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
    private static void dataRegistryDirPath(ResourceLocation location, CallbackInfoReturnable<String> cir) {
        if (!location.getNamespace().equals("minecraft") && PollinatedDataRegistryImpl.isValidDataRegistryKey(location))
            cir.setReturnValue(location.getNamespace() + "/" + location.getPath());
    }
}
