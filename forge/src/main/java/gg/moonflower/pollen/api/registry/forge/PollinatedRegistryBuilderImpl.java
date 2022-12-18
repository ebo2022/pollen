package gg.moonflower.pollen.api.registry.forge;

import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import gg.moonflower.pollen.api.registry.PollinatedRegistryBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PollinatedRegistryBuilderImpl<T> implements PollinatedRegistryBuilder<T> {

    private final RegistryBuilder<T> parent;
    private final ResourceLocation name;

    PollinatedRegistryBuilderImpl(ResourceLocation name) {
        this.parent = new RegistryBuilder<>();
        this.name = name;
    }

    @Override
    public PollinatedRegistryBuilder<T> disableSaving() {
        this.parent.disableSaving();
        return this;
    }

    @Override
    public PollinatedRegistryBuilder<T> disableSync() {
        this.parent.disableSync();
        return this;
    }

    @Override
    public PollinatedRegistry<T> build() {
        return new PollinatedRegistryImpl<>(this.name, this.parent);
    }
}