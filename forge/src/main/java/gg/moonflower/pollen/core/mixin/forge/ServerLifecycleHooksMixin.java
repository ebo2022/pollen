package gg.moonflower.pollen.core.mixin.forge;

import gg.moonflower.pollen.api.PollenRegistries;
import gg.moonflower.pollen.api.biome.modification.forge.BiomeModifierHook;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerLifecycleHooks.class)
public class ServerLifecycleHooksMixin {

    @Unique
    private static RegistryAccess registryAccess;

    @Inject(method = "runModifiers", at = @At("HEAD"), remap = false)
    private static void captureRegistryAccess(MinecraftServer server, CallbackInfo ci) {
        registryAccess = server.registryAccess();
    }

    @Inject(method = "lambda$runModifiers$4", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/world/ModifiableBiomeInfo;applyBiomeModifiers(Lnet/minecraft/core/Holder;Ljava/util/List;)V", shift = At.Shift.BEFORE), remap = false)
    private static void setPollenModifiers(List<Holder.Reference<Biome>> list, Holder.Reference<Biome> biomeHolder, CallbackInfo ci) {
        ((BiomeModifierHook) biomeHolder.value().modifiableBiomeInfo()).pollen_setModifiers(PollenRegistries.BIOME_MODIFIERS.get(registryAccess).holders().map(Holder::value).toList());
    }
}
