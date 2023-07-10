package dhyces.trimmed.impl.util;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.datafixers.util.Pair;
import dhyces.trimmed.impl.client.models.template.ModelTemplateManager;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class BakedModelManagerMixinUtil {
    public static Map.Entry<ResourceLocation, Resource> interceptTemplates(Map.Entry<ResourceLocation, Resource> entry, LocalBooleanRef cancelAdd) {
        cancelAdd.set(false);
        if (entry.getKey().getPath().endsWith(".trimmed_template.json")) {
            cancelAdd.set(true);
            ModelTemplateManager.addTemplateResource(entry.getKey(), () -> entry.getValue().openAsReader());
        }
        return entry;
    }

    public static CompletableFuture<Pair<ResourceLocation, BlockModel>> interceptCompletable(
            Supplier<Pair<ResourceLocation, BlockModel>> supplier,
            Executor executor,
            Operation<CompletableFuture<Pair<ResourceLocation, BlockModel>>> original,
            LocalBooleanRef cancelAdd
    ) {
        return cancelAdd.get() ? null : original.call(supplier, executor);
    }

    public static boolean cancelAddBlockModel(LocalBooleanRef cancelAdd) {
        return !cancelAdd.get();
    }
}
