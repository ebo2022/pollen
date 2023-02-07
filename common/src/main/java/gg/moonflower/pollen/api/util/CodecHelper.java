package gg.moonflower.pollen.api.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class CodecHelper {

    private CodecHelper() {
    }

    public static <T extends Enum<T> & StringRepresentable> Codec<T> enumCodec(Supplier<T[]> values) {
        Map<String, T> mimicMap = Arrays.stream(values.get()).collect(Collectors.toMap(T::getSerializedName, Function.identity()));
        return StringRepresentable.fromEnum(values, mimicMap::get);
    }
}
