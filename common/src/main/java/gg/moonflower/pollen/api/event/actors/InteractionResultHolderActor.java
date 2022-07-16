package gg.moonflower.pollen.api.event.actors;

import net.minecraft.world.InteractionResultHolder;

/**
 * A functional interface to handle events that return an {@link InteractionResultHolder}.
 *
 * @param <T> The event to use as a parameter
 * @author ebo2022
 * @since 2.0.0
 */
@FunctionalInterface
public interface InteractionResultHolderActor<T> {
    InteractionResultHolder<?> invoke(T event);
}
