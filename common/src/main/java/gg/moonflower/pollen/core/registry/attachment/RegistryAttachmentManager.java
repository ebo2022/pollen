package gg.moonflower.pollen.core.registry.attachment;

import gg.moonflower.pollen.api.registry.attachment.RegistryAttachment;
import gg.moonflower.pollen.core.extensions.ResourceKeyExtension;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ApiStatus.Internal
public class RegistryAttachmentManager<T> {

    @SuppressWarnings("unchecked")
    private static <T> ResourceKeyExtension<T> extension(Registry<T> registry) {
        return (ResourceKeyExtension<T>) registry.key();
    }

    public static <T, R> void register(Registry<T> registry, RegistryAttachment<T, R> attachment) {
        extension(registry).pollen_registerAttachment(attachment);
    }

    public static <T> Set<Map.Entry<ResourceLocation, RegistryAttachment<T, ?>>> getAttachmentsForRegistry(Registry<T> registry) {
        return extension(registry).pollen_getAttachments();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T, R> RegistryAttachment<T, R> get(Registry<T> registry, ResourceLocation id, Class<R> clazz) {
        RegistryAttachment<T, ?> attachment = extension(registry).pollen_getAttachment(id);
        if (attachment == null)
            return null;
        if (clazz != attachment.valueClass()) {
            throw new IllegalArgumentException(("Found attachment with ID \"%s\" for registry \"%s\", "
                    + "but it has the wrong value class (expected %s, got %s)")
                    .formatted(id, registry.key().location(), clazz, attachment.valueClass()));
        }
        return (RegistryAttachmentImpl<T, R>) attachment;
    }

    public static <T> RegistryAttachmentManager<T> builtin(Registry<T> registry) {
        ResourceKeyExtension<T> extension = extension(registry);
        RegistryAttachmentManager<T> manager = extension.pollen_getBuiltinAttachmentManager();
        if (manager == null)
            extension.pollen_setBuiltinAttachmentManager(manager = new RegistryAttachmentManager<>());
        return manager;
    }

    public static <T> RegistryAttachmentManager<T> loadedData(Registry<T> registry) {
        ResourceKeyExtension<T> extension = extension(registry);
        RegistryAttachmentManager<T> manager = extension.pollen_getDataAttachmentManager();
        if (manager == null)
            extension.pollen_setDataAttachmentManager(manager = new RegistryAttachmentManager<>());
        return manager;
    }

    public final Map<RegistryAttachment<T, ?>, Map<T, ?>> valueTracker;
    private final Map<RegistryAttachment<T, ?>, Map<T, ?>> tagValueTracker;

    private RegistryAttachmentManager() {
        this.valueTracker = new HashMap<>();
        this.tagValueTracker = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <R> Map<T, R> getDirectValues(RegistryAttachment<T, R> attachment) {
        return (Map<T, R>) this.valueTracker.computeIfAbsent(attachment, __ -> new HashMap<>());
    }

    @SuppressWarnings("unchecked")
    public <R> Map<TagKey<T>, R> getTagValues(RegistryAttachment<T, R> attachment) {
        return (Map<TagKey<T>, R>) this.tagValueTracker.computeIfAbsent(attachment, __ -> new HashMap<>());
    }

    public <R> R getDirectValue(RegistryAttachment<T, R> attachment, T key) {
        return this.getDirectValues(attachment).get(key);
    }

    public <R> R getTagValue(RegistryAttachment<T, R> attachment, TagKey<T> key) {
        return this.getTagValues(attachment).get(key);
    }

    public <R> R getDirectOrTagValue(RegistryAttachment<T, R> attachment, T key) {
        R value =  this.getDirectValue(attachment, key);
        if (value == null) {
            Map<TagKey<T>, R> tags = this.getTagValues(attachment);
            for (Map.Entry<TagKey<T>, R> tagEntry : tags.entrySet()) {
                for (Holder<T> holder : attachment.getRegistry().getTagOrEmpty(tagEntry.getKey())) {
                    if (holder.value().equals(key)) {
                        if (value != null) {
                            RegistryAttachmentImpl.LOGGER.warn("Value {} for registry {} already has attachment {} defined. Overriding with value from tag {}.",
                                    attachment.getRegistry().getKey(key),
                                    attachment.getRegistry().key().location(),
                                    attachment.getId(),
                                    tagEntry.getKey().location());
                        }
                        value = tagEntry.getValue();
                    }
                }
            }
        }

        return value;
    }

    public <R> void put(RegistryAttachment<T, R> attachment, T entry, R value) {
        this.getDirectValues(attachment).put(entry, value);
    }

    public <R> void put(RegistryAttachment<T, R> attachment, TagKey<T> tag, R value) {
        this.getTagValues(attachment).put(tag, value);
    }
}