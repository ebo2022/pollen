package gg.moonflower.pollen.api.resource.condition;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.config.PollinatedModConfig;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.core.resource.condition.ConfigResourceCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

/**
 * A common condition for checking if a resource should be loaded.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface PollinatedResourceCondition {

    /**
     * Tests to see if the provided JSON should allow the resource to load.
     *
     * @param json The JSON to check
     * @return Whether the resource passes this condition
     * @throws JsonParseException If any errors occurs when reading from the JSON
     */
    boolean test(JsonObject json) throws JsonParseException;

    /**
     * An <code>and</code> operator condition for use with data generators.
     *
     * @param values The conditions to check
     * @return A condition checking if all the specified conditions are true
     */
    @ExpectPlatform
    static PollinatedResourceConditionProvider and(PollinatedResourceConditionProvider... values) {
        return Platform.error();
    }

    /**
     * A static <code>false</code> condition for use with data generators.
     *
     * @return A condition always returning false
     */
    @ExpectPlatform
    static PollinatedResourceConditionProvider FALSE() {
        return Platform.error();
    }

    /**
     * A static <code>true</code> condition for use with data generators.
     *
     * @return A condition always returning true
     */
    @ExpectPlatform
    static PollinatedResourceConditionProvider TRUE() {
        return Platform.error();
    }

    /**
     * Inverts the specified condition for use with data generators.
     *
     * @param value The conditions to invert
     * @return A condition checking if all the specified condition is false
     */
    @ExpectPlatform
    static PollinatedResourceConditionProvider not(PollinatedResourceConditionProvider value) {
        return Platform.error();
    }

    /**
     * An <code>or</code> operator condition for use with data generators.
     *
     * @param values The conditions to check
     * @return A condition checking if any of the specified conditions are true
     */
    @ExpectPlatform
    static PollinatedResourceConditionProvider or(PollinatedResourceConditionProvider... values) {
        return Platform.error();
    }

    /**
     * Checks to see if a block is registered with the specified name.
     *
     * @param name The name of the block
     * @return A condition checking if the block is registered
     */
    @ExpectPlatform
    static PollinatedResourceConditionProvider blockExists(ResourceLocation name) {
        return Platform.error();
    }

    /**
     * Checks to see if an item is registered with the specified name.
     *
     * @param name The name of the item
     * @return A condition checking if the item is registered
     */
    @ExpectPlatform
    static PollinatedResourceConditionProvider itemExists(ResourceLocation name) {
        return Platform.error();
    }

    /**
     * Checks to see if a fluid is registered with the specified name.
     *
     * @param name The name of the fluid
     * @return A condition checking if the fluid is registered
     */
    @ExpectPlatform
    static PollinatedResourceConditionProvider fluidExists(ResourceLocation name) {
        return Platform.error();
    }

    /**
     * Checks to see if the item tag exists and has at least one registered item in it.
     *
     * @param tag The item tag to check
     * @return A condition checking if the item is registered
     */
    @ExpectPlatform
    static PollinatedResourceConditionProvider itemTagPopulated(Tag.Named<Item> tag) {
        return Platform.error();
    }

    /**
     * Checks to see if the block tag exists and has at least one registered block in it.
     *
     * @param tag The block tag to check
     * @return A condition checking if the block is registered
     */
    @ExpectPlatform
    static PollinatedResourceConditionProvider blockTagPopulated(Tag.Named<Block> tag) {
        return Platform.error();
    }

    /**
     * Checks to see if the fluid tag exists and has at least one registered fluid in it.
     *
     * @param tag The fluid tag to check
     * @return A condition checking if the fluid is registered
     */
    @ExpectPlatform
    static PollinatedResourceConditionProvider fluidTagPopulated(Tag.Named<Fluid> tag) {
        return Platform.error();
    }

    /**
     * Checks to see if all the specified mods are loaded.
     *
     * @param modIds The IDs of the mods to check
     * @return A condition checking if all mod are loaded
     */
    @ExpectPlatform
    static PollinatedResourceConditionProvider allModsLoaded(String... modIds) {
        return Platform.error();
    }

    /**
     * Checks to see if any the specified mods are loaded.
     *
     * @param modIds The IDs of the mods to check
     * @return A condition checking if any mod is loaded
     */
    @ExpectPlatform
    static PollinatedResourceConditionProvider anyModsLoaded(String... modIds) {
        return Platform.error();
    }

    /**
     * Checks to see if any the specified mods are loaded.
     *
     * @param modIds The IDs of the mods to check
     * @return A condition checking if any mod is loaded
     */
    static <T> PollinatedResourceConditionProvider config(T config, String... modIds) {
        return new ConfigResourceCondition.Provider();
    }
}
