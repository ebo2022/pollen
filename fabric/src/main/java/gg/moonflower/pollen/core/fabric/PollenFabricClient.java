package gg.moonflower.pollen.core.fabric;

import gg.moonflower.pollen.api.event.events.client.render.ReloadRendersEvent;
import gg.moonflower.pollen.api.event.events.entity.EntityEvents;
import gg.moonflower.pollen.api.event.events.lifecycle.TickEvents;
import gg.moonflower.pollen.api.event.events.network.ClientNetworkEvents;
import gg.moonflower.pollen.api.event.events.world.ChunkEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.InvalidateRenderStateCallback;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PollenFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> TickEvents.CLIENT_PRE.invoker().tick());
        ClientTickEvents.END_CLIENT_TICK.register(client -> TickEvents.CLIENT_POST.invoker().tick());
        ClientTickEvents.START_WORLD_TICK.register(level -> TickEvents.LEVEL_PRE.invoker().tick(level));
        ClientTickEvents.END_WORLD_TICK.register(level -> TickEvents.LEVEL_POST.invoker().tick(level));

        ClientChunkEvents.CHUNK_LOAD.register((level, chunk) -> ChunkEvents.LOAD.invoker().load(level, chunk));
        ClientChunkEvents.CHUNK_UNLOAD.register((level, chunk) -> ChunkEvents.UNLOAD.invoker().unload(level, chunk));

        InvalidateRenderStateCallback.EVENT.register(() -> ReloadRendersEvent.EVENT.invoker().reloadRenders());

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> ClientNetworkEvents.LOGIN.invoker().login(client.gameMode, client.player, handler.getConnection()));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientNetworkEvents.LOGOUT.invoker().logout(client.gameMode, client.player, handler.getConnection()));

        ClientEntityEvents.ENTITY_LOAD.register((entity, level) -> EntityEvents.JOIN.invoker().onJoin(entity, level));
        ClientEntityEvents.ENTITY_UNLOAD.register((entity, level) -> EntityEvents.LEAVE.invoker().onLeave(entity, level));
    }
}
