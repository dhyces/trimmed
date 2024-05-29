package dev.dhyces.trimmed;

import dev.dhyces.trimmed.api.client.TrimmedClientApiEntrypoint;
import dev.dhyces.trimmed.impl.client.models.source.ModelSourceLoader;
import dev.dhyces.trimmed.impl.client.models.source.NamedModel;
import dev.dhyces.trimmed.impl.client.models.template.ModelTemplateManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.block.model.BlockModel;
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
        TrimmedClient.registerClientReloadListener((id, listener) -> {
            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new WrappedReloadListener(Trimmed.id(id), listener));
        });
        PreparableModelLoadingPlugin.register(TrimmedClient::startGeneratingModels,
                (data, pluginContext) -> {
                    Map<ResourceLocation, BlockModel> modelMapByFileId = new Object2ObjectOpenHashMap<>();
                    for (NamedModel namedModel : data) {
                        // Need to add as ModelResourceLocations
                        pluginContext.addModels(namedModel.modelId());
                        // But the context ids are in regular ResourceLocations
                        modelMapByFileId.put(namedModel.id(), namedModel.model());
                    }
                    pluginContext.resolveModel().register(context -> modelMapByFileId.get(context.id()));
        });

        CommonLifecycleEvents.TAGS_LOADED.register(TrimmedClient::onTagsSynced);
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
