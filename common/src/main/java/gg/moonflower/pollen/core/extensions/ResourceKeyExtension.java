package gg.moonflower.pollen.core.extensions;

import gg.moonflower.pollen.api.registry.attachment.RegistryAttachment;
import gg.moonflower.pollen.core.registry.attachment.RegistryAttachmentManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public interface ResourceKeyExtension<T> {

    void pollen_registerAttachment(RegistryAttachment<T, ?> attachment);

    @Nullable
    RegistryAttachment<T, ?> pollen_getAttachment(ResourceLocation id);

    Set<Map.Entry<ResourceLocation, RegistryAttachment<T, ?>>> pollen_getAttachments();

    RegistryAttachmentManager<T> pollen_getBuiltinAttachmentManager();

    void pollen_setBuiltinAttachmentManager(RegistryAttachmentManager<T> manager);

    RegistryAttachmentManager<T> pollen_getDataAttachmentManager();

    void pollen_setDataAttachmentManager(RegistryAttachmentManager<T> manager);
}
