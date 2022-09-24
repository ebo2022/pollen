package gg.moonflower.pollen.api.event.events.registry;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import gg.moonflower.pollen.api.registry.RegistryDataAttachment;

@FunctionalInterface
public interface RegisterDataAttachmentsEvent {

    PollinatedEvent<RegisterDataAttachmentsEvent> EVENT = EventRegistry.createLoop(RegisterDataAttachmentsEvent.class);

    void registerAttachments(Registry registry);

    /**
     * Used for registration of {@link RegistryDataAttachment}s.
     *
     * @since 2.0.0
     */
    interface Registry {

        /**
         * Registers a {@link RegistryDataAttachment} to be added to the registry access.
         *
         * @param attachment The attachment to register
         */
        <T> void register(RegistryDataAttachment<T> attachment);
    }
}
