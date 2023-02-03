package gg.moonflower.pollen.api.util;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import java.util.List;
import java.util.function.Function;

public final class CodecHelper {

    private CodecHelper() {
    }

    /**
     * Tests to see if an object is valid based on its codec.
     *
     * @param codec The codec to test against
     * @param value An instance of the object to test
     * @param <T> The object type
     */
    public static <T> void validate(Codec<T> codec, T value) {
        DataResult<JsonElement> encoded = codec.encodeStart(JsonOps.INSTANCE, value);
        if (encoded.result().isEmpty()) {
            if (encoded.error().isPresent()) {
                throw new IllegalArgumentException("Value is invalid: " + encoded.error().get().message());
            } else {
                throw new IllegalArgumentException("Value is invalid: unknown error");
            }
        }
    }

    /**
     * Creates a codec that can accept a singular value or list of values.
     *
     * @param baseCodec The base value codec
     * @param <T> The codec type
     * @return A codec that accepts either a singular instance or a list of the base codec
     */
    public static <T> Codec<List<T>> singletonOrListCodec(Codec<T> baseCodec) {
        return Codec.either(baseCodec.listOf(), baseCodec).xmap(
                either -> either.map(Function.identity(), List::of),
                list -> list.size() == 1 ? Either.right(list.get(0)) : Either.left(list)
        );
    }
}
