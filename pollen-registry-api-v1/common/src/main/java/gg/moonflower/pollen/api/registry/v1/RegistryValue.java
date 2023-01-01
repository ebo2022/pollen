package gg.moonflower.pollen.api.registry.v1;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * A reference to an object registered by a {@link PollinatedRegistry}.
 *
 * @param <T> The object type
 * @author ebo2022
 * @since 1.7.0
 */
public interface RegistryValue<T> extends Supplier<T> {

    @Override
    T get();

    Optional<Holder<T>> getHolder();

    boolean isPresent();

    ResourceLocation getName();

    ResourceKey<T> getKey();
}
