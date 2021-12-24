package gg.moonflower.pollen.api.registry.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class BlockEntityRendererRegistry {

    private BlockEntityRendererRegistry() {
    }

    @ExpectPlatform
    public static <T extends BlockEntity> void register(BlockEntityType<T> type, BlockEntityRendererProvider<T> factory) {
        Platform.error();
    }
}
