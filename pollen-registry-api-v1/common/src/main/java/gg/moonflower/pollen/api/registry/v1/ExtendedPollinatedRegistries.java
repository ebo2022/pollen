package gg.moonflower.pollen.api.registry.v1;

import com.mojang.serialization.Lifecycle;
import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.base.platform.Platform;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * Contains methods to create custom {@link PollinatedRegistry}s.
 *
 * @author ebo2022
 * @since 2.0.0
 */
public interface ExtendedPollinatedRegistries {

    /**
     * Creates a simple custom registry.
     * <p>This method creates a <b>vanilla</b>-backed registry on both platforms.
     *
     * @param registryId The registry {@link ResourceLocation} used as the registry id
     * @param <T>        The type stored in the Registry
     * @return A vanilla-backed custom {@link PollinatedRegistry}
     */
    static <T> PollinatedRegistry<T> createSimple(ResourceLocation registryId) {
        return PollinatedRegistry.createVanilla(new MappedRegistry<>(ResourceKey.createRegistryKey(registryId), Lifecycle.stable(), null), registryId.getNamespace());
    }

    /**
     * Creates a simple custom registry.
     * <p>This method creates a <b>vanilla</b>-backed registry on both platforms.
     * <p>If a queried entry doesn't exist, this registry will return the assigned default registry ID.
     *
     * @param registryId The registry {@link ResourceLocation} used as the registry id
     * @param defaultId  The default registry id
     * @param <T>        The type stored in the Registry
     * @return An defaulted, vanilla-backed custom {@link PollinatedRegistry}
     */
    static <T> PollinatedRegistry<T> createDefaulted(ResourceLocation registryId, ResourceLocation defaultId) {
        return PollinatedRegistry.createVanilla(new DefaultedRegistry<>(defaultId.toString(), ResourceKey.createRegistryKey(registryId), Lifecycle.stable(), null), registryId.getNamespace());
    }

    /**
     * Creates a {@link PollinatedRegistryBuilder} for the specified registry id.
     * <p>Built registries are more versatile and provide automatic syncing between servers and clients.
     * <p>If you don't require more advanced registry mechanics, use {@link #createSimple(ResourceLocation)} instead.
     *
     * @param registryId     The registry {@link ResourceLocation} that will be used as the registry id
     * @param <T>            The type to be stored in the registry
     * @return A new {@link PollinatedRegistryBuilder}
     */
    @SafeVarargs
    @ExpectPlatform
    static <T> PollinatedRegistryBuilder<T> registryBuilder(ResourceLocation registryId, T... typeIdentifier) {
        return Platform.error();
    }
}
