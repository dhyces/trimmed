package dhyces.trimmed.impl.mixin.client;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.datafixers.util.Pair;
import dhyces.trimmed.impl.client.models.template.ModelTemplateManager;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Debug(export = true)
@Mixin(ModelManager.class)
public class BakedModelManagerMixin {

//    @ModifyVariable(method = "method_45899", at = @At(value = "STORE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"))
//    private static Map.Entry<ResourceLocation, Resource> trimmed$interceptTemplatesIntermediary(
//            Map.Entry<ResourceLocation, Resource> entry,
//            @Share("trimmed-cancelAdd") LocalBooleanRef cancelAdd
//    ) {
//        return interceptTemplates(entry, cancelAdd);
//    }

    @ModifyVariable(method = "lambda$loadBlockModels$10", at = @At(value = "STORE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"))
    private static Map.Entry<ResourceLocation, Resource> trimmed$interceptTemplatesSrg(
            Map.Entry<ResourceLocation, Resource> entry,
            @Share("trimmed-cancelAdd") LocalBooleanRef cancelAdd
    ) {
        return interceptTemplates(entry, cancelAdd);
    }

//    @WrapOperation(method = "method_45899", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;supplyAsync(Ljava/util/function/Supplier;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
//    private static CompletableFuture<Pair<ResourceLocation, BlockModel>> trimmed$interceptCompletableIntermediary(
//            Supplier<Pair<ResourceLocation, BlockModel>> supplier,
//            Executor executor,
//            Operation<CompletableFuture<Pair<ResourceLocation, BlockModel>>> original,
//            Executor executor2,
//            Map<ResourceLocation, Resource> resourceMap,
//            @Share("trimmed-cancelAdd") LocalBooleanRef cancelAdd
//    ) {
//        return interceptCompletable(supplier, executor, original, cancelAdd);
//    }

    @WrapOperation(method = "lambda$loadBlockModels$10", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;supplyAsync(Ljava/util/function/Supplier;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
    private static CompletableFuture<Pair<ResourceLocation, BlockModel>> trimmed$interceptCompletableSrg(
            Supplier<Pair<ResourceLocation, BlockModel>> supplier,
            Executor executor,
            Operation<CompletableFuture<Pair<ResourceLocation, BlockModel>>> original,
            Executor executor2,
            Map<ResourceLocation, Resource> resourceMap,
            @Share("trimmed-cancelAdd") LocalBooleanRef cancelAdd
    ) {
        return interceptCompletable(supplier, executor, original, cancelAdd);
    }

//    @WrapWithCondition(method = "method_45899", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
//    private static boolean trimmed$cancelAddBlockModelIntermediary(
//            List<CompletableFuture<Pair<ResourceLocation, BlockModel>>> instance,
//            Object future,
//            @Share("trimmed-cancelAdd") LocalBooleanRef cancelAdd
//    ) {
//        return cancelAddBlockModel(cancelAdd);
//    }

    @WrapWithCondition(method = "lambda$loadBlockModels$10", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private static boolean trimmed$cancelAddBlockModelSrg(
            List<CompletableFuture<Pair<ResourceLocation, BlockModel>>> instance,
            Object future,
            @Share("trimmed-cancelAdd") LocalBooleanRef cancelAdd
    ) {
        return cancelAddBlockModel(cancelAdd);
    }

    private static Map.Entry<ResourceLocation, Resource> interceptTemplates(Map.Entry<ResourceLocation, Resource> entry, LocalBooleanRef cancelAdd) {
        cancelAdd.set(false);
        if (entry.getKey().getPath().endsWith(".trimmed_template.json")) {
            cancelAdd.set(true);
            ModelTemplateManager.addTemplateResource(entry.getKey(), () -> entry.getValue().openAsReader());
        }
        return entry;
    }

    private static CompletableFuture<Pair<ResourceLocation, BlockModel>> interceptCompletable(
            Supplier<Pair<ResourceLocation, BlockModel>> supplier,
            Executor executor,
            Operation<CompletableFuture<Pair<ResourceLocation, BlockModel>>> original,
            LocalBooleanRef cancelAdd
    ) {
        return cancelAdd.get() ? null : original.call(supplier, executor);
    }

    private static boolean cancelAddBlockModel(LocalBooleanRef cancelAdd) {
        return !cancelAdd.get();
    }
}
