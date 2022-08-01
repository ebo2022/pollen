package gg.moonflower.pollen.api.registry.content;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author ebo2022
 * @since 1.5.0
 */
public final class ShovelRegistry {

    private ShovelRegistry() {
    }

    /**
     * Registers a shovel flattening interaction for the specified blocks.
     *
     * @param from The block to add a flattening interaction for
     * @param to   The resultant block state the original block will be replaced with
     */
    @ExpectPlatform
    public static void register(Block from, BlockState to) {
        Platform.error();
    }
}
