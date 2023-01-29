package gg.moonflower.pollen.core.mixin.forge;

import gg.moonflower.pollen.api.registry.resource.PollinatedPreparableReloadListener;
import gg.moonflower.pollen.api.registry.resource.ReloadListenerKeys;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.ServerFunctionLibrary;
import net.minecraft.tags.TagManager;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.storage.loot.LootTables;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Locale;

@Mixin({RecipeManager.class, ServerAdvancementManager.class, ServerFunctionLibrary.class, LootTables.class, TagManager.class})
public abstract class PreparableReloadListenerMixin implements PollinatedPreparableReloadListener {

    private ResourceLocation pollenId;

    // Mirrors Fabric assigning vanilla reloaders IDs so they can be used as dependencies
    @Override
    public ResourceLocation getPollenId() {
        if (this.pollenId == null) {
            Object self = this;
            if (self instanceof RecipeManager) {
                this.pollenId = ReloadListenerKeys.RECIPES;
            } else if (self instanceof ServerAdvancementManager) {
                this.pollenId = ReloadListenerKeys.ADVANCEMENTS;
            } else if (self instanceof ServerFunctionLibrary) {
                this.pollenId = ReloadListenerKeys.FUNCTIONS;
            } else if (self instanceof LootTables) {
                this.pollenId = ReloadListenerKeys.LOOT_TABLES;
            } else if (self instanceof TagManager) {
                this.pollenId = ReloadListenerKeys.TAGS;
            } else {
                this.pollenId = new ResourceLocation("minecraft", "private/" + self.getClass().getSimpleName().toLowerCase(Locale.ROOT));
            }
        }
        return this.pollenId;
    }
}