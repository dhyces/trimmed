package dev.dhyces.trimmed.impl.client.models.template;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.dhyces.trimmed.impl.client.models.source.ModelSource;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class ModelTemplateManager {
    private static Map<ResourceLocation, IoSupplier<BufferedReader>> rawTemplates;
    private static Multimap<ResourceLocation, Template> templates;

    private static final FileToIdConverter TEMPLATE_CONVERTER = new FileToIdConverter("trimmed/model_templates", ".json");

    private ModelTemplateManager() {}

    public static void init() {
    }

    public static void addTemplateResource(ResourceLocation templatePath, IoSupplier<BufferedReader> resource) {
        if (rawTemplates == null) {
            rawTemplates = new HashMap<>();
        }
        rawTemplates.put(templatePath, resource);
    }

    public static void addTemplate(ResourceLocation templateId, Template template) {
        if (templates == null) {
            templates = HashMultimap.create();
        }
        templates.put(templateId, template);
    }

    public static void generateTemplates(BiConsumer<ResourceLocation, Supplier<BlockModel>> modelConsumer) {
        if (templates == null) {
            return;
        }
        for (Map.Entry<ResourceLocation, Template> entry : templates.entries()) {
            IoSupplier<BufferedReader> readerSupplier = rawTemplates.get(TEMPLATE_CONVERTER.idToFile(entry.getKey()));
            if (readerSupplier == null) {
                throw new IllegalStateException("No template file found for " + entry.getKey());
            }
            try (BufferedReader reader = readerSupplier.get()) {
                entry.getValue().generate(reader, modelConsumer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        rawTemplates.clear();
    }

    public static CompletableFuture<Map<ResourceLocation, ModelSource>> generatorPreparer(ResourceManager resourceManager, Executor backgroundExecutor) {
        return CompletableFuture.supplyAsync(() -> {
            ImmutableMap.Builder<ResourceLocation, Template> templates = ImmutableMap.builder();
            for (Map.Entry<ResourceLocation, Resource> entry : TEMPLATE_CONVERTER.listMatchingResources(resourceManager).entrySet()) {
                ResourceLocation id = TEMPLATE_CONVERTER.fileToId(entry.getKey());
                try (BufferedReader reader = entry.getValue().openAsReader()) {
                    JsonObject jsonObject = GsonHelper.parse(reader);
                    templates.put(id, new GroovyTemplate(jsonObject.getAsString()));
                } catch (JsonParseException | IOException e) {
                    throw new RuntimeException("Failed to read %s from %s: ".formatted(entry.getKey(), entry.getValue().source().packId()), e);
                }
            }
            return templates.build();
        }, backgroundExecutor);
    }

    public static CompletableFuture<Map<ResourceLocation, Template>> templatePreparer(ResourceManager resourceManager, Executor backgroundExecutor) {
        return CompletableFuture.supplyAsync(() -> {
            ImmutableMap.Builder<ResourceLocation, Template> templates = ImmutableMap.builder();
            for (Map.Entry<ResourceLocation, Resource> entry : TEMPLATE_CONVERTER.listMatchingResources(resourceManager).entrySet()) {
                ResourceLocation id = TEMPLATE_CONVERTER.fileToId(entry.getKey());
                try (BufferedReader reader = entry.getValue().openAsReader()) {
                    JsonObject jsonObject = GsonHelper.parse(reader);
                    templates.put(id, new GroovyTemplate(jsonObject.getAsString()));
                } catch (JsonParseException | IOException e) {
                    throw new RuntimeException("Failed to read %s from %s: ".formatted(entry.getKey(), entry.getValue().source().packId()), e);
                }
            }
            return templates.build();
        }, backgroundExecutor);
    }
}
