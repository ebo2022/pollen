package gg.moonflower.pollen.api.event.actors;

import net.minecraft.world.InteractionResult;

/**
 * A functional interface to handle events that return an {@link InteractionResult}.
 *
 * @param <T> The event to use as a parameter
 * @author ebo2022
 * @since 2.0.0
 */
@FunctionalInterface
public interface InteractionResultActor<T> {
    InteractionResult invoke(T event);
}
