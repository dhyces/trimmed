package dev.dhyces.trimmed.impl.client.models.template;

import com.google.gson.JsonObject;
import dev.dhyces.trimmed.Trimmed;
import dev.dhyces.trimmed.api.TrimmedReference;
import dev.dhyces.trimmed.api.client.models.source.ModelTemplateManager;
import dev.dhyces.trimmed.api.client.models.template.StringTemplate;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ModelTemplateManagerImpl implements ModelTemplateManager {
    private static final FileToIdConverter TEMPLATE_CONVERTER = new FileToIdConverter(TrimmedReference.MODEL_TEMPLATES_DIRECTORY, ".json");

    private final Map<ResourceLocation, StringTemplate> templates;

    private ModelTemplateManagerImpl() {
        this.templates = new Object2ObjectOpenHashMap<>();
    }

    private StringTemplate getTemplate(ResourceLocation id) {
        StringTemplate template = templates.get(id);
        if (template == null) {
            throw new IllegalArgumentException("Template %s does not exist");
        }
        return template;
    }

    public String process(ResourceLocation id, Function<String, String> replacer) {
        return getTemplate(id).process(replacer);
    }

    public JsonObject processAsJson(ResourceLocation id, Function<String, String> replacer) {
        return GsonHelper.parse(process(id, replacer), true);
    }

    public static CompletableFuture<ModelTemplateManagerImpl> load(ResourceManager resourceManager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            ModelTemplateManagerImpl manager = new ModelTemplateManagerImpl();
            for (Map.Entry<ResourceLocation, Resource> entry : TEMPLATE_CONVERTER.listMatchingResources(resourceManager).entrySet()) {
                try (BufferedReader reader = entry.getValue().openAsReader()) {
                    ResourceLocation id = TEMPLATE_CONVERTER.fileToId(entry.getKey());
                    String rawData = reader.lines().collect(Collectors.joining());
                    manager.templates.put(id, StringTemplate.of(rawData));
                } catch (IOException e) {
                    Trimmed.LOGGER.error("Failed to read %s from %s: ".formatted(entry.getKey(), entry.getValue().source().packId()), e);
                }
            }
            return manager;
        }, executor);
    }
}
