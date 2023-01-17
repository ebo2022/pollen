package gg.moonflower.pollen.core.mixin.fabric;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import gg.moonflower.pollen.api.biome.modifier.fabric.FabricBiomeModifierSetup;
import gg.moonflower.pollen.api.event.events.lifecycle.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.jetbrains.annotations.Nullable;
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

    // Modifiers should be applied each time a world starts BEFORE fabric applies theirs
    @Inject(method = "<init>", at = @At(value = "RETURN", shift = At.Shift.BEFORE))
    public void registerModifiers(Thread thread, LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, WorldStem worldStem, Proxy proxy, DataFixer dataFixer,
                                  MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, GameProfileCache gameProfileCache, ChunkProgressListenerFactory chunkProgressListenerFactory, CallbackInfo ci) {
        FabricBiomeModifierSetup.runModifiers();
    }
}
