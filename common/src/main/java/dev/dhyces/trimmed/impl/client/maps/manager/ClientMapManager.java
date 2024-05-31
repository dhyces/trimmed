package dev.dhyces.trimmed.impl.client.maps.manager;

import dev.dhyces.trimmed.Trimmed;
import dev.dhyces.trimmed.api.maps.MapHolder;
import dev.dhyces.trimmed.api.util.Utils;
import dev.dhyces.trimmed.api.maps.MapKey;
import dev.dhyces.trimmed.impl.client.maps.KeyResolvers;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

// Maps have types, which determine key and value. Then there can be many of those maps which can be accessed via key.
// Getting a map from a key before maps have been loaded should return the MapAccess which is then later filled when
// data is loaded. All map types support groups, where the subdirectories are the same as the names.
public class ClientMapManager implements PreparableReloadListener {
    public static final String PATH = "trimmed/maps";
    private static CompletableFuture<Unit> completable = new CompletableFuture<>();
    private static final Map<MapKey<?, ?>, MapHandler<?, ?>> REGISTRY = new Reference2ObjectOpenHashMap<>();

    public static <K, V> void registerBaseKey(MapKey<K, V> key) {
        if (key.isSubKey()) {
            throw new IllegalArgumentException("Illegal id {%s}. Id cannot contain sub-paths \"/\".".formatted(key));
        }
        if (KeyResolvers.getId(key.getType().getKeyResolver()) == null) {
            throw new IllegalArgumentException("KeyResolver for %s is not registered".formatted(key));
        }
        if (!REGISTRY.containsKey(key)) {
            REGISTRY.put(key, new MapHandler<>(key));
        }
    }

    public static <K, V> MapHolder<K, V> getHolder(MapKey<K, V> key) {
        return (MapHolder<K, V>) REGISTRY.computeIfAbsent(key.isSubKey() ? key.getBaseKey() : key, MapHandler::new).getHolder(Utils.unsafeCast(key));
    }

    public static CompletableFuture<Unit> future() {
        return completable;
    }

    private static void finishReload() {
        Trimmed.logInDev("Client maps loaded!");
        // Reset this instance to complete for the next reload
        completable = new CompletableFuture<>();
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier pPreparationBarrier, ResourceManager pResourceManager, ProfilerFiller pPreparationsProfiler, ProfilerFiller pReloadProfiler, Executor pBackgroundExecutor, Executor pGameExecutor) {
        return load(pResourceManager).thenApply(completable::complete).thenCompose(pPreparationBarrier::wait).thenRun(ClientMapManager::finishReload);
    }

    private CompletableFuture<Unit> load(ResourceManager resourceManager) {
        REGISTRY.values().forEach(MapHandler::clear);

        for (Map.Entry<MapKey<?, ?>, MapHandler<?, ?>> entry : REGISTRY.entrySet()) {
            ResourceLocation resolverPath = entry.getKey().getMapId().withPrefix("trimmed/maps/" + Utils.namespacedPath(KeyResolvers.getId(entry.getKey().getType().getKeyResolver())) + "/");

            FileToIdConverter converter = FileToIdConverter.json(resolverPath.getPath());
            try {
                entry.getValue().parse(resolverPath, converter, resourceManager);
            } catch (RuntimeException e) {
                Trimmed.LOGGER.error("Could not read map", e);
            }
        }

        return CompletableFuture.completedFuture(Unit.INSTANCE);
    }
}
