package dev.dhyces.trimmed.api.client.models.source;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public interface ModelTemplateManager {
    String process(ResourceLocation id, Function<String, String> replacer);
}
