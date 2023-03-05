package gg.moonflower.pollen.api.registry.fabric;

import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import gg.moonflower.pollen.api.registry.RegistryProperties;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Objects;

@ApiStatus.Internal
public class PollinatedRegistryImpl<T> {

    public static <T> PollinatedRegistry<T> create(ResourceKey<? extends Registry<T>> registryKey, String modId) {
       return PollinatedRegistry.createVanilla(registryKey, modId);
    }

    public static <T> PollinatedRegistry<T> create(PollinatedRegistry<T> registry, String modId) {
        return PollinatedRegistry.createVanilla(((PollinatedRegistry.VanillaImpl<T>) registry).getRegistry(), modId);
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T> PollinatedRegistry<T> create(ResourceLocation registryId, RegistryProperties<T> properties, T... typeGetter) {
        FabricRegistryBuilder<T, MappedRegistry<T>> builder = FabricRegistryBuilder.createSimple((Class<T>)typeGetter.getClass().getComponentType(), registryId);
        if (properties.shouldSave())
            builder.attribute(RegistryAttribute.PERSISTED);
        if (properties.shouldSync())
            builder.attribute(RegistryAttribute.SYNCED);
        Registry<T> registry = builder.buildAndRegister();
        List<RegistryProperties.OnAdd<T>> onAdd = properties.getOnAdd();
        if (!onAdd.isEmpty())
            RegistryEntryAddedCallback.event(registry).register((id, name, object) -> onAdd.forEach(c -> c.onAdd(id, name, object)));
        return PollinatedRegistry.createVanilla(registry, registryId.getNamespace());
    }
}
