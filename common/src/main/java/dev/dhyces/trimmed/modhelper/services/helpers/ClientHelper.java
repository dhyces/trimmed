package dev.dhyces.trimmed.modhelper.services.helpers;

import dev.dhyces.trimmed.api.client.TrimmedClientApiEntrypoint;
import dev.dhyces.trimmed.impl.ModApiConsumer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface ClientHelper {
    List<ModApiConsumer<TrimmedClientApiEntrypoint>> getClientApiConsumers();
    BakedModel getModel(ResourceLocation resourceId);
}
