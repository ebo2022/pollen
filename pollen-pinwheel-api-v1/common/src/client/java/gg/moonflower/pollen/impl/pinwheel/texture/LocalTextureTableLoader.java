package gg.moonflower.pollen.impl.pinwheel.texture;

import com.google.gson.Gson;
import com.mojang.logging.LogUtils;
import gg.moonflower.pollen.api.pinwheel.v1.geometry.GeometryModelParser;
import gg.moonflower.pollen.api.pinwheel.v1.texture.GeometryModelTexture;
import gg.moonflower.pollen.api.pinwheel.v1.texture.GeometryModelTextureTable;
import gg.moonflower.pollen.api.pinwheel.v1.texture.TextureTableLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class LocalTextureTableLoader implements TextureTableLoader {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    private final Map<ResourceLocation, GeometryModelTextureTable> textures;
    private final String folder;
    private String[] hashTables;

    public LocalTextureTableLoader() {
        this("textures/geometry");
    }

    public LocalTextureTableLoader(@Nullable String folder) {
        this.textures = new HashMap<>();
        this.folder = folder == null || folder.isEmpty() ? "" : folder + "/";
        this.hashTables = new String[0];
    }

    @Override
    public void addTextures(BiConsumer<ResourceLocation, GeometryModelTextureTable> textureConsumer) {
        this.textures.forEach(textureConsumer);
    }

    @Override
    public void addHashTables(Consumer<String> hashTableConsumer) {
        for (String hashTable : this.hashTables)
            hashTableConsumer.accept(hashTable);
    }

    @Override
    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() ->
        {
            Map<ResourceLocation, GeometryModelTextureTable> textureLocations = new HashMap<>();
            for (ResourceLocation textureTableLocation : resourceManager.listResources(this.folder, name -> name.endsWith(".json"))) {
                ResourceLocation textureTableName = new ResourceLocation(textureTableLocation.getNamespace(), textureTableLocation.getPath().substring(this.folder.length(), textureTableLocation.getPath().length() - 5));
                if (textureTableName.getPath().equals("hash_tables"))
                    continue;

                try (Resource resource = resourceManager.getResource(textureTableLocation)) {
                    GeometryModelTextureTable table = GeometryModelParser.parseTextures(new InputStreamReader(resource.getInputStream()));
                    // Validate there are no online textures
                    table.getTextureDefinitions().forEach((name, textures) -> {
                        for (GeometryModelTexture texture : textures)
                            if (texture.getType() == GeometryModelTexture.Type.ONLINE)
                                throw new IllegalArgumentException(name + " uses unsupported texture type: " + texture.getType().name().toLowerCase());
                    });
                    textureLocations.put(textureTableName, table);
                } catch (Exception e) {
                    LOGGER.error("Failed to load texture table '" + textureTableName + "'", e);
                }
            }
            LOGGER.info("Loaded " + textureLocations.size() + " model texture tables.");
            return textureLocations;
        }, backgroundExecutor).thenAcceptBothAsync(CompletableFuture.supplyAsync(() ->
        {
            Set<String> hashTables = new HashSet<>();
            for (String domain : resourceManager.getNamespaces()) {
                ResourceLocation hashTableLocation = new ResourceLocation(domain, this.folder + "hash_tables.json");
                if (!resourceManager.hasResource(hashTableLocation))
                    continue;

                try (Resource resource = resourceManager.getResource(hashTableLocation)) {
                    hashTables.addAll(Arrays.asList(GSON.fromJson(new InputStreamReader(resource.getInputStream()), String[].class)));
                } catch (Exception e) {
                    LOGGER.error("Failed to load texture hash table for " + domain, e);
                }
            }
            LOGGER.info("Loaded " + hashTables.size() + " hash tables.");
            return hashTables.toArray(new String[0]);
        }, backgroundExecutor), (textureLocations, hashTables) ->
        {
            this.textures.clear();
            this.textures.putAll(textureLocations);
            this.hashTables = hashTables;
        }, gameExecutor).thenCompose(stage::wait);
    }
}