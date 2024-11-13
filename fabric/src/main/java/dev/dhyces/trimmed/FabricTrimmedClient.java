package dev.dhyces.trimmed;

import dev.dhyces.trimmed.api.TrimmedReference;
import dev.dhyces.trimmed.api.client.models.source.NamedModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.AtlasSourceTypeRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@SuppressWarnings("unused")
public class FabricTrimmedClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TrimmedClient.init();
        TrimmedClient.registerSpriteSourceTypes(AtlasSourceTypeRegistry::register);
        TrimmedClient.registerClientReloadListener((id, listener) -> {
            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new WrappedReloadListener(TrimmedReference.id(id), listener));
        });
        PreparableModelLoadingPlugin.register(TrimmedClient::startGeneratingModels,
                (data, pluginContext) -> {
                    for (NamedModel namedModel : data) {
                        pluginContext.addModels(namedModel.id());
                    }
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> TrimmedClient.resetSyncedStatus());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> TrimmedClient.resetSyncedStatus());

        CommonLifecycleEvents.TAGS_LOADED.register(TrimmedClient::onTagsSynced);

        TrimmedClient.initApi();
    }

    private record WrappedReloadListener(ResourceLocation id, PreparableReloadListener reloadListener) implements IdentifiableResourceReloadListener {

        @Override
        public ResourceLocation getFabricId() {
            return id;
        }

        @Override
        public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, Executor backgroundExecutor, Executor gameExecutor) {
            return reloadListener.reload(preparationBarrier, resourceManager, backgroundExecutor, gameExecutor);
        }
    }
}
