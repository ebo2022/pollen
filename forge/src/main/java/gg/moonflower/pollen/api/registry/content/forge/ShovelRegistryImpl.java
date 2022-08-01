package gg.moonflower.pollen.api.registry.content.forge;

import gg.moonflower.pollen.core.mixin.forge.ShovelItemAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ShovelRegistryImpl {

    public static void register(Block from, BlockState to) {
        ShovelItemAccessor.getFlattenables().put(from, to);
    }
}
