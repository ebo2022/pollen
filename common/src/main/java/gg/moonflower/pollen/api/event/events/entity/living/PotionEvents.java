package gg.moonflower.pollen.api.event.events.entity.living;

import gg.moonflower.pollen.api.event.PollinatedEventResult;
import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;


public final class PotionEvents {

    public static final PollinatedEvent<Applicable> APPLICABLE = EventRegistry.createEventResult(Applicable.class);
    public static final PollinatedEvent<Add> ADD = EventRegistry.createLoop(Add.class);
    public static final PollinatedEvent<Remove> REMOVE = EventRegistry.createCancellable(Remove.class);
    public static final PollinatedEvent<Expire> EXPIRE = EventRegistry.createLoop(Expire.class);

    private PotionEvents() {
    }

    /**
     * Fired to check if an effect can be applied to an entity.
     *
     * @author ebo2022
     * @since 2.0.0
     */
    @FunctionalInterface
    public interface Applicable {

        /**
         * Called when checking if the specified effect can be applied to the given entity.
         *
         * @param entity         The entity that would receive the effect
         * @param effectInstance The effect to check if applicable
         * @return The result for this event. {@link PollinatedEventResult#PASS} will continue onto the next iteration, while any others will override vanilla behavior
         */
        PollinatedEventResult applicable(LivingEntity entity, MobEffectInstance effectInstance);
    }

    /**
     * Fired when a new effect is added to an entity.
     *
     * @author ebo2022
     * @since 2.0.0
     */
    @FunctionalInterface
    public interface Add {


        /**
         * Called when the specified effect is about to be applied to the given entity.
         *
         * @param entity            The entity receiving the effect
         * @param oldEffectInstance The old instance of the effect. This may be <code>null</code> if there was no effect of the added type beforehand
         * @param newEffectInstance The effect being added
         */
        void add(LivingEntity entity, MobEffectInstance oldEffectInstance, MobEffectInstance newEffectInstance);
    }

    /**
     * Fired when an effect is about to be removed from an entity.
     *
     * @author ebo2022
     * @since 2.0.0
     */
    @FunctionalInterface
    public interface Remove {

        /**
         * Called before the specified effect is about to be removed from the given entity.
         *
         * @param entity The entity that the effect is being removed from
         * @param effect The effect being removed
         * @return <code>true</code> to continue processing, or <code>false</code> to prevent the effect from being removed
         */
        boolean remove(LivingEntity entity, MobEffect effect);
    }

    /**
     * Fired when an effect expires on an entity.
     *
     * @author ebo2022
     * @since 2.0.0
     */
    @FunctionalInterface
    public interface Expire {

        /**
         * Called when the specified effect has expired on the given entity.
         *
         * @param entity         The entity that had the effect
         * @param effectInstance An instance of the effect that expired
         */
        void expire(LivingEntity entity, MobEffectInstance effectInstance);
    }
}
