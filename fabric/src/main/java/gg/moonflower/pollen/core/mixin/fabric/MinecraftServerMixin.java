package gg.moonflower.pollen.core.mixin.fabric;

import com.mojang.datafixers.DataFixer;
import gg.moonflower.pollen.api.biome.modifier.fabric.FabricBiomeModifierSetup;
import gg.moonflower.pollen.api.event.events.lifecycle.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;getMillis()J", ordinal = 0, shift = At.Shift.BEFORE))
    public void started(CallbackInfo ci) {
        ServerLifecycleEvents.STARTED.invoker().started((MinecraftServer) (Object) this);
    }

    // Register biome modifiers after datapacks have been loaded, but before Fabric runs biome modifications
    @Inject(method = "<init>", at = @At(value = "RETURN", shift = At.Shift.BEFORE))
    public void registerModifiers(Thread thread, LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, WorldStem worldStem, Proxy proxy, DataFixer dataFixer, Services services, ChunkProgressListenerFactory chunkProgressListenerFactory, CallbackInfo ci) {
        FabricBiomeModifierSetup.runModifiers();
    }
}
