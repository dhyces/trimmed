package dev.dhyces.trimmed.impl.client.models.source;

import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public record NamedModel(ResourceLocation id, Supplier<UnbakedModel> model) {
    public static NamedModel item(ResourceLocation fileId, Supplier<UnbakedModel> model) {
        return new NamedModel(fileId, model);
    }
}
