package dhyces.trimmed.client.override;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dhyces.trimmed.TrimmedClient;
import dhyces.trimmed.client.override.provider.ItemOverrideProvider;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ItemOverrideReloadListener implements IdentifiableResourceReloadListener {
    private static final Set<ModelIdentifier> MODELS_TO_ADD = new HashSet<>();
    public static final Codec<List<ItemOverrideProvider>> ITEM_OVERRIDE_CODEC = ItemOverrideProvider.CODEC.listOf().fieldOf("values").codec();

    private static final ResourceFinder OVERRIDES_FINDER = ResourceFinder.json("models/item/overrides");

    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        MODELS_TO_ADD.clear();
        ItemOverrideRegistry.clearRegistry();
        return CompletableFuture.supplyAsync(() -> OVERRIDES_FINDER.findAllResources(manager), prepareExecutor)
                .thenCompose(identifierListMap -> parseAll(identifierListMap, prepareExecutor)).thenCompose(synchronizer::whenPrepared).thenAccept(pairs -> pairs.forEach(pair -> ItemOverrideRegistry.addOverrideSet(pair.getFirst(), pair.getSecond())));
    }

    private CompletableFuture<List<Pair<ModelIdentifier, OverrideSet>>> parseAll(final Map<Identifier, List<Resource>> map, Executor prepareExecutor) {
        List<CompletableFuture<Pair<ModelIdentifier, OverrideSet>>> futures = new ArrayList<>();
        for (Map.Entry<Identifier, List<Resource>> entry : map.entrySet()) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                Identifier cleanId = new Identifier(entry.getKey().getNamespace(), entry.getKey().getPath()
                        .replace("models/item/overrides/", "")
                        .replace(".json", ""));
                ModelIdentifier id = new ModelIdentifier(cleanId, "inventory");
                OverrideSet overrideSet = new OverrideSet(id);
                for (Resource resource : entry.getValue()) {
                    try (BufferedReader reader = resource.getReader()) {
                        JsonObject jsonObject = JsonHelper.deserialize(reader, true);
                        DataResult<List<ItemOverrideProvider>> providerResult = ITEM_OVERRIDE_CODEC.parse(JsonOps.INSTANCE, jsonObject);
                        if (providerResult.error().isPresent()) {
                            TrimmedClient.LOGGER.error(providerResult.error().get().message());
                            continue;
                        }
                        overrideSet.addProviders(providerResult.result().get());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                overrideSet.getModelsToBake().forEachOrdered(MODELS_TO_ADD::add);
                return Pair.of(id, overrideSet);
            }, prepareExecutor));
        }
        return Util.combineSafe(futures);
    }

    public static Collection<ModelIdentifier> getModelsToBake() {
        return Collections.unmodifiableCollection(MODELS_TO_ADD);
    }

    @Override
    public Identifier getFabricId() {
        return TrimmedClient.id("item_overrides");
    }
}
