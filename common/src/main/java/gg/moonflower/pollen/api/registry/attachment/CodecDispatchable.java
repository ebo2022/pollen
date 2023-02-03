package gg.moonflower.pollen.api.registry.attachment;

import net.minecraft.resources.ResourceLocation;

/**
 * Used with {@link RegistryAttachment}s to allow for polymorphic attachment types.
 *
 * @author ebo2022
 * @since
 */
public interface CodecDispatchable {

    /**
     * @return The type location for this instance of the attachment object
     */
    ResourceLocation type();
}
