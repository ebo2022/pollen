package gg.moonflower.pollen.core.mixin.forge;

import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ShovelItem.class)
public interface ShovelItemAccessor {

    @Accessor
    static Map<Block, BlockState> getFlattenables() {
        return Platform.error();
    }
}
