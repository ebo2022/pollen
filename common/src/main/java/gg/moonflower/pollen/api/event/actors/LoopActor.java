package gg.moonflower.pollen.api.event.actors;

/**
 * A functional interface to handle events with a void return type.
 *
 * @param <T> The event to use as a parameter
 * @author ebo2022
 * @since 2.0.0
 */
@FunctionalInterface
public interface LoopActor<T> {
    void invoke(T event);
}
