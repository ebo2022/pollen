package gg.moonflower.pollen.api.event.actors;

import gg.moonflower.pollen.api.event.CompoundEventResult;

/**
 * A functional interface to handle events that return an {@link CompoundEventResult}.
 *
 * @param <T> The event to use as a parameter
 * @author ebo2022
 * @since 2.0.0
 */
@FunctionalInterface
public interface CompoundResultActor<T> {
    CompoundEventResult<?> invoke(T event);
}
