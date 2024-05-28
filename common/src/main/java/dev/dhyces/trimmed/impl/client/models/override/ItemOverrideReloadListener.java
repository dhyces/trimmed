package dev.dhyces.trimmed.impl.client.models.override;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.dhyces.trimmed.modhelper.services.Services;
import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class ItemOverrideReloadListener extends SimplePreparableReloadListener<Map<ResourceLocation, List<JsonObject>>> {
    private static final Logger LOGGER = LoggerFactory.getLogger("Trimmed/Item Model Overrides");

    public static final String OVERRIDES_DIRECTORY = "trimmed/model_overrides";
    private static final FileToIdConverter OVERRIDES_FINDER = FileToIdConverter.json(OVERRIDES_DIRECTORY);

    @Override
    protected Map<ResourceLocation, List<JsonObject>> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Object2ObjectMap<ResourceLocation, List<JsonObject>> files = new Object2ObjectOpenHashMap<>();
        for (Map.Entry<ResourceLocation, List<Resource>> entry : OVERRIDES_FINDER.listMatchingResourceStacks(resourceManager).entrySet()) {
            ResourceLocation id = OVERRIDES_FINDER.fileToId(entry.getKey());
            List<JsonObject> jsons = new ObjectArrayList<>();
            for (Resource resource : entry.getValue()) {
                try (BufferedReader reader = resource.openAsReader()) {
                    jsons.add(GsonHelper.parse(reader, true));
                } catch (JsonParseException | IOException e) {
                    LOGGER.error("Could not read %s: ".formatted(entry.getKey()), e);
                }
            }
            files.put(id, jsons);
        }
        return files;
    }

    @Override
    protected void apply(Map<ResourceLocation, List<JsonObject>> jsonFiles, ResourceManager resourceManager, ProfilerFiller profiler) {
        ItemOverrideRegistry.clearRegistry();
        for (Map.Entry<ResourceLocation, List<JsonObject>> entry : jsonFiles.entrySet()) {
            ObjectSet<ItemOverrideProvider> combined = new ObjectOpenHashSet<>();
            try {
                for (JsonObject json : entry.getValue()) {
                    Optional<Set<ItemOverrideProvider>> result = Services.PLATFORM_HELPER.decodeWithConditions(ItemOverrideProvider.SET_MAP_CODEC_CODEC, json);
                    if (result.isEmpty()) {
                        LOGGER.debug("Skipping loading item overrides from {} as its conditions were not met", entry.getKey());
                        continue;
                    }
                    combined.addAll(result.get());
                }
            } catch (JsonParseException e) {
                LOGGER.error("Could not read %s: ".formatted(entry.getKey()), e);
            }
            ItemOverrideRegistry.addOverrideSet(entry.getKey(), ObjectSets.unmodifiable(combined));
        }
    }
}
