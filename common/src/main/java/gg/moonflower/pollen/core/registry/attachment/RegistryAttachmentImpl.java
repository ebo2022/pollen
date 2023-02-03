package gg.moonflower.pollen.core.registry.attachment;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.registry.attachment.DefaultValueProvider;
import gg.moonflower.pollen.api.registry.attachment.RegistryAttachment;
import gg.moonflower.pollen.api.util.CodecHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@ApiStatus.Internal
public abstract class RegistryAttachmentImpl<T, R> implements RegistryAttachment<T, R> {

    static final Logger LOGGER = LogManager.getLogger("PollinatedRegistryAttachment");
    static final Logger COMPUTE_LOGGER = LogManager.getLogger("PollinatedRegistryAttachment|Compute");

    protected final Registry<T> registry;
    protected final ResourceLocation id;
    protected final Class<R> valueClass;
    protected final Codec<R> codec;
    protected final Side side;

    public RegistryAttachmentImpl(Registry<T> registry, ResourceLocation id, Class<R> valueClass, Codec<R> codec, Side side) {
        this.registry = registry;
        this.id = id;
        this.valueClass = valueClass;
        this.codec = codec;
        this.side = side;
    }

    @Override
    public Registry<T> getRegistry() {
        return this.registry;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public Codec<R> getCodec() {
        return this.codec;
    }

    @Override
    public Side getSide() {
        return this.side;
    }

    @Override
    public @Nullable R get(T entry) {
        if (this.side == Side.CLIENT)
            Platform.assertClientAccess();
        R value = RegistryAttachmentManager.loadedData(this.registry).getDirectOrTagValue(this, entry);
        if (value != null)
            return value;
        value = RegistryAttachmentManager.builtin(this.registry).getDirectOrTagValue(this, entry);
        if (value != null) {
            return value;
        }
        return this.defaultOrNull(entry);
    }

    @Nullable
    public abstract R defaultOrNull(T entry);

    @Override
    public Set<T> keySet() {
        if (this.side == Side.CLIENT)
            Platform.assertClientAccess();
        Set<T> set = new HashSet<>();
        set.addAll(RegistryAttachmentManager.loadedData(this.registry).getDirectValues(this).keySet());
        set.addAll(RegistryAttachmentManager.builtin(this.registry).getDirectValues(this).keySet());
        return set;
    }

    @Override
    public Set<Entry<T, R>> entrySet() {
        if (this.side == Side.CLIENT)
            Platform.assertClientAccess();
        return this.keySet().stream().map(key -> {
            R value = RegistryAttachmentManager.loadedData(this.registry).getDirectValue(this, key);
            if (value == null)
                value = RegistryAttachmentManager.builtin(this.registry).getDirectValue(this, key);
            return new Entry<>(key, value);
        }).collect(Collectors.toSet());
    }

    @Override
    public Set<TagKey<T>> tagKeySet() {
        if (this.side == Side.CLIENT)
            Platform.assertClientAccess();
        Set<TagKey<T>> set = new HashSet<>();
        set.addAll(RegistryAttachmentManager.loadedData(this.registry).getTagValues(this).keySet());
        set.addAll(RegistryAttachmentManager.builtin(this.registry).getTagValues(this).keySet());
        return set;
    }

    @Override
    public Set<TagEntry<T, R>> tagEntrySet() {
        if (this.side == Side.CLIENT)
            Platform.assertClientAccess();
        return this.tagKeySet().stream().map(key -> {
            R value = RegistryAttachmentManager.loadedData(this.registry).getTagValue(this, key);
            if (value == null)
                value = RegistryAttachmentManager.builtin(this.registry).getTagValue(this, key);
            return new TagEntry<>(key, value);
        }).collect(Collectors.toSet());
    }

    @Override
    public Class<R> valueClass() {
        return this.valueClass;
    }

    @NotNull
    @Override
    public Iterator<Entry<T, R>> iterator() {
        return this.registry.stream()
                .map(entry -> {
                    R value = this.get(entry);
                    return value == null ? null : new Entry<>(entry, value);
                })
                .filter(Objects::nonNull)
                .iterator();
    }

    public static final class ConstantDefaultValue<T, R> extends RegistryAttachmentImpl<T, R> {

        @Nullable
        private final R defaultValue;

        public ConstantDefaultValue(Registry<T> registry, ResourceLocation id, Class<R> valueClass, Codec<R> codec, Side side, @Nullable R defaultValue) {
            super(registry, id, valueClass, codec, side);
            if (defaultValue != null)
                CodecHelper.validate(this.getCodec(), defaultValue);
            this.defaultValue = defaultValue;
        }

        @Override
        @Nullable
        public R defaultOrNull(T entry) {
            return this.defaultValue;
        }
    }

    public static final class ComputedDefaultValue<T, R> extends RegistryAttachmentImpl<T, R> {

        private final DefaultValueProvider<T, R> defaultValueProvider;

        public ComputedDefaultValue(Registry<T> registry, ResourceLocation id, Class<R> valueClass, Codec<R> codec, Side side, DefaultValueProvider<T, R> defaultValueProvider) {
            super(registry, id, valueClass, codec, side);
            this.defaultValueProvider = defaultValueProvider;
        }

        @Override
        @Nullable
        public R defaultOrNull(T entry) {
            DefaultValueProvider.ComputationResult<R> result = this.defaultValueProvider.compute(entry);
            if (result.isFailure()) {
                COMPUTE_LOGGER.error("Failed to compute defaulted value for registry value {}: {}", this.registry.getId(entry), result.error());
                return null;
            } else {
                R value = result.get();
                DataResult<JsonElement> encoded = codec.encodeStart(JsonOps.INSTANCE, value);
                if (encoded.result().isEmpty()) {
                    if (encoded.error().isPresent()) {
                        COMPUTE_LOGGER.error("Computed invalid value for entry {}: {}", this.registry.getId(entry), encoded.error().get().message());
                    } else {
                        COMPUTE_LOGGER.error("Computed invalid value for entry {}: unknown error", this.registry.getId(entry));
                    }
                    return null;
                }
                RegistryAttachmentManager.builtin(this.registry).put(this, entry, value);
                return value;
            }
        }
    }
}
