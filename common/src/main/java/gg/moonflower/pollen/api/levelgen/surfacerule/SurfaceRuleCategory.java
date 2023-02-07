package gg.moonflower.pollen.api.levelgen.surfacerule;

import com.mojang.serialization.Codec;
import gg.moonflower.pollen.api.util.CodecHelper;
import net.minecraft.util.StringRepresentable;

public enum SurfaceRuleCategory implements StringRepresentable {

        OVERWORLD("overworld"),
        NETHER("nether");

        private final String name;
        public static final Codec<SurfaceRuleCategory> CODEC = CodecHelper.enumCodec(SurfaceRuleCategory::values);

        SurfaceRuleCategory(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }