package gg.moonflower.pollen.core.mixin.fabric;

import com.mojang.datafixers.DataFixer;
import gg.moonflower.pollen.api.PollenRegistries;
import gg.moonflower.pollen.api.biome.modification.PollinatedBiomeModifier;
import gg.moonflower.pollen.api.biome.modification.fabric.FabricBiomeModifierManager;
import gg.moonflower.pollen.api.biome.modification.fabric.PollinatedBiomeInfoImpl;
import gg.moonflower.pollen.api.event.events.lifecycle.ServerLifecycleEvents;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;
import java.util.Map;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow
    public abstract RegistryAccess.Frozen registryAccess();

    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;getMillis()J", ordinal = 0, shift = At.Shift.BEFORE))
    public void started(CallbackInfo ci) {
        ServerLifecycleEvents.STARTED.invoker().started((MinecraftServer) (Object) this);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void loadPollenModifiers(Thread thread, LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, WorldStem worldStem, Proxy proxy, DataFixer dataFixer, Services services, ChunkProgressListenerFactory chunkProgressListenerFactory, CallbackInfo ci) {
        Registry<PollinatedBiomeModifier> modifiers = PollenRegistries.BIOME_MODIFIERS.get(this.registryAccess());
        for (ModificationPhase phase : ModificationPhase.values()) {
            for (Map.Entry<ResourceKey<PollinatedBiomeModifier>, PollinatedBiomeModifier> entry : modifiers.entrySet()) {
                ResourceLocation modifierLocation = entry.getKey().location();
                BiomeModifications.create(modifierLocation).add(phase, selector -> true, (selector, context) -> entry.getValue().apply(selector.getBiomeRegistryEntry(), wrapPhase(phase), new PollinatedBiomeInfoImpl(context)));
            }
        }
    }

    @Unique
    private static PollinatedBiomeModifier.Phase wrapPhase(ModificationPhase phase) {
        return switch (phase) {
            case ADDITIONS -> PollinatedBiomeModifier.Phase.ADDITIONS;
            case REMOVALS -> PollinatedBiomeModifier.Phase.REMOVALS;
            case REPLACEMENTS -> PollinatedBiomeModifier.Phase.REPLACEMENTS;
            case POST_PROCESSING -> PollinatedBiomeModifier.Phase.POST_PROCESSING;
        };
    }
}
