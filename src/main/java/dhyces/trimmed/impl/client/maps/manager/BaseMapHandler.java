package dhyces.trimmed.impl.client.maps.manager;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.DataResult;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.data.maps.MapEntry;
import dhyces.trimmed.impl.client.maps.manager.delegates.BaseMapDelegate;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Function;

public abstract class BaseMapHandler<K, V> {
    protected final Map<K, Map<V, String>> registeredMaps = new HashMap<>();
    private final List<WeakReference<BaseMapDelegate<V, ?>>> listeners = new ArrayList<>();
    protected boolean isLoaded = false;

    @Nullable
    public Map<V, String> getMap(K mapKey) {
        return registeredMaps.get(mapKey);
    }

    /**
     * This is for the MapKey<T>
     */
    protected abstract K createMapKey(ResourceLocation mapId);

    /**
     * This is for the actual internal map, ie "minecraft:iron_ingot" -> "SomeValue"
     */
    protected abstract V createKey(ResourceLocation keyId);

    public boolean hasLoaded() {
        return isLoaded;
    }

    void clear() {
        registeredMaps.clear();
        isLoaded = false;
    }

    void resolveMaps(Map<ResourceLocation, Map<ResourceLocation, MapEntry>> unresolvedMaps) {
        for (Map.Entry<ResourceLocation, Map<ResourceLocation, MapEntry>> entry : unresolvedMaps.entrySet()) {
            DataResult<Map<V, String>> dataResult = resolveMap(unresolvedMaps, registeredMaps, entry.getKey(), this::createMapKey, this::createKey, new LinkedHashSet<>());
            dataResult.error().ifPresent(mapPartialResult -> Trimmed.LOGGER.error(mapPartialResult.message()));
        }
        isLoaded = true;
    }

    static <KEY, VAL> DataResult<Map<VAL, String>> resolveMap(Map<ResourceLocation, Map<ResourceLocation, MapEntry>> unresolvedMaps, Map<KEY, Map<VAL, String>> resolvedMaps, ResourceLocation mapId, Function<ResourceLocation, KEY> mapKeyFactory, Function<ResourceLocation, VAL> keyFactory, LinkedHashSet<ResourceLocation> resolutionSet) {
        KEY key = mapKeyFactory.apply(mapId);

        ImmutableMap.Builder<VAL, String> mapBuilder = ImmutableMap.builder();

        for (Map.Entry<ResourceLocation, MapEntry> entry : unresolvedMaps.get(mapId).entrySet()) {
            ResourceLocation rl = entry.getKey();
            MapEntry mapEntry = entry.getValue();

            VAL resolvedKey = keyFactory.apply(rl);
            if (resolvedKey != null) {
                mapBuilder.put(resolvedKey, mapEntry.value());
            } else if (!mapEntry.optional()) {
                return DataResult.error(() -> "Map key %s is not present and is required!".formatted(rl));
            }
        }

        return DataResult.success(resolvedMaps.computeIfAbsent(key, key1 -> mapBuilder.build()));
    }
}
