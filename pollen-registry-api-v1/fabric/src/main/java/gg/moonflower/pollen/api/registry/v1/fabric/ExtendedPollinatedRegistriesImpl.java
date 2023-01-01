package gg.moonflower.pollen.api.registry.v1.fabric;

import gg.moonflower.pollen.api.registry.v1.PollinatedRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.resources.ResourceLocation;

public class ExtendedPollinatedRegistriesImpl {

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T> PollinatedRegistryBuilder<T> registryBuilder(ResourceLocation registryId, T... typeIdentifier) {
        return new PollinatedRegistryBuilderImpl<>(FabricRegistryBuilder.createSimple((Class<T>) typeIdentifier.getClass().getComponentType(), registryId), registryId.getNamespace());
    }
}
