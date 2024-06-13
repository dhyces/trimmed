package dev.dhyces.trimmed.impl.client.models.source;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public record NamedModel(ResourceLocation id, Supplier<BlockModel> model) {
    public static NamedModel item(ResourceLocation fileId, Supplier<BlockModel> model) {
        return new NamedModel(fileId, model);
    }
}
