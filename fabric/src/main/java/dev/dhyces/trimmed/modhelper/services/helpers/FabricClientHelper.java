package dev.dhyces.trimmed.modhelper.services.helpers;

import dev.dhyces.trimmed.api.client.TrimmedClientApiEntrypoint;
import dev.dhyces.trimmed.impl.ModApiConsumer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class FabricClientHelper implements ClientHelper {
    @Override
    public List<ModApiConsumer<TrimmedClientApiEntrypoint>> getClientApiConsumers() {
        return FabricLoader.getInstance().getEntrypointContainers("trimmed:client_api_entrypoint", TrimmedClientApiEntrypoint.class)
                .stream().map(container -> new ModApiConsumer<>(container.getProvider().getMetadata().getId(), container.getEntrypoint())).toList();
    }

    @Override
    public BakedModel getModel(ResourceLocation resourceId) {
        return Minecraft.getInstance().getModelManager().getModel(resourceId);
    }
}
