package gg.moonflower.pollen.impl.registry;

import gg.moonflower.pollen.api.registry.v1.RegistryValue;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

@ApiStatus.Internal
public class VanillaRegistryValue<T, R extends T> implements RegistryValue<R> {

    private final R value;
    private final ResourceLocation name;
    private final ResourceKey<R> key;
    private final Registry<T> registry;

    @SuppressWarnings("unchecked")
    public VanillaRegistryValue(R value, Registry<T> registry, ResourceLocation name) {
        this.value = value;
        this.registry = registry;
        this.name = name;
        this.key = ResourceKey.create((ResourceKey<? extends Registry<R>>) registry.key(), name);
    }

    @Override
    public R get() {
        return this.value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Holder<R>> getHolder() {
        Holder<R> holder = (Holder<R>) this.registry.getHolder((ResourceKey<T>) this.key).orElse(null);
        return Optional.ofNullable(holder);
    }

    @Override
    public boolean isPresent() {
        return this.registry.containsKey(this.name);
    }

    @Override
    public ResourceLocation getName() {
        return this.name;
    }

    @Override
    public ResourceKey<R> getKey() {
        return this.key;
    }
}
