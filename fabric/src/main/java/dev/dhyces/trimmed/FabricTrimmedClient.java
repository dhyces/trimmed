package dev.dhyces.trimmed;

import dev.dhyces.trimmed.impl.ModelBakeryHelper;
import dev.dhyces.trimmed.impl.client.models.source.NamedModel;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.AtlasSourceTypeRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@SuppressWarnings("unused")
public class FabricTrimmedClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TrimmedClient.init();
        TrimmedClient.registerSpriteSourceTypes((id, codec) -> {
            SpriteSourceType type = new SpriteSourceType(codec);
            AtlasSourceTypeRegistry.register(id, type);
            return type;
        });
        TrimmedClient.registerClientReloadListener((id, listener) -> {
            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new WrappedReloadListener(Trimmed.id(id), listener));
        });
        PreparableModelLoadingPlugin.register(TrimmedClient::startGeneratingModels,
                (data, pluginContext) -> {
                    Map<ResourceLocation, NamedModel> modelMapByFileId = new Object2ObjectOpenHashMap<>();
                    for (NamedModel namedModel : data) {
                        pluginContext.addModels(namedModel.id());
                        modelMapByFileId.put(namedModel.id(), namedModel);
                    }
                    pluginContext.resolveModel().register(context -> {
                        NamedModel namedModel = modelMapByFileId.get(context.id());
                        if (namedModel != null && !((ModelBakeryHelper)context.loader()).trimmed$hasResourceFor(context.id().withPath(s -> "models/" + s + ".json"))) {
                            return modelMapByFileId.get(context.id()).model().get();
                        }
                        return null;
                    });
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
        public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
            return reloadListener.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
        }
    }
}
