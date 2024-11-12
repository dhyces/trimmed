package dev.dhyces.trimmed.api.services;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

public interface ModelHelper {
    BakedModel getModel(ResourceLocation resourceId);
}
