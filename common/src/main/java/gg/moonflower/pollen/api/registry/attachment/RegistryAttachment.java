package gg.moonflower.pollen.api.registry.attachment;

import com.mojang.serialization.Codec;
import gg.moonflower.pollen.core.registry.attachment.RegistryAttachmentImpl;
import gg.moonflower.pollen.core.registry.attachment.RegistryAttachmentManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Represents an arbitrary value that can be attached to values of a given {@link Registry}.
 *
 * @param <T> The registry type
 * @param <R> The type of value to associate with registry entries
 * @author ebo2022
 * @since
 */
public interface RegistryAttachment<T, R> extends Iterable<RegistryAttachment.Entry<T, R>> {


    Registry<T> getRegistry();

    ResourceLocation getId();

    Codec<R> getCodec();

    Side getSide();

    @Nullable
    R get(T entry);

    default Optional<R> getOptional(T value) {
        return Optional.ofNullable(this.get(value));
    }

    /**
     * @return A stream of all registered entries (this also includes computed default values, if a fixed value or provider is present)
     */
    default Stream<Entry<T, R>> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    /**
     * @return A set of all registry values with a directly associated attachment
     */
    Set<T> keySet();

    /**
     * @return A set of all directly registered entries
     */
    Set<Entry<T, R>> entrySet();

    /**
     * @return A set of all tag keys with an associated attachment
     */
    Set<TagKey<T>> tagKeySet();

    /**
     * @return A set of all registered tag entries
     */
    Set<TagEntry<T, R>> tagEntrySet();

    Class<R> valueClass();


    /**
     * Represents an association between a registry value and its attachment.
     *
     * @param entry The registry value
     * @param value The attachment linked to the registry value
     * @param <T>   The registry type
     * @param <R>   The attachment type
     */
    record Entry<T, R>(T entry, R value) {
    }

    /**
     * Represents an association between a tag and an attachment linked to the tag's entries. These cannot be computed.
     *
     * @param tag   The registry tag key
     * @param value The attachment linked to the tag
     * @param <T>   The registry type
     * @param <R>   The attachment type
     */
    record TagEntry<T, R>(TagKey<T> tag, R value) {
    }

    /**
     * Fired when a registry entry is associated with an attached value.
     *
     * @param <R> The registry type
     * @param <V> The attached value type
     */
    @FunctionalInterface
    interface ValueAdded<R, V> {
        void onValueAdded(R entry, V value);
    }

    /**
     * Fired when a tag key is associated with an attached value.
     *
     * @param <R> The registry type
     * @param <V> The attached value type
     */
    @FunctionalInterface
    interface TagValueAdded<R, V> {
        void onTagValueAdded(TagKey<R> tag, V value);
    }

    /**
     * Defines which environments a registry attachment can be loaded in.
     *
     * @since
     */
    enum Side {

        /**
         * Attachments of this type only exist on the client.
         */
        CLIENT(PackType.CLIENT_RESOURCES),

        /**
         * Attachments of this type exist on both sides. Registry values are automatically synchronized from server to client.
         */
        COMMON(PackType.SERVER_DATA),

        /**
         * Attachments of this type exist only on servers.
         */
        SERVER(PackType.SERVER_DATA);

        private final PackType packType;

        Side(PackType packType) {
            this.packType = packType;
        }

        /**
         * @return The pack type the side is predominantly loaded on
         */
        public PackType getPackType() {
            return this.packType;
        }

        /**
         * Checks whether the attachment type could load based on the specified pack type.
         *
         * @param  type The pack type to check
         * @return Whether the attachment type can load based on the specified pack type
         */
        public boolean shouldLoad(PackType type) {
            return this.packType == type;
        }
    }

    /**
     * A builder to create a new attachment.
     *
     * @param <T> The registry value type
     * @param <R> The attachment type
     * @since
     */
    final class Builder<T, R> {
        private final Registry<T> registry;
        private final ResourceLocation id;
        private final Class<R> valueClass;
        private final Codec<R> codec;

        private Side side;

        @Nullable
        private R defaultValue;

        @Nullable
        private DefaultValueProvider<T, R> defaultValueProvider;

        private Builder(Registry<T> registry, ResourceLocation id, Class<R> valueClass, Codec<R> codec) {
            this.registry = registry;
            this.id = id;
            this.valueClass = valueClass;
            this.codec = codec;
            this.side = Side.COMMON;

            if (RegistryAttachmentManager.get(registry, id, valueClass) != null) {
                throw new IllegalStateException("Attachment with ID '%s' is already registered for registry %s!"
                        .formatted(id, registry.key().location()));
            }
        }

        public Builder<T, R> side(Side side) {
            this.side = side;
            return this;
        }

        /**
         * Set the default value of this attachment to be the provided constant value. It must be compatible with the provided {@link Codec}.
         *
         * @param defaultValue The default value
         */
        public Builder<T, R> defaultValue(@Nullable R defaultValue) {
            this.defaultValue = defaultValue;
            this.defaultValueProvider = null;
            return this;
        }

        /**
         * Sets the default value of this attachment to be computed based on the registry value. Any returned value should be compatible with the provided {@link Codec}.
         */
        public Builder<T, R> defaultValueProvider(@Nullable DefaultValueProvider<T, R> defaultValueProvider) {
            this.defaultValueProvider = defaultValueProvider;
            this.defaultValue = null;
            return this;
        }

        /**
         * Builds a new attachment with the given parameters.
         *
         * @return The new attachment
         */
        public RegistryAttachment<T, R> build() {
            RegistryAttachment<T, R> attachment;
            if (this.defaultValueProvider == null) {
                attachment = new RegistryAttachmentImpl.ConstantDefaultValue<>(this.registry, this.id, this.valueClass,
                        this.codec, this.side, this.defaultValue);
            } else {
                attachment = new RegistryAttachmentImpl.ComputedDefaultValue<>(this.registry, this.id, this.valueClass,
                        this.codec, this.side, this.defaultValueProvider);
            }
            RegistryAttachmentManager.register(this.registry, attachment);
            return attachment;
        }
    }
}
