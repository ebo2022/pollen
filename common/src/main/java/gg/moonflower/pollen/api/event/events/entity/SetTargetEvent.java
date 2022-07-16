package gg.moonflower.pollen.api.event.events.entity;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

/**
 * Fired each time a mob changes their attack target.
 *
 * @author abigailfails
 * @since 1.0.0
 */
public class SetTargetEvent {

    public static final PollinatedEvent<Consumer<SetTargetEvent>> EVENT = EventRegistry.createLoopActor(SetTargetEvent.class);

    private final LivingEntity attacker;
    private final LivingEntity target;

    public SetTargetEvent(LivingEntity attacker, LivingEntity target) {
        this.attacker = attacker;
        this.target = target;
    }

    /**
     * @return The attacking entity
     */
    public LivingEntity getAttacker() {
        return this.attacker;
    }

    /**
     * @return The target entity
     */
    public LivingEntity getTarget() {
        return this.target;
    }
}
