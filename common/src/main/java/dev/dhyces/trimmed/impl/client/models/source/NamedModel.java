package dev.dhyces.trimmed.impl.client.models.source;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public record NamedModel(ResourceLocation id, BlockModel model, @Nullable ModelResourceLocation modelId) {
    public static NamedModel item(ResourceLocation fileId, BlockModel model) {
        return new NamedModel(fileId, model, convertToItemModelId(fileId));
    }

    public static ModelResourceLocation convertToItemModelId(ResourceLocation modelFileId) {
        return new ModelResourceLocation(modelFileId.withPath(s -> s.substring(s.indexOf("/")+1)), "inventory");
    }
}
