package gg.moonflower.pollen.api.registry.content.fabric;

import net.fabricmc.fabric.api.registry.FlattenableBlockRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ShovelRegistryImpl {

    public static void register(Block from, BlockState to) {
        FlattenableBlockRegistry.register(from, to);
    }
}
