package gg.moonflower.pollen.core;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.moonflower.pollen.api.block.PollinatedStandingSignBlock;
import gg.moonflower.pollen.api.block.PollinatedWallSignBlock;
import gg.moonflower.pollen.api.event.events.registry.CommandRegistryEvent;
import gg.moonflower.pollen.api.item.SpawnEggItemBase;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.registry.*;
import gg.moonflower.pollen.api.registry.content.*;
import gg.moonflower.pollen.api.registry.resource.TagRegistry;
import gg.moonflower.pollen.core.client.render.DebugPollenFlowerPotRenderer;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRendererRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.*;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@ApiStatus.Internal
public class PollenTest {

    private static final PollinatedRegistry<Item> ITEMS = create(() -> PollinatedRegistry.create(Registry.ITEM, Pollen.MOD_ID));
    private static final PollinatedBlockRegistry BLOCKS = create(() -> PollinatedRegistry.createBlock(ITEMS));
    private static final PollinatedFluidRegistry FLUIDS = create(() -> PollinatedRegistry.createFluid(Pollen.MOD_ID));
    private static final ResourceKey<? extends Registry<TestMoonflowerType>> MOONFLOWER_KEY = create(() -> ResourceKey.createRegistryKey(new ResourceLocation("pollen", "moonflower_types")));
    public static final PollinatedDataRegistry<TestMoonflowerType> MOONFLOWER_TYPES = create(() -> PollinatedDataRegistry.create(Objects.requireNonNull(MOONFLOWER_KEY), TestMoonflowerType.CODEC));

    public static final TagKey<Fluid> TEST_TAG = create(() -> TagRegistry.bindFluid(new ResourceLocation(Pollen.MOD_ID, "test")));

    public static final Supplier<Item> TEST_SPAWN_EGG = create(() -> Objects.requireNonNull(ITEMS).register("test_spawn_egg", () -> new SpawnEggItemBase<>(() -> EntityType.IRON_GOLEM, 0, 0, new Item.Properties().tab(CreativeModeTab.TAB_MISC))));
    public static final Pair<Supplier<PollinatedStandingSignBlock>, Supplier<PollinatedWallSignBlock>> TEST_SIGN = create(() -> Objects.requireNonNull(BLOCKS).registerSign("test", Material.WOOD, MaterialColor.COLOR_BLUE));
    public static final Supplier<TestMoonflowerType> BLUE_MOONFLOWER = create(() -> Objects.requireNonNull(MOONFLOWER_TYPES).registerDefaultValue("blue", () -> new TestMoonflowerType("Blue Moon Floweer")));

    static void onClient() {
        BlockRendererRegistry.register(Blocks.FLOWER_POT, new DebugPollenFlowerPotRenderer());
    }

    static void onCommon() {
        Objects.requireNonNull(ITEMS).register(Pollen.PLATFORM);
        Objects.requireNonNull(BLOCKS).register(Pollen.PLATFORM);
        Objects.requireNonNull(FLUIDS).register(Pollen.PLATFORM);
        Objects.requireNonNull(MOONFLOWER_TYPES).register(Pollen.PLATFORM);

        DispenseItemBehaviorRegistry.register(Blocks.DIAMOND_BLOCK, (source, stack) -> source.getLevel().getBlockState(new BlockPos(DispenserBlock.getDispensePosition(source))).getBlock() == Blocks.GOLD_BLOCK, new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                source.getLevel().setBlock(new BlockPos(DispenserBlock.getDispensePosition(source)), Blocks.DIAMOND_BLOCK.defaultBlockState(), 2);
                stack.shrink(1);
                return stack;
            }
        });

        DispenseItemBehaviorRegistry.register(Blocks.DIAMOND_BLOCK, (source, stack) -> source.getLevel().getBlockState(new BlockPos(DispenserBlock.getDispensePosition(source))).getBlock() == Blocks.EMERALD_BLOCK, new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                source.getLevel().setBlock(new BlockPos(DispenserBlock.getDispensePosition(source)), Blocks.GOLD_BLOCK.defaultBlockState(), 2);
                stack.shrink(1);
                return stack;
            }
        });
        CommandRegistryEvent.EVENT.register((dispatcher, context, selection) -> dispatcher.register(Commands.literal("moonflowers").executes(PollenTest::executeTestCommand)));
    }

    static void onClientPost(Platform.ModSetupContext context) {
    }

    static void onCommonPost(Platform.ModSetupContext context) {
        FlammabilityRegistry.register(Blocks.DIAMOND_BLOCK, 200, 50);
        CompostablesRegistry.register(Blocks.SAND, 1);
        FurnaceFuelRegistry.register(Items.BUCKET, 100);
    }

    private static <T> T create(Supplier<T> factory) {
        return !Pollen.TESTS_ENABLED ? null : factory.get();
    }

    public record TestMoonflowerType(String name) {

        public static final Codec<TestMoonflowerType> CODEC = create(() -> RecordCodecBuilder.create(instance -> instance
                .group(Codec.STRING.fieldOf("name").forGetter(TestMoonflowerType::name))
                .apply(instance, TestMoonflowerType::new)));
        public static final Codec<Holder<TestMoonflowerType>> REGISTRY_CODEC = create(() -> RegistryFileCodec.create(Objects.requireNonNull(MOONFLOWER_KEY), Objects.requireNonNull(CODEC)));

        @Override
        public String toString() {
            return "Moonflower{name=" + this.name + "}";
        }
    }

    private static int executeTestCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Registry<TestMoonflowerType> registry = Objects.requireNonNull(MOONFLOWER_TYPES).get(context.getSource().registryAccess());
        Component header = Component.literal("Found " + registry.entrySet().size() + " taters:").withStyle(ChatFormatting.GRAY);
        context.getSource().sendSuccess(header, false);
        registry.entrySet().forEach(entry -> {
            ResourceLocation id = entry.getKey().location();
            TestMoonflowerType moonflower = entry.getValue();
            context.getSource().sendSuccess(Component.literal("- ID:   " + id), false);
            context.getSource().sendSuccess(Component.literal("  Name: " + moonflower.name()), false);
        });
        return 1;
    }
}
