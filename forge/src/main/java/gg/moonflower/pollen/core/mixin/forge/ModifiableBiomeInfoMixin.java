package gg.moonflower.pollen.core.mixin.forge;

import gg.moonflower.pollen.api.biome.modification.PollinatedBiomeModifier;
import gg.moonflower.pollen.api.biome.modification.forge.BiomeModifierHook;
import gg.moonflower.pollen.api.biome.modification.forge.PollinatedBiomeInfoImpl;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(ModifiableBiomeInfo.class)
public class ModifiableBiomeInfoMixin implements BiomeModifierHook {

    @Unique
    private static List<PollinatedBiomeModifier> pollenModifiers;

    @Override
    public void pollen_setModifiers(List<PollinatedBiomeModifier> modifiers) {
        pollenModifiers = modifiers;
    }

    @Inject(method = "applyBiomeModifiers", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/world/BiomeModifier$Phase;values()[Lnet/minecraftforge/common/world/BiomeModifier$Phase;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    public void applyPollenModifiers(Holder<Biome> biome, List<BiomeModifier> biomeModifiers, CallbackInfo ci, ModifiableBiomeInfo.BiomeInfo info, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        for (PollinatedBiomeModifier.Phase phase : PollinatedBiomeModifier.Phase.values()) {
            for (PollinatedBiomeModifier modifier : pollenModifiers) {
                if (modifier.test(biome, phase)) {
                    modifier.apply(new PollinatedBiomeInfoImpl(builder));
                }
            }
        }
    }
}
