package gg.moonflower.pollen.api.levelgen.surfacerule;

import com.mojang.serialization.Codec;
import gg.moonflower.pollen.api.util.CodecHelper;
import net.minecraft.util.StringRepresentable;

public enum SurfaceRuleStage implements StringRepresentable {

        BEFORE_BEDROCK("before_bedrock"),
        AFTER_BEDROCK("after_bedrock");

        private final String name;
        public static final Codec<SurfaceRuleStage> CODEC = CodecHelper.enumCodec(SurfaceRuleStage::values);

        SurfaceRuleStage(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }