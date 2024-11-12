package dev.dhyces.trimmed.impl.client.services;

import dev.dhyces.trimmed.api.services.ModelHelper;
import dev.dhyces.trimmed.modhelper.services.Services;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

public class ModelHelperImpl implements ModelHelper {
    @Override
    public BakedModel getModel(ResourceLocation resourceId) {
        return Services.CLIENT_HELPER.getModel(resourceId);
    }
}
