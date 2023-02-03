package gg.moonflower.pollen.core.network.play;

import gg.moonflower.pollen.api.network.packet.PollinatedPacket;
import gg.moonflower.pollen.api.network.packet.PollinatedPacketContext;
import gg.moonflower.pollen.api.registry.attachment.RegistryAttachment;
import gg.moonflower.pollen.core.registry.attachment.RegistryAttachmentManager;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClientboundSyncRegistryAttachmentPacket implements PollinatedPacket<PollenClientPlayPacketHandler> {

    private static final Map<ResourceLocation, CacheEntry> CACHE = new HashMap<>();

    private final ResourceLocation registryId;
    private final ResourceLocation attachmentId;
    private final String namespace;
    private final int size;
    private final Set<AttachmentEntry> entries;

    public ClientboundSyncRegistryAttachmentPacket(ResourceLocation registryId, ResourceLocation attachmentId, String namespace, int size, Set<AttachmentEntry> entries) {
        this.registryId = registryId;
        this.attachmentId = attachmentId;
        this.namespace = namespace;
        this.size = size;
        this.entries = entries;
    }

    public ClientboundSyncRegistryAttachmentPacket(FriendlyByteBuf buf) {
        this.registryId = buf.readResourceLocation();
        this.attachmentId = buf.readResourceLocation();
        this.namespace = buf.readUtf();
        this.size = buf.readInt();

        int size1 = this.size;
        Set<AttachmentEntry> set = new HashSet<>();
        while (size1 > 0) {
            set.add(AttachmentEntry.read(buf));
            size1--;
        }
        this.entries = set;
    }

    @Override
    public void writePacketData(FriendlyByteBuf buf) throws IOException {
        buf.writeResourceLocation(this.registryId);
        buf.writeResourceLocation(this.attachmentId);
        buf.writeUtf(this.namespace);
        buf.writeInt(this.size);
        this.entries.forEach(entry -> entry.write(buf));
    }

    @Override
    public void processPacket(PollenClientPlayPacketHandler handler, PollinatedPacketContext ctx) {
        handler.handleSyncRegistryAttachmentPacket(this, ctx);
    }

    @SuppressWarnings("unchecked")
    private static void fillCache() {
        if (!CACHE.isEmpty())
            return;
        for (var registryEntry : Registry.REGISTRY.entrySet()) {
            var registry = (Registry<Object>) registryEntry.getValue();
            var dataHolder = RegistryAttachmentManager.loadedData(registry);

            for (var attachmentEntry : RegistryAttachmentManager.getAttachmentsForRegistry(registry)) {
                var attachment = (RegistryAttachment<Object, Object>) attachmentEntry.getValue();
                if (attachment.getSide() != RegistryAttachment.Side.COMMON)
                    continue;

                // Namespace, Attachment
                var encoded = new HashMap<String, Set<AttachmentEntry>>();
                Map<Object, Object> entryValues = dataHolder.getDirectValues(attachment);
                if (!entryValues.isEmpty()) {
                    for (var valueEntry : entryValues.entrySet()) {
                        var entryId = registry.getKey(valueEntry.getKey());
                        if (entryId == null)
                            throw new IllegalStateException("Foreign object in data holder of attachment " + attachment.getId() + ": " + valueEntry.getKey());

                        encoded.computeIfAbsent(entryId.getNamespace(), __ -> new HashSet<>()).add(
                                new AttachmentEntry(entryId.getPath(), false, attachment.getCodec()
                                        .encodeStart(NbtOps.INSTANCE, valueEntry.getValue())
                                        .getOrThrow(false, msg -> {
                                            throw new IllegalStateException("Failed to encode value for attachment " + attachment.getId() + " of registry entry " + entryId + ": " + msg);
                                        })
                                )
                        );
                    }
                }

                Map<TagKey<Object>, Object> entryTagValues = dataHolder.getTagValues(attachment);
                if (entryTagValues != null) {
                    for (var valueEntry : entryTagValues.entrySet()) {
                        encoded.computeIfAbsent(valueEntry.getKey().location().getNamespace(), id -> new HashSet<>()).add(
                                new AttachmentEntry(valueEntry.getKey().location().getPath(), true, attachment.getCodec()
                                        .encodeStart(NbtOps.INSTANCE, valueEntry.getValue())
                                        .getOrThrow(false, msg -> {
                                            throw new IllegalStateException("Failed to encode value for attachment tag " + attachment.getId() +" of registry " + valueEntry.getKey().location() +": " + msg);
                                        })));
                    }
                }

                var valueMaps = new HashSet<NamespaceValuePair>();
                for (var namespaceEntry : encoded.entrySet()) {
                    valueMaps.add(new NamespaceValuePair(namespaceEntry.getKey(), namespaceEntry.getValue()));
                }

                CACHE.put(attachment.getId(), new CacheEntry(attachment.getRegistry().key().location(), valueMaps));
            }
        }
    }

    private record NamespaceValuePair(String namespace, Set<AttachmentEntry> entries) {
    }

    private record CacheEntry(ResourceLocation registryId, Set<NamespaceValuePair> namespacesToValues) {
    }

    private record AttachmentEntry(String path, boolean isTag, Tag value) {
        public void write(FriendlyByteBuf buf) {
            buf.writeUtf(this.path);
            buf.writeBoolean(this.isTag);

            CompoundTag compound = new CompoundTag();
            compound.put("value", this.value);
            buf.writeNbt(compound);
        }

        public static AttachmentEntry read(FriendlyByteBuf buf) {
            String path = buf.readUtf();
            boolean isTag = buf.readBoolean();
            Tag value = buf.readNbt().get("value");

            return new AttachmentEntry(path, isTag, value);
        }
    }
}
