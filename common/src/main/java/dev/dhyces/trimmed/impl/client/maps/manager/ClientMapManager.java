package dev.dhyces.trimmed.impl.client.maps.manager;

import dev.dhyces.trimmed.Trimmed;
import dev.dhyces.trimmed.api.maps.MapHolder;
import dev.dhyces.trimmed.api.util.Utils;
import dev.dhyces.trimmed.api.maps.MapKey;
import dev.dhyces.trimmed.impl.client.maps.MapKeyResolvers;
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
    private static final CompletableFuture<Unit> COMPLETABLE = new CompletableFuture<>();
    private static final Map<MapKey<?, ?>, MapHandler<?, ?>> REGISTRY = new Reference2ObjectOpenHashMap<>();

    public static <K, V> void registerBaseKey(MapKey<K, V> key) {
        if (key.getMapId().getPath().contains("/")) {
            throw new IllegalArgumentException("Illegal id {%s}. Id cannot contain sub-paths \"/\".".formatted(key));
        }
        if (MapKeyResolvers.getId(key.getType().getKeyResolver()) == null) {
            throw new IllegalArgumentException("MapKeyResolver for %s is not registered".formatted(key));
        }
        if (REGISTRY.containsKey(key)) {
            throw new IllegalArgumentException("MapType already registered for " + key);
        }
        REGISTRY.put(key, new MapHandler<>(key));
    }

    public static <K, V> MapHolder<K, V> getHolder(MapKey<K, V> key) {
        return (MapHolder<K, V>) REGISTRY.computeIfAbsent(key.isSubKey() ? key.getBaseKey() : key, MapHandler::new).getHolder(Utils.unsafeCast(key));
    }

    public static CompletableFuture<Unit> future() {
        return COMPLETABLE;
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier pPreparationBarrier, ResourceManager pResourceManager, ProfilerFiller pPreparationsProfiler, ProfilerFiller pReloadProfiler, Executor pBackgroundExecutor, Executor pGameExecutor) {
        return load(pResourceManager).thenApply(COMPLETABLE::complete).thenCompose(pPreparationBarrier::wait).thenRun(() -> Trimmed.logInDev("Client maps loaded!"));
    }

    private CompletableFuture<Unit> load(ResourceManager resourceManager) {
        // TODO: Test if this can be safely moved into the later iteration. Current thought is that if an error occurs
        //  that isn't caught here, it won't clear later handlers and make things weird
        REGISTRY.values().forEach(MapHandler::clear);

        for (Map.Entry<MapKey<?, ?>, MapHandler<?, ?>> entry : REGISTRY.entrySet()) {
            ResourceLocation resolverPath = entry.getKey().getMapId().withPrefix("trimmed/maps/" + Utils.namespacedPath(MapKeyResolvers.getId(entry.getKey().getType().getKeyResolver()), '/') + "/");

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
