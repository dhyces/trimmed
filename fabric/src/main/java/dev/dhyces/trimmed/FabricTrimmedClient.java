package dev.dhyces.trimmed;

import dev.dhyces.trimmed.impl.client.models.source.ModelSourceLoader;
import dev.dhyces.trimmed.impl.client.models.source.NamedModel;
import dev.dhyces.trimmed.impl.client.models.template.ModelTemplateManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class FabricTrimmedClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TrimmedClient.init();
        TrimmedClient.registerClientReloadListener((id, listener) -> {
            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new WrappedReloadListener(Trimmed.id(id), listener));
        });
        PreparableModelLoadingPlugin.register((resourceManager, executor) ->
                ModelTemplateManager.load(resourceManager, executor)
                        .thenCompose(templateManager ->
                            ModelSourceLoader.load(templateManager, resourceManager, executor)
                        ),
                (data, pluginContext) -> {
                    Map<ResourceLocation, BlockModel> modelMapByFileId = new Object2ObjectOpenHashMap<>();
                    for (NamedModel namedModel : data) {
                        pluginContext.addModels(namedModel.modelId());
                        modelMapByFileId.put(namedModel.id(), namedModel.model());
                    }
                    // Need to add as ModelResourceLocations
                    pluginContext.resolveModel().register(context -> {
                        // But the context ids are in regular ResourceLocations
                        if (modelMapByFileId.containsKey(context.id())) {
                            return modelMapByFileId.get(context.id());
                        }
                        return null;
                    });
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
