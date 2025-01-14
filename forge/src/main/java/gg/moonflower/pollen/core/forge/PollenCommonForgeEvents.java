package gg.moonflower.pollen.core.forge;

import gg.moonflower.pollen.api.event.PollinatedEventResult;
import gg.moonflower.pollen.api.event.events.LootTableConstructingEvent;
import gg.moonflower.pollen.api.event.events.entity.EntityEvents;
import gg.moonflower.pollen.api.event.events.entity.ModifyTradesEvents;
import gg.moonflower.pollen.api.event.events.entity.SetTargetEvent;
import gg.moonflower.pollen.api.event.events.entity.living.LivingEntityEvents;
import gg.moonflower.pollen.api.event.events.entity.living.PotionEvents;
import gg.moonflower.pollen.api.event.events.entity.player.ContainerEvents;
import gg.moonflower.pollen.api.event.events.entity.player.PlayerEvents;
import gg.moonflower.pollen.api.event.events.entity.player.PlayerInteractionEvents;
import gg.moonflower.pollen.api.event.events.entity.player.server.ServerPlayerTrackingEvents;
import gg.moonflower.pollen.api.event.events.lifecycle.ServerLifecycleEvents;
import gg.moonflower.pollen.api.event.events.lifecycle.TickEvents;
import gg.moonflower.pollen.api.event.events.registry.CommandRegistryEvent;
import gg.moonflower.pollen.api.event.events.world.ChunkEvents;
import gg.moonflower.pollen.api.event.events.world.ExplosionEvents;
import gg.moonflower.pollen.api.event.events.lifecycle.LevelLoadingEvents;
import gg.moonflower.pollen.api.event.events.world.WorldEvents;
import gg.moonflower.pollen.api.util.value.MutableFloat;
import gg.moonflower.pollen.api.util.value.MutableInt;
import gg.moonflower.pollen.core.Pollen;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Mod.EventBusSubscriber(modid = Pollen.MOD_ID)
public class PollenCommonForgeEvents {

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.TickEvent.ServerTickEvent event) {
        switch (event.phase) {
            case START:
                TickEvents.SERVER_PRE.invoker().tick();
                break;
            case END:
                TickEvents.SERVER_POST.invoker().tick();
                break;
        }
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.TickEvent.WorldTickEvent event) {
        switch (event.phase) {
            case START:
                TickEvents.LEVEL_PRE.invoker().tick(event.world);
                break;
            case END:
                TickEvents.LEVEL_POST.invoker().tick(event.world);
                break;
        }
    }

    @SubscribeEvent
    public static void onEvent(LivingEvent.LivingUpdateEvent event) {
        if (!TickEvents.LIVING_PRE.invoker().tick(event.getEntityLiving()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEvent(FMLServerAboutToStartEvent event) {
        if (!ServerLifecycleEvents.PRE_STARTING.invoker().preStarting(event.getServer()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEvent(FMLServerStartingEvent event) {
        if (!ServerLifecycleEvents.STARTING.invoker().starting(event.getServer()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEvent(FMLServerStartedEvent event) {
        ServerLifecycleEvents.STARTED.invoker().started(event.getServer());
    }

    @SubscribeEvent
    public static void onEvent(FMLServerStoppingEvent event) {
        ServerLifecycleEvents.STOPPING.invoker().stopping(event.getServer());
    }

    @SubscribeEvent
    public static void onEvent(FMLServerStoppedEvent event) {
        ServerLifecycleEvents.STOPPED.invoker().stopped(event.getServer());
    }

    @SubscribeEvent
    public static void onEvent(RegisterCommandsEvent event) {
        CommandRegistryEvent.EVENT.invoker().registerCommands(event.getDispatcher(), event.getEnvironment());
    }

    @SubscribeEvent
    public static void onEvent(WorldEvent.Load event) {
        LevelLoadingEvents.LOAD.invoker().load(event.getWorld());
    }

    @SubscribeEvent
    public static void onEvent(WorldEvent.Unload event) {
        LevelLoadingEvents.UNLOAD.invoker().unload(event.getWorld());
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem event) {
        InteractionResultHolder<ItemStack> result = PlayerInteractionEvents.RIGHT_CLICK_ITEM.invoker().interaction(event.getPlayer(), event.getWorld(), event.getHand());
        if (result.getResult() != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result.getResult());
        }
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = PlayerInteractionEvents.RIGHT_CLICK_BLOCK.invoker().interaction(event.getPlayer(), event.getWorld(), event.getHand(), event.getHitVec());
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock event) {
        InteractionResult result = PlayerInteractionEvents.LEFT_CLICK_BLOCK.invoker().interaction(event.getPlayer(), event.getWorld(), event.getHand(), event.getPos(), event.getFace());
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract event) {
        InteractionResult result = PlayerInteractionEvents.RIGHT_CLICK_ENTITY.invoker().interaction(event.getPlayer(), event.getWorld(), event.getHand(), event.getEntity());
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.world.ChunkEvent.Load event) {
        ChunkEvents.LOAD.invoker().load(event.getWorld(), event.getChunk());
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.world.ChunkEvent.Unload event) {
        ChunkEvents.UNLOAD.invoker().unload(event.getWorld(), event.getChunk());
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.entity.player.PlayerEvent.StartTracking event) {
        ServerPlayerTrackingEvents.START_TRACKING_ENTITY.invoker().startTracking(event.getPlayer(), event.getEntity());
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.entity.player.PlayerEvent.StopTracking event) {
        ServerPlayerTrackingEvents.STOP_TRACKING_ENTITY.invoker().stopTracking(event.getPlayer(), event.getEntity());
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent event) {
        SetTargetEvent.EVENT.invoker().setTarget(event.getEntityLiving(), event.getTarget());
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.world.ExplosionEvent.Start event) {
        if (!ExplosionEvents.START.invoker().start(event.getWorld(), event.getExplosion()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.world.ExplosionEvent.Detonate event) {
        ExplosionEvents.DETONATE.invoker().detonate(event.getWorld(), event.getExplosion(), event.getAffectedEntities());
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.world.SaplingGrowTreeEvent event) {
        PollinatedEventResult result = WorldEvents.TREE_GROWING.invoker().interaction(event.getWorld(), event.getRand(), event.getPos());
        event.setResult(convertResult(result));
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.entity.player.BonemealEvent event) {
        PollinatedEventResult result = WorldEvents.BONEMEAL.invoker().bonemeal(event.getWorld(), event.getPos(), event.getBlock(), event.getStack());
        if (result == PollinatedEventResult.DENY)
            event.setCanceled(true);
        else if (result == PollinatedEventResult.ALLOW)
            event.setResult(Event.Result.ALLOW);
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.entity.EntityJoinWorldEvent event) {
        if (!EntityEvents.JOIN.invoker().onJoin(event.getEntity(), event.getWorld()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.entity.EntityLeaveWorldEvent event) {
        EntityEvents.LEAVE.invoker().onLeave(event.getEntity(), event.getWorld());
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.village.VillagerTradesEvent event) {
        Int2ObjectMap<ModifyTradesEvents.TradeRegistry> newTrades = new Int2ObjectOpenHashMap<>();
        int minTier = event.getTrades().keySet().stream().mapToInt(Integer::intValue).min().orElse(1);
        int maxTier = event.getTrades().keySet().stream().mapToInt(Integer::intValue).max().orElse(5);
        ModifyTradesEvents.VILLAGER.invoker().modifyTrades(new ModifyTradesEvents.ModifyVillager.Context() {
            @Override
            public VillagerProfession getProfession() {
                return event.getType();
            }

            @Override
            public ModifyTradesEvents.TradeRegistry getTrades(int tier) {
                Validate.inclusiveBetween(minTier, maxTier, tier, "Tier must be between " + minTier + " and " + maxTier);
                return newTrades.computeIfAbsent(tier, key -> new ModifyTradesEvents.TradeRegistry());
            }

            @Override
            public int getMinTier() {
                return minTier;
            }

            @Override
            public int getMaxTier() {
                return maxTier;
            }
        });

        newTrades.forEach((tier, registry) -> event.getTrades().get(tier.intValue()).addAll(registry));
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.village.WandererTradesEvent event) {
        ModifyTradesEvents.TradeRegistry generic = new ModifyTradesEvents.TradeRegistry();
        ModifyTradesEvents.TradeRegistry rare = new ModifyTradesEvents.TradeRegistry();

        ModifyTradesEvents.WANDERER.invoker().modifyTrades(new ModifyTradesEvents.ModifyWanderer.Context() {
            @Override
            public ModifyTradesEvents.TradeRegistry getGeneric() {
                return generic;
            }

            @Override
            public ModifyTradesEvents.TradeRegistry getRare() {
                return rare;
            }
        });

        event.getGenericTrades().addAll(generic);
        event.getRareTrades().addAll(rare);
    }

    @SubscribeEvent
    public static void onEvent(PlayerContainerEvent.Open event) {
        ContainerEvents.OPEN.invoker().open(event.getPlayer(), event.getContainer());
    }

    @SubscribeEvent
    public static void onEvent(PlayerContainerEvent.Close event) {
        ContainerEvents.CLOSE.invoker().close(event.getPlayer(), event.getContainer());
    }

    @SubscribeEvent
    public static void onEvent(AdvancementEvent event) {
        PlayerEvents.ADVANCEMENT_EVENT.invoker().playerAdvancement(event.getPlayer(), event.getAdvancement());
    }

    @SubscribeEvent
    public static void onEvent(PlayerXpEvent.PickupXp event) {
        if (!PlayerEvents.EXP_PICKUP.invoker().expPickup(event.getPlayer(), event.getOrb()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEvent(PlayerXpEvent.XpChange event) {
        if (!PlayerEvents.EXP_CHANGE.invoker().expChange(event.getPlayer(), MutableInt.complex(event, PlayerXpEvent.XpChange::getAmount, PlayerXpEvent.XpChange::setAmount)))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEvent(PlayerXpEvent.LevelChange event) {
       if (!PlayerEvents.LEVEL_CHANGE.invoker().levelChange(event.getPlayer(), MutableInt.complex(event, PlayerXpEvent.LevelChange::getLevels, PlayerXpEvent.LevelChange::setLevels)))
           event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.entity.player.PlayerSleepInBedEvent event) {
        Player.BedSleepingProblem result = PlayerEvents.START_SLEEPING.invoker().startSleeping(event.getPlayer(), event.getPos());
        if (result != null)
            event.setResult(result);
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.entity.player.PlayerWakeUpEvent event) {
        PlayerEvents.STOP_SLEEPING.invoker().stopSleeping(event.getPlayer(), event.wakeImmediately(), event.updateWorld());
    }

    @SubscribeEvent
    public static void onEvent(PlayerEvent.PlayerRespawnEvent event) {
        PlayerEvents.RESPAWN.invoker().respawn((ServerPlayer) event.getEntity(), event.isEndConquered());
    }

    @SubscribeEvent
    public static void onEvent(PlayerEvent.Clone event) {
        if (event.getOriginal() instanceof ServerPlayer && event.getPlayer() instanceof ServerPlayer)
            PlayerEvents.CLONE.invoker().clone((ServerPlayer) event.getOriginal(), (ServerPlayer) event.getPlayer(), event.isWasDeath());
    }

    @SubscribeEvent
    public static void onEvent(PlayerEvent.ItemCraftedEvent event) {
        PlayerEvents.ITEM_CRAFTED.invoker().craft(event.getPlayer(), event.getCrafting(), event.getInventory());
    }

    @SubscribeEvent
    public static void onEvent(PlayerEvent.ItemSmeltedEvent event) {
        PlayerEvents.ITEM_SMELTED.invoker().smelt(event.getPlayer(), event.getSmelting());
    }

    @SubscribeEvent
    public static void onEvent(LootTableLoadEvent event) {
        LootTableConstructingEvent.Context context = new LootTableConstructingEvent.Context(event.getName(), event.getTable());
        LootTableConstructingEvent.EVENT.invoker().modifyLootTable(context);
        event.setTable(context.apply());
    }

    @SubscribeEvent
    public static void onEvent(LivingDamageEvent event) {
      if (!LivingEntityEvents.DAMAGE.invoker().livingDamage(event.getEntityLiving(), event.getSource(), MutableFloat.complex(event, LivingDamageEvent::getAmount, LivingDamageEvent::setAmount)))
          event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEvent(LivingDeathEvent event) {
        if (!LivingEntityEvents.DEATH.invoker().death(event.getEntityLiving(), event.getSource()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEvent(LivingHealEvent event) {
        if (!LivingEntityEvents.HEAL.invoker().heal(event.getEntityLiving(), MutableFloat.complex(event, LivingHealEvent::getAmount, LivingHealEvent::setAmount)))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEvent(PotionEvent.PotionApplicableEvent event) {
        PollinatedEventResult result = PotionEvents.APPLICABLE.invoker().applicable(event.getEntityLiving(), event.getPotionEffect());
        event.setResult(convertResult(result));
    }

    @SubscribeEvent
    public static void onEvent(PotionEvent.PotionAddedEvent event) {
        PotionEvents.ADD.invoker().add(event.getEntityLiving(), event.getOldPotionEffect(), event.getPotionEffect());
    }

    @SubscribeEvent
    public static void onEvent(PotionEvent.PotionRemoveEvent event) {
        if (!PotionEvents.REMOVE.invoker().remove(event.getEntityLiving(), event.getPotion()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEvent(PotionEvent.PotionExpiryEvent event) {
        PotionEvents.EXPIRE.invoker().expire(event.getEntityLiving(), event.getPotionEffect());
    }

    public static PollinatedEventResult convertResult(Event.Result result) {
        switch (result) {
            case DENY:
                return PollinatedEventResult.DENY;
            case ALLOW:
                return PollinatedEventResult.ALLOW;
            case DEFAULT:
                return PollinatedEventResult.PASS;
            default:
                throw new UnsupportedOperationException("Unknown event result type: " + result);
        }
    }

    public static Event.Result convertResult(PollinatedEventResult result) {
        switch (result) {
            case DENY:
                return Event.Result.DENY;
            case ALLOW:
                return Event.Result.ALLOW;
            case PASS:
                return Event.Result.DEFAULT;
            default:
                throw new UnsupportedOperationException("Unknown event result type: " + result);
        }
    }
}
