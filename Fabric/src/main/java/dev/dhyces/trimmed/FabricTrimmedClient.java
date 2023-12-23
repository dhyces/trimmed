package dev.dhyces.trimmed;

import dev.dhyces.trimmed.impl.client.models.template.ModelTemplateManager;
import dev.dhyces.trimmed.impl.mixin.client.ModelLoaderAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class FabricTrimmedClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TrimmedClient.init();
        TrimmedClient.registerClientReloadListener((id, listener) -> {
            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new WrappedReloadListener(Trimmed.id(id), listener));
        });
        ModelLoadingPlugin.register(pluginContext -> {
            TrimmedClient.addModels(pluginContext::addModels);
            pluginContext.modifyModelOnLoad().register((model, context) -> {
                if (context.id() == ModelBakery.MISSING_MODEL_LOCATION) {
                    try {
                        ModelTemplateManager.generateTemplates((resourceLocation, modelSupplier) -> {
                            ((ModelLoaderAccessor)context.loader()).invokeLoadTopLevel(new ModelResourceLocation(resourceLocation, "inventory"));
                        });
                    } catch (RuntimeException e) {
                        Trimmed.LOGGER.error(e.getMessage());
                    }
                }
                return model;
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
