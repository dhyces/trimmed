package dev.dhyces.trimmed.impl.client.models.source;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;

public record NamedModel(ResourceLocation id, BlockModel model) {
    public static NamedModel of(ResourceLocation id, BlockModel model) {
        return new NamedModel(id, model);
    }
}
