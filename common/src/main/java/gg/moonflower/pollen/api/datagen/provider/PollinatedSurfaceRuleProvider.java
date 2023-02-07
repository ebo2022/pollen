package gg.moonflower.pollen.api.datagen.provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import gg.moonflower.pollen.api.levelgen.surfacerule.PollinatedSurfaceRuleSource;
import gg.moonflower.pollen.api.util.PollinatedModContainer;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class PollinatedSurfaceRuleProvider implements DataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator generator;
    private final String domain;

    public PollinatedSurfaceRuleProvider(DataGenerator generator, PollinatedModContainer modContainer) {
        this.generator = generator;
        this.domain = modContainer.getId();
    }

    protected abstract void registerRuleSources(BiConsumer<String, List<PollinatedSurfaceRuleSource.RuleProvider>> registry);

    @Override
    public void run(HashCache cache) {
        Path folder = this.generator.getOutputFolder();
        Set<ResourceLocation> set = new HashSet<>();
        BiConsumer<String, List<PollinatedSurfaceRuleSource.RuleProvider>> registry = (s, ruleProviders) -> {
            ResourceLocation location;
            if (!set.add(location = new ResourceLocation(this.domain, s)))
                throw new IllegalStateException("Duplicate surface rule source " + location);

            Path path = folder.resolve("data/" + location.getNamespace() + "/surface_rule_sources/" + location.getPath() + ".json");

            try {
                PollinatedSurfaceRuleSource source = new PollinatedSurfaceRuleSource(ruleProviders);
                Optional<JsonElement> json = PollinatedSurfaceRuleSource.CODEC.encodeStart(JsonOps.INSTANCE, source).resultOrPartial(msg -> {
                    LOGGER.error("Couldn't serialize surface rule source {}: {}", path, msg);
                });
                if (json.isPresent()) {
                    DataProvider.save(GSON, cache, json.get(), path);
                }
            } catch (IOException e) {
                LOGGER.error("Couldn't save surface rule source {}", path, e);
            }
        };
        this.registerRuleSources(registry);
    }

    @Override
    public String getName() {
        return "Modded Surface Rule Sources";
    }
}
