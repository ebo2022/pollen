package gg.moonflower.pollen.core.test;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.moonflower.pollen.core.PollenTest;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;

import java.util.Objects;

public class TestMoonflowerType {

    public static final Codec<TestMoonflowerType> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.STRING.fieldOf("name").forGetter(TestMoonflowerType::getName))
            .apply(instance, TestMoonflowerType::new));
    public static final Codec<Holder<TestMoonflowerType>> REGISTRY_CODEC = RegistryFileCodec.create(Objects.requireNonNull(PollenTest.KEY), CODEC);

    private final String name;

    public TestMoonflowerType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "Moonflower{name=" + this.name + "}";
    }
}
