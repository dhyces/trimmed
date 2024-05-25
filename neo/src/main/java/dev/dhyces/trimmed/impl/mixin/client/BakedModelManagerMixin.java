package dev.dhyces.trimmed.impl.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ModelManager.class)
public abstract class BakedModelManagerMixin {

    @ModifyReturnValue(method = "loadBlockModels", at = @At("RETURN"))
    private static CompletableFuture<Map<ResourceLocation, BlockModel>> injectGenerators(CompletableFuture<Map<ResourceLocation, BlockModel>> original, ResourceManager resourceManager, Executor executor) {
//        CompletableFuture<Map<ResourceLocation, BlockModel>> generatedModels =
        return original;
    }
}
