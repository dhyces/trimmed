package dev.dhyces.trimmed.impl.mixin.client;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.datafixers.util.Pair;
import dev.dhyces.trimmed.impl.client.models.template.ModelTemplateManager;
import dev.dhyces.trimmed.impl.client.models.template.Template;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static dev.dhyces.trimmed.impl.util.BakedModelManagerMixinUtil.*;

@Mixin(ModelManager.class)
public abstract class BakedModelManagerMixin {

    @WrapOperation(method = "reload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelManager;loadBlockModels(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
    private CompletableFuture<Map<ResourceLocation, BlockModel>> trimmed$scheduleTemplateParsing(
            ResourceManager resourceManager, Executor executor, Operation<CompletableFuture<Map<ResourceLocation, BlockModel>>> original) {
        CompletableFuture<Map<ResourceLocation, Template>> templateFuture = ModelTemplateManager.templatePreparer(resourceManager, executor);
        return original.call(resourceManager, executor).thenCombineAsync(templateFuture, (models, templates) -> {

            return models;
        }, executor);
    }

//    @ModifyVariable(method = {"lambda$loadBlockModels$10", "m_245318_"}, at = @At(value = "STORE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"))
//    private static Map.Entry<ResourceLocation, Resource> trimmed$interceptTemplates(
//            Map.Entry<ResourceLocation, Resource> entry,
//            @Share("trimmed-cancelAdd") LocalBooleanRef cancelAdd
//    ) {
//        return interceptTemplates(entry, cancelAdd);
//    }
//
//    @WrapOperation(method = {"lambda$loadBlockModels$10", "m_245318_"}, at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;supplyAsync(Ljava/util/function/Supplier;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
//    private static CompletableFuture<Pair<ResourceLocation, BlockModel>> trimmed$interceptCompletable(
//            Supplier<Pair<ResourceLocation, BlockModel>> supplier,
//            Executor executor,
//            Operation<CompletableFuture<Pair<ResourceLocation, BlockModel>>> original,
//            Executor executor2,
//            Map<ResourceLocation, Resource> resourceMap,
//            @Share("trimmed-cancelAdd") LocalBooleanRef cancelAdd
//    ) {
//        return interceptCompletable(supplier, executor, original, cancelAdd);
//    }
//
//
//    @WrapWithCondition(method = {"lambda$loadBlockModels$10", "m_245318_"}, at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
//    private static boolean trimmed$cancelAddBlockModel(
//            List<CompletableFuture<Pair<ResourceLocation, BlockModel>>> instance,
//            Object future,
//            @Share("trimmed-cancelAdd") LocalBooleanRef cancelAdd
//    ) {
//        return cancelAddBlockModel(cancelAdd);
//    }
}
