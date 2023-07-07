package dhyces.trimmed.impl.client.models.override;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dhyces.trimmed.modhelper.services.Services;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import net.minecraft.Util;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ItemOverrideReloadListener implements PreparableReloadListener {
    private static final Logger LOGGER = LoggerFactory.getLogger("Trimmed/Item Model Overrides");
    private static final Set<ModelResourceLocation> MODELS_TO_ADD = new HashSet<>();

    private static final FileToIdConverter OVERRIDES_FINDER = FileToIdConverter.json("models/item/overrides");

    @Override
    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier synchronizer, ResourceManager manager, ProfilerFiller prepareProfiler, ProfilerFiller applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        MODELS_TO_ADD.clear();
        ItemOverrideRegistry.clearRegistry();
        return parseAll(manager)
                .thenCompose(synchronizer::wait)
                .thenAccept(pairs -> {
                    pairs.forEach(pair -> ItemOverrideRegistry.addOverrideSet(pair.getFirst(), pair.getSecond()));
                    Trimmed.logInDev("Item model overrides loaded!");
                });
    }

    private CompletableFuture<List<Pair<ResourceLocation, Set<ItemOverrideProvider>>>> parseAll(ResourceManager manager) {
        List<Pair<ResourceLocation, Set<ItemOverrideProvider>>> list = new ArrayList<>();
        for (Map.Entry<ResourceLocation, List<Resource>> entry : OVERRIDES_FINDER.listMatchingResourceStacks(manager).entrySet()) {
            ResourceLocation id = OVERRIDES_FINDER.fileToId(entry.getKey());
            Set<ItemOverrideProvider> providers = new LinkedHashSet<>();
            for (Resource resource : entry.getValue()) {
                try (BufferedReader reader = resource.openAsReader()) {
                    JsonObject jsonObject = GsonHelper.parse(reader, true);
                    if (!Services.PLATFORM_HELPER.shouldPassConditions(jsonObject)) {
                        Trimmed.LOGGER.debug("Skipping loading item overrides from {} as its conditions were not met", id);
                        continue;
                    }
                    DataResult<List<ItemOverrideProvider>> providerResult = ItemOverrideProvider.LIST_CODEC.parse(JsonOps.INSTANCE, jsonObject);
                    List<ItemOverrideProvider> providerList = providerResult.getOrThrow(false, s -> {});
                    providerList.forEach(itemOverrideProvider -> {
                        itemOverrideProvider.finish(id);
                        providers.add(itemOverrideProvider);
                    });
                } catch (Exception e) {
                    LOGGER.error("Could not read %s: ".formatted(entry.getKey()), e);
                }
            }
            providers.stream().flatMap(ItemOverrideProvider::getModelsToBake).forEachOrdered(MODELS_TO_ADD::add);
            list.add(Pair.of(id, providers));
        }
        return CompletableFuture.completedFuture(list);
    }

    public static Collection<ModelResourceLocation> getModelsToBake() {
        return Collections.unmodifiableCollection(MODELS_TO_ADD);
    }
}
