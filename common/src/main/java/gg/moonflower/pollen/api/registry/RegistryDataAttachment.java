package gg.moonflower.pollen.api.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import gg.moonflower.pollen.api.event.events.registry.RegisterDataAttachmentsEvent;

import java.util.function.Supplier;

/**
 * Allows for dynamic JSON registration of objects to the attached {@link PollinatedRegistry}.
 * <p>Only supports custom pollen registries.
 *
 * @param registry             The parent registry that dynamic objects should register to
 * @param codec                The codec to serialize and deserialize registry elements
 * @param defaultValueSupplier The default registry value
 * @param <T>                  The type of dynamic registry object
 *
 * @see RegisterDataAttachmentsEvent
 * @author ebo2022
 * @since 2.0.0
 */
public record RegistryDataAttachment<T>(PollinatedRegistry<T> registry, Codec<T> codec, Supplier<T> defaultValueSupplier) {

    /**
     * @return The {@link ResourceKey} of the attached registry
     */
    public ResourceKey<? extends Registry<T>> key() {
        return this.registry.key();
    }

    /**
     * @return The vanilla-facing registry
     */
    public Registry<T> vanillaRegistry() {
        return ((PollinatedRegistry.VanillaImpl<T>) this.registry).getRegistry();
    }

    /**
     * @return The lifecycle of this data attachment
     */
    public Lifecycle lifecycle() {
        return this.vanillaRegistry().lifecycle();
    }
}
