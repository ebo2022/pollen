package gg.moonflower.pollen.api.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RegistryProperties<T> {

    private final List<OnAdd<T>> onAdd = new ArrayList<>();
    private boolean saveToDisk = true;
    private boolean syncToClients = true;

    public RegistryProperties<T> disableSaving() {
        this.saveToDisk = false;
        return this;
    }

    public RegistryProperties<T> disableSync() {
        this.syncToClients = false;
        return this;
    }

    public RegistryProperties<T> onAdd(OnAdd<T> onAdd) {
        this.onAdd.add(onAdd);
        return this;
    }

    public boolean shouldSave() {
        return this.saveToDisk;
    }

    public boolean shouldSync() {
        return this.syncToClients;
    }

    public List<OnAdd<T>> getOnAdd() {
        return Collections.unmodifiableList(this.onAdd);
    }

    /**
     * A consumer that fires when a registry entry is added.
     *
     * @param <T> The registry type
     */
    @FunctionalInterface
    public interface OnAdd<T> {

        /**
         * Called when a new object is added to the registry.
         *
         * @param id     The raw numeric id
         * @param name   The name of the object
         * @param object The object registered
         */
        void onAdd(int id, ResourceLocation name, T object);
    }
}
