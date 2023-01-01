package gg.moonflower.pollen.api.registry.v1.fabric;

import gg.moonflower.pollen.api.registry.v1.PollinatedRegistry;
import gg.moonflower.pollen.api.registry.v1.PollinatedRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.MappedRegistry;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PollinatedRegistryBuilderImpl<T> implements PollinatedRegistryBuilder<T> {

    private final FabricRegistryBuilder<T, MappedRegistry<T>> builder;
    private final String modId;
    private boolean saveToDisk = true;
    private boolean sync = true;

    PollinatedRegistryBuilderImpl(FabricRegistryBuilder<T, MappedRegistry<T>> builder, String modId) {
        this.builder = builder;
        this.modId = modId;
    }

    @Override
    public PollinatedRegistryBuilder<T> disableSaving() {
        this.saveToDisk = false;
        return this;
    }

    @Override
    public PollinatedRegistryBuilder<T> disableSync() {
        this.sync = false;
        return this;
    }

    @Override
    public PollinatedRegistry<T> build() {
        if (this.saveToDisk)
            this.builder.attribute(RegistryAttribute.PERSISTED);
        if (this.sync)
            this.builder.attribute(RegistryAttribute.SYNCED);
        return PollinatedRegistry.createVanilla(this.builder.buildAndRegister(), this.modId);
    }
}
