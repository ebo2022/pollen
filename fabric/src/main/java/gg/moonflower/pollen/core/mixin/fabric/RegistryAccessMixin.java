package gg.moonflower.pollen.core.mixin.fabric;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.RegistryAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(RegistryAccess.class)
public class RegistryAccessMixin {

    @Mod(method = "lambda.static.1", )
}
