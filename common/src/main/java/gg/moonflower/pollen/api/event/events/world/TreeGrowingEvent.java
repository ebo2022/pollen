package gg.moonflower.pollen.api.event.events.world;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SaplingBlock;

import java.util.Random;

/**
 * Fired when a sapling grows into a tree.
 *
 * @author ebo2022
 * @since 2.0.0
 */
@FunctionalInterface
public interface TreeGrowingEvent {

    PollinatedEvent<TreeGrowingEvent> EVENT = EventRegistry.createResult(TreeGrowingEvent.class);

    /**
     * Called when a {@link SaplingBlock} is successfully grown.
     *
     * @param level The level the sapling is in
     * @param rand  An instance of {@link Random} for use in code
     * @param pos   The origin position of the sapling
     * @return The result for this interaction. {@link InteractionResult#PASS} will continue onto the next iteration, while any others will override vanilla behavior
     */
    InteractionResult interaction(LevelAccessor level, Random rand, BlockPos pos);
}
