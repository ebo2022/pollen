package gg.moonflower.pollen.core.mixin;

import gg.moonflower.pollen.api.registry.attachment.RegistryAttachment;
import gg.moonflower.pollen.core.extensions.ResourceKeyExtension;
import gg.moonflower.pollen.core.registry.attachment.RegistryAttachmentManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Mixin(ResourceKey.class)
public class ResourceKeyMixin<T> implements ResourceKeyExtension<T> {

    @Unique
    private final Map<ResourceLocation, RegistryAttachment<T, ?>> pollen_attachments = new HashMap<>();
    @Unique
    private RegistryAttachmentManager<T> pollen_builtinAttachmentManager;
    @Unique
    private RegistryAttachmentManager<T> pollen_dataAttachmentManager;

    @Override
    public void pollen_registerAttachment(RegistryAttachment<T, ?> attachment) {
        this.pollen_attachments.put(attachment.getId(), attachment);
    }

    @Override
    public @Nullable RegistryAttachment<T, ?> pollen_getAttachment(ResourceLocation id) {
        return this.pollen_attachments.get(id);
    }

    @Override
    public Set<Map.Entry<ResourceLocation, RegistryAttachment<T, ?>>> pollen_getAttachments() {
        return this.pollen_attachments.entrySet();
    }

    @Override
    public RegistryAttachmentManager<T> pollen_getBuiltinAttachmentManager() {
        return this.pollen_builtinAttachmentManager;
    }

    @Override
    public void pollen_setBuiltinAttachmentManager(RegistryAttachmentManager<T> manager) {
        this.pollen_builtinAttachmentManager = manager;
    }

    @Override
    public RegistryAttachmentManager<T> pollen_getDataAttachmentManager() {
        return this.pollen_dataAttachmentManager;
    }

    @Override
    public void pollen_setDataAttachmentManager(RegistryAttachmentManager<T> manager) {
        this.pollen_dataAttachmentManager = manager;
    }
}
