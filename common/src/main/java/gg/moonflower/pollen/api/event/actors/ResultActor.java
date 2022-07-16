package gg.moonflower.pollen.api.event.actors;

import gg.moonflower.pollen.api.event.EventResult;

/**
 * A functional interface to handle events that return an {@link EventResult}.
 *
 * @param <T> The event to use as a parameter
 * @author ebo2022
 * @since 2.0.0
 */
@FunctionalInterface
public interface ResultActor<T> {
    EventResult invoke(T event);
}
