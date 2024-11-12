package dev.dhyces.trimmed.impl.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.dhyces.trimmed.TrimmedClient;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ModelManager.class)
public abstract class ModelManagerMixin {

    @ModifyReturnValue(method = "loadBlockModels", at = @At("RETURN"))
    private static CompletableFuture<Map<ResourceLocation, UnbakedModel>> injectGenerators(CompletableFuture<Map<ResourceLocation, UnbakedModel>> original, ResourceManager resourceManager, Executor executor) {
        return original.thenCombineAsync(TrimmedClient.startGeneratingModels(resourceManager, executor),
                (originalMap, generatedModels) -> {
                    Object2ObjectMap<ResourceLocation, UnbakedModel> newMap = new Object2ObjectOpenHashMap<>();
                    Set<ResourceLocation> generatedModelIds = new ObjectOpenHashSet<>();
                    generatedModels.forEach(namedModel -> {
                        if (!originalMap.containsKey(namedModel.id())) {
                            newMap.put(namedModel.id(), namedModel.model().get());
                            generatedModelIds.add(namedModel.id());
                        }
                    });
                    TrimmedClient.setModels(generatedModelIds);
                    newMap.putAll(originalMap);
                    return Object2ObjectMaps.unmodifiable(newMap);
        });
    }
}
