package gg.moonflower.pollen.core.client.entitlement;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.core.client.entitlement.type.DeveloperHalo;
import gg.moonflower.pollen.core.client.entitlement.type.Halo;
import gg.moonflower.pollen.core.client.entitlement.type.ModelCosmetic;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public abstract class Entitlement {

    private ResourceLocation registryName;
    private Component displayName;

    /**
     * Updates internal settings for the entitlement.
     *
     * @param settings The new settings JSON
     */
    public abstract void updateSettings(JsonObject settings);

    /**
     * @return A JSON with all settings for this entitlement saved
     */
    public abstract JsonObject saveSettings();

    /**
     * @return The id of this entitlement, prefixed by <code>pollen</code>
     */
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    /**
     * @return The chat-visible name of the entitlement
     */
    public Component getDisplayName() {
        return displayName;
    }

    public final void setRegistryName(String registryName) {
        Validate.isTrue(this.registryName == null);
        this.registryName = new ResourceLocation(Pollen.MOD_ID, registryName);
    }

    public final void setDisplayName(Component displayName) {
        Validate.isTrue(this.displayName == null);
        this.displayName = displayName;
    }

    /**
     * @return The type of entitlement this is
     */
    public abstract Type getType();

    public enum Type {
        COSMETIC(ModelCosmetic.CODEC),
        HALO(Halo.CODEC),
        DEVELOPER_HALO(DeveloperHalo.CODEC);

        private final Codec<? extends Entitlement> codec;

        Type(Codec<? extends Entitlement> codec) {
            this.codec = codec;
        }

        public Codec<? extends Entitlement> codec() {
            return codec;
        }

        @Nullable
        public static Type byName(String name) {
            for (Type type : values())
                if (type.name().toLowerCase(Locale.ROOT).equals(name))
                    return type;
            return null;
        }
    }
}
