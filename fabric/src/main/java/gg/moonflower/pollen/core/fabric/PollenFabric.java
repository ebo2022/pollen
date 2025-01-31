package gg.moonflower.pollen.core.fabric;

import gg.moonflower.pollen.api.config.PollinatedConfigType;
import gg.moonflower.pollen.api.config.fabric.ConfigTracker;
import gg.moonflower.pollen.api.event.events.LootTableConstructingEvent;
import gg.moonflower.pollen.api.event.events.entity.player.PlayerEvents;
import gg.moonflower.pollen.api.event.events.entity.player.PlayerInteractionEvents;
import gg.moonflower.pollen.api.event.events.entity.player.server.ServerPlayerTrackingEvents;
import gg.moonflower.pollen.api.event.events.lifecycle.ServerLifecycleEvents;
import gg.moonflower.pollen.api.event.events.lifecycle.TickEvents;
import gg.moonflower.pollen.api.event.events.registry.CommandRegistryEvent;
import gg.moonflower.pollen.api.event.events.world.ChunkEvents;
import gg.moonflower.pollen.api.event.events.lifecycle.LevelLoadingEvents;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.resource.condition.fabric.PollinatedResourceConditionImpl;
import gg.moonflower.pollen.common.trades.VillagerTradeManager;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.core.command.ConfigCommand;
import gg.moonflower.pollen.core.mixin.fabric.LevelResourceAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@ApiStatus.Internal
public class PollenFabric implements ModInitializer {

    private static final LevelResource SERVERCONFIG = LevelResourceAccessor.init("serverconfig");

    private static Path getServerConfigPath(MinecraftServer server) {
        Path serverConfig = server.getWorldPath(SERVERCONFIG);
        if (!Files.isDirectory(serverConfig)) {
            try {
                Files.createDirectories(serverConfig);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create " + serverConfig, e);
            }
        }
        return serverConfig;
    }

    @Override
    public void onInitialize() {
        Pollen.init();
        PollinatedResourceConditionImpl.init();

        ConfigTracker.INSTANCE.loadConfigs(PollinatedConfigType.COMMON, FabricLoader.getInstance().getConfigDir());
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            ConfigTracker.INSTANCE.loadConfigs(PollinatedConfigType.CLIENT, FabricLoader.getInstance().getConfigDir());

        Pollen.PLATFORM.setup();

        ServerTickEvents.START_SERVER_TICK.register(level -> TickEvents.SERVER_PRE.invoker().tick());
        ServerTickEvents.END_SERVER_TICK.register(level -> TickEvents.SERVER_POST.invoker().tick());
        ServerTickEvents.START_WORLD_TICK.register(level -> TickEvents.LEVEL_PRE.invoker().tick(level));
        ServerTickEvents.END_WORLD_TICK.register(level -> TickEvents.LEVEL_POST.invoker().tick(level));

        net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STOPPING.register(server -> ServerLifecycleEvents.STOPPING.invoker().stopping(server));
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STOPPED.register(server -> ServerLifecycleEvents.STOPPED.invoker().stopped(server));

        ServerWorldEvents.LOAD.register((server, world) -> LevelLoadingEvents.LOAD.invoker().load(world));
        ServerWorldEvents.UNLOAD.register((server, world) -> LevelLoadingEvents.UNLOAD.invoker().unload(world));

        ServerChunkEvents.CHUNK_LOAD.register((level, chunk)->ChunkEvents.LOAD.invoker().load(level, chunk));
        ServerChunkEvents.CHUNK_UNLOAD.register((level, chunk)->ChunkEvents.UNLOAD.invoker().unload(level, chunk));

        UseItemCallback.EVENT.register((player, level, hand) -> PlayerInteractionEvents.RIGHT_CLICK_ITEM.invoker().interaction(player, level, hand));
        UseBlockCallback.EVENT.register((player, level, hand, result) -> PlayerInteractionEvents.RIGHT_CLICK_BLOCK.invoker().interaction(player, level, hand, result));
        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> PlayerInteractionEvents.LEFT_CLICK_BLOCK.invoker().interaction(player, level, hand, pos, direction));
        UseEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> PlayerInteractionEvents.RIGHT_CLICK_ENTITY.invoker().interaction(player, world, hand, entity));

        EntitySleepEvents.ALLOW_SLEEPING.register(((player, sleepingPos) -> PlayerEvents.START_SLEEPING.invoker().startSleeping(player, sleepingPos)));

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> CommandRegistryEvent.EVENT.invoker().registerCommands(dispatcher, dedicated ? Commands.CommandSelection.DEDICATED : Platform.getRunningServer().isPresent() ? Commands.CommandSelection.INTEGRATED : Commands.CommandSelection.ALL));

        EntityTrackingEvents.START_TRACKING.register((entity, player) -> ServerPlayerTrackingEvents.START_TRACKING_ENTITY.invoker().startTracking(player, entity));
        EntityTrackingEvents.STOP_TRACKING.register((entity, player) -> ServerPlayerTrackingEvents.STOP_TRACKING_ENTITY.invoker().stopTracking(player, entity));

        LootTableLoadingCallback.EVENT.register((resourceManager, manager, id, supplier, setter) -> {
            LootTableConstructingEvent.Context context = new LootTableConstructingEvent.Context(id, supplier.build());
            LootTableConstructingEvent.EVENT.invoker().modifyLootTable(context);
            setter.set(context.apply());
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> PlayerEvents.RESPAWN.invoker().respawn(newPlayer, alive));
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> PlayerEvents.CLONE.invoker().clone(oldPlayer, newPlayer, alive));

        // Pollen Events
        ServerLifecycleEvents.PRE_STARTING.register(server -> {
            ConfigTracker.INSTANCE.loadConfigs(PollinatedConfigType.SERVER, getServerConfigPath(server));
            VillagerTradeManager.init();
            return true;
        });
        ServerLifecycleEvents.STOPPED.register(server -> ConfigTracker.INSTANCE.unloadConfigs(PollinatedConfigType.SERVER, getServerConfigPath(server)));
        CommandRegistryEvent.EVENT.register((dispatcher, selection) -> ConfigCommand.register(dispatcher, selection == Commands.CommandSelection.DEDICATED));
    }
}
