package dhyces.trimmed.impl.client.override;

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
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ItemOverrideReloadListener implements PreparableReloadListener {
    private static final Set<ModelResourceLocation> MODELS_TO_ADD = new HashSet<>();

    private static final FileToIdConverter OVERRIDES_FINDER = FileToIdConverter.json("models/item/overrides");

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier synchronizer, ResourceManager manager, ProfilerFiller prepareProfiler, ProfilerFiller applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        MODELS_TO_ADD.clear();
        ItemOverrideRegistry.clearRegistry();
        return CompletableFuture.supplyAsync(() -> OVERRIDES_FINDER.listMatchingResourceStacks(manager), prepareExecutor)
                .thenCompose(identifierListMap -> parseAll(identifierListMap, prepareExecutor))
                .thenCompose(synchronizer::wait)
                .thenAccept(pairs -> {
                    pairs.forEach(pair -> ItemOverrideRegistry.addOverrideSet(pair.getFirst(), pair.getSecond()));
                    Trimmed.logInDev("Item model overrides loaded!");
                });
    }

    private CompletableFuture<List<Pair<ModelResourceLocation, Set<ItemOverrideProvider>>>> parseAll(final Map<ResourceLocation, List<Resource>> map, Executor prepareExecutor) {
        List<CompletableFuture<Pair<ModelResourceLocation, Set<ItemOverrideProvider>>>> futures = new ArrayList<>();
        for (Map.Entry<ResourceLocation, List<Resource>> entry : map.entrySet()) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                ResourceLocation cleanId = OVERRIDES_FINDER.fileToId(entry.getKey());
                ModelResourceLocation id = new ModelResourceLocation(cleanId, "inventory");
                Set<ItemOverrideProvider> providers = new LinkedHashSet<>();
                for (Resource resource : entry.getValue()) {
                    try (BufferedReader reader = resource.openAsReader()) {
                        JsonObject jsonObject = GsonHelper.parse(reader, true);
                        if (!Services.PLATFORM_HELPER.shouldPassConditions(jsonObject)) {
                            Trimmed.LOGGER.debug("Skipping loading item overrides from {} as its conditions were not met", cleanId);
                            continue;
                        }
                        DataResult<List<ItemOverrideProvider>> providerResult = ItemOverrideProvider.LIST_CODEC.parse(JsonOps.INSTANCE, jsonObject);
                        if (providerResult.error().isPresent()) {
                            Trimmed.LOGGER.error(providerResult.error().get().message());
                            continue;
                        }
                        providers.addAll(providerResult.result().get());
                    } catch (IOException e) {
                        throw new RuntimeException("Could not read %s: ".formatted(entry.getKey()), e);
                    }
                }
                providers.stream().flatMap(ItemOverrideProvider::getModelsToBake).forEachOrdered(MODELS_TO_ADD::add);
                return Pair.of(id, providers);
            }, prepareExecutor));
        }
        return Util.sequence(futures);
    }

    public static Collection<ModelResourceLocation> getModelsToBake() {
        return Collections.unmodifiableCollection(MODELS_TO_ADD);
    }
}
