package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(BuiltinRegistries.class)
public interface BuiltinRegistriesAccessor {

    @Accessor("LOADERS")
    static Map<ResourceLocation, Supplier<? extends Holder<?>>> getLoaders() {
        return Platform.error();
    }
}
