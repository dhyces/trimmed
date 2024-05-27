package dev.dhyces.trimmed.impl.client.models.source;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import dev.dhyces.trimmed.Trimmed;
import dev.dhyces.trimmed.impl.client.maps.manager.ClientMapManager;
import dev.dhyces.trimmed.impl.client.models.template.ModelTemplateManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class ModelSourceLoader {
    private static final FileToIdConverter MODEL_SOURCE_CONVERTER = new FileToIdConverter("trimmed/model_generators", ".json");
    private ModelSourceLoader() {}

    public static CompletableFuture<Map<ResourceLocation, BlockModel>> load(ModelTemplateManager templateManager, ResourceManager resourceManager, Executor executor) {
        return ClientMapManager.future().thenApplyAsync(unit -> {
            Map<ResourceLocation, BlockModel> models = new Object2ObjectOpenHashMap<>();
            for (Map.Entry<ResourceLocation, Resource> entry : MODEL_SOURCE_CONVERTER.listMatchingResources(resourceManager).entrySet()) {
                try (BufferedReader reader = entry.getValue().openAsReader()) {
                    JsonObject json = GsonHelper.parse(reader, true);
                    ModelSource modelSource = ModelSourceRegistry.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
                    modelSource.generate(resourceManager, templateManager).forEach(namedModel -> {
                        models.put(namedModel.id(), namedModel.model());
                    });
                } catch (JsonParseException | IllegalStateException | IOException e) {
                    Trimmed.LOGGER.error("Failed to read %s from %s: ".formatted(entry.getKey(), entry.getValue().source().packId()), e);
                }
            }
            return models;
        }, executor);
    }
}
