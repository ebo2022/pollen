package gg.moonflower.pollen.api.levelgen.biome.parameters;

import net.minecraft.world.level.biome.Climate;

/**
 * Represents noise used for biome variants.
 *
 * @author ebo2022
 * @see <a href="https://github.com/Glitchfiend/TerraBlender/blob/TB-1.19.x-2.x.x/Common/src/main/java/terrablender/api/ParameterUtils.java">ParameterUtils</a>
 * @since 1.5.0
 */
public enum Weirdness {

    MID_SLICE_NORMAL_ASCENDING(Climate.Parameter.span(-1.0F, -0.93333334F)),
    HIGH_SLICE_NORMAL_ASCENDING(Climate.Parameter.span(-0.93333334F, -0.7666667F)),
    PEAK_NORMAL(Climate.Parameter.span(-0.7666667F, -0.56666666F)),
    HIGH_SLICE_NORMAL_DESCENDING(Climate.Parameter.span(-0.56666666F, -0.4F)),
    MID_SLICE_NORMAL_DESCENDING(Climate.Parameter.span(-0.4F, -0.26666668F)),
    LOW_SLICE_NORMAL_DESCENDING(Climate.Parameter.span(-0.26666668F, -0.05F)),
    VALLEY(Climate.Parameter.span(-0.05F, 0.05F)),
    LOW_SLICE_VARIANT_ASCENDING(Climate.Parameter.span(0.05F, 0.26666668F)),
    MID_SLICE_VARIANT_ASCENDING(Climate.Parameter.span(0.26666668F, 0.4F)),
    HIGH_SLICE_VARIANT_ASCENDING(Climate.Parameter.span(0.4F, 0.56666666F)),
    PEAK_VARIANT(Climate.Parameter.span(0.56666666F, 0.7666667F)),
    HIGH_SLICE_VARIANT_DESCENDING(Climate.Parameter.span(0.7666667F, 0.93333334F)),
    MID_SLICE_VARIANT_DESCENDING(Climate.Parameter.span(0.93333334F, 1.0F)),
    FULL_RANGE(Climate.Parameter.span(-1.0F, 1.0F));

    private final Climate.Parameter parameter;

    Weirdness(Climate.Parameter parameter) {
        this.parameter = parameter;
    }

    /**
     * @return The actual parameter the weirdness value represents
     */
    public Climate.Parameter parameter() {
        return this.parameter;
    }

    /**
     * Creates a {@link Climate.Parameter} with a range between the specified minimum and maximum values.
     *
     * @param min The minimum weirdness value
     * @param max The maximum weirdness value
     * @return A climate parameter spanning the given values
     */
    public static Climate.Parameter span(Weirdness min, Weirdness max) {
        return Climate.Parameter.span(Climate.unquantizeCoord(min.parameter().min()), Climate.unquantizeCoord(max.parameter().max()));
    }
}
