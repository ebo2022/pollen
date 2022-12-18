package gg.moonflower.pollen.api.registry.fabric;

import com.mojang.serialization.Codec;
import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import gg.moonflower.pollen.api.registry.PollinatedRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class PollinatedRegistryBuilderImpl<T> implements PollinatedRegistryBuilder<T> {
    private boolean save = true;
    private boolean sync = true;
    private final FabricRegistryBuilder<T, MappedRegistry<T>> parent;
    private final ResourceLocation registryName;

    PollinatedRegistryBuilderImpl(FabricRegistryBuilder<T, MappedRegistry<T>> parent, ResourceLocation registryName) {
        this.parent = parent;
        this.registryName = registryName;
    }

    @Override
    public PollinatedRegistryBuilder<T> disableSaving() {
        this.save = false;
        return this;
    }

    @Override
    public PollinatedRegistryBuilder<T> disableSync() {
        this.sync = false;
        return this;
    }

    @Override
    public PollinatedRegistryBuilder<T> dataPackRegistry(Codec<T> codec, @Nullable Codec<T> networkCodec) {
        // TODO: setup data registration and early classloading
        return this;
    }

    @Override
    public PollinatedRegistry<T> build() {
        if (this.save)
            this.parent.attribute(RegistryAttribute.PERSISTED);
        if (this.sync)
            this.parent.attribute(RegistryAttribute.SYNCED);
        return new PollinatedRegistryImpl<>(this.parent.buildAndRegister(), this.registryName.getNamespace());
    }
}