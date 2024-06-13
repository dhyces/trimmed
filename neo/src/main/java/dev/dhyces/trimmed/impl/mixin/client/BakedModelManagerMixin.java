package dev.dhyces.trimmed.impl.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.dhyces.trimmed.NeoTrimmedClient;
import dev.dhyces.trimmed.TrimmedClient;
import dev.dhyces.trimmed.impl.client.models.source.ModelSourceLoader;
import dev.dhyces.trimmed.impl.client.models.source.NamedModel;
import dev.dhyces.trimmed.impl.client.models.template.ModelTemplateManager;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ModelManager.class)
public abstract class BakedModelManagerMixin {

    @ModifyReturnValue(method = "loadBlockModels", at = @At("RETURN"))
    private static CompletableFuture<Map<ResourceLocation, BlockModel>> injectGenerators(CompletableFuture<Map<ResourceLocation, BlockModel>> original, ResourceManager resourceManager, Executor executor) {
        return original.thenCombineAsync(TrimmedClient.startGeneratingModels(resourceManager, executor),
                (originalMap, generatedModels) -> {
                    Object2ObjectMap<ResourceLocation, BlockModel> newMap = new Object2ObjectOpenHashMap<>();
                    Set<ModelResourceLocation> generatedModelIds = new ObjectOpenHashSet<>();
                    generatedModels.forEach(namedModel -> {
                        ResourceLocation path = namedModel.id().withPrefix("models/").withSuffix(".json");
                        if (!originalMap.containsKey(path)) {
                            newMap.put(path, namedModel.model().get());
                            generatedModelIds.add(namedModel.modelId());
                        }
                    });
                    NeoTrimmedClient.setModels(generatedModelIds);
                    newMap.putAll(originalMap);
                    return Object2ObjectMaps.unmodifiable(newMap);
        });
    }
}
