package gg.moonflower.pollen.api.biome.modifier;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.moonflower.pollen.api.biome.modifier.modification.BiomeModification;
import gg.moonflower.pollen.api.biome.modifier.selector.BiomeSelector;
import gg.moonflower.pollen.api.registry.resource.PollinatedPreparableReloadListener;
import gg.moonflower.pollen.api.registry.resource.ResourceRegistry;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public final class BiomeModifierManager implements PollinatedPreparableReloadListener {

    private static final Map<ResourceLocation, Entry> MODIFIERS = new HashMap<>();
    private static final BiomeModifierManager INSTANCE = new BiomeModifierManager();
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ResourceLoader resourceLoader = new ResourceLoader();

    private BiomeModifierManager() {
    }

    /**
     * Retrieves a biome modifier by the specified id.
     *
     * @param location The id of the biome modifier to retrieve
     * @return An optional of the biome modifier
     */
    public static Optional<Entry> getModifier(ResourceLocation location) {
        return Optional.ofNullable(MODIFIERS.get(location));
    }

    /**
     * Retrieves the id of the specified biome modifier.
     *
     * @param modifier The biome modifier to get the id for
     * @return An optional of the biome modifier id
     */
    public static Optional<ResourceLocation> getModifierId(Entry modifier) {
        return MODIFIERS.entrySet().stream().filter(entry -> entry.getValue().equals(modifier)).map(Map.Entry::getKey).findFirst();
    }

    /**
     * @return All currently registered biome modifier ids
     */
    public static Stream<ResourceLocation> getAllModifierIds() {
        return MODIFIERS.keySet().stream();
    }

    /**
     * @return All currently registered biome modifiers
     */
    public static Stream<Entry> getAllModifiers() {
        return MODIFIERS.values().stream();
    }

    /**
     * @return A set of all associated locations and corresponding modifiers
     */
    public static Set<Map.Entry<ResourceLocation, Entry>> entrySet() {
        return MODIFIERS.entrySet();
    }


    @ApiStatus.Internal
    public static void init() {
        ResourceRegistry.registerReloadListener(PackType.SERVER_DATA, INSTANCE);
    }

    @Override
    public ResourceLocation getPollenId() {
        return new ResourceLocation(Pollen.MOD_ID, "biome_modifier_manager");
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller
            reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return this.resourceLoader.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor).thenRun(() -> {
            LOGGER.info("Loaded " + MODIFIERS.size() + " biome modifiers");
        });
    }

    public record Entry(BiomeSelectionMode biomeSelectionMode, List<BiomeSelector> selectors, List<BiomeModification> modifications) {

        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                BiomeSelectionMode.CODEC.optionalFieldOf("selection_mode", BiomeSelectionMode.ALL_TRUE).forGetter(Entry::biomeSelectionMode),
                BiomeSelector.CODEC.listOf().fieldOf("selectors").forGetter(Entry::selectors),
                BiomeModification.CODEC.listOf().fieldOf("modifiers").forGetter(Entry::modifications)
        ).apply(builder, Entry::new));
    }

    @ApiStatus.Internal
    private static class ResourceLoader extends SimpleJsonResourceReloadListener {

        private ResourceLoader() {
            super(new Gson(), "biome_modifiers");
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> elements, ResourceManager resourceManager, ProfilerFiller profiler) {
            MODIFIERS.clear();
            for (Map.Entry<ResourceLocation, JsonElement> entry : elements.entrySet()) {
                try {
                    if (MODIFIERS.containsKey(entry.getKey()))
                        throw new IllegalStateException("Duplicate biome modifier: " + entry.getKey());

                    DataResult<Entry> result = Entry.CODEC.parse(JsonOps.INSTANCE, entry.getValue());
                    if (result.error().isPresent() || result.result().isEmpty())
                        throw new IOException(result.error().get().message() + " " + entry.getValue());

                    MODIFIERS.put(entry.getKey(), result.result().get());
                } catch (IOException e) {
                    LOGGER.error("Failed to load biome modifier: " + entry.getKey(), e);
                }
            }
        }
    }
}
