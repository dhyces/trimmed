package dhyces.trimmed.impl.client.maps.manager;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.data.maps.MapValue;
import dhyces.trimmed.impl.client.maps.manager.delegates.BaseMapDelegate;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.BiFunction;
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
    protected abstract V createKey(ResourceLocation key, MapValue value);

    public boolean hasLoaded() {
        return isLoaded;
    }

    void clear() {
        registeredMaps.clear();
        isLoaded = false;
    }

    void resolveMaps(Map<ResourceLocation, Set<Map.Entry<ResourceLocation, MapValue>>> unresolvedMaps) {
        for (Map.Entry<ResourceLocation, Set<Map.Entry<ResourceLocation, MapValue>>> entry : unresolvedMaps.entrySet()) {
            DataResult<Map<V, String>> dataResult = resolveMap(unresolvedMaps, registeredMaps, entry.getKey(), this::createMapKey, this::createKey, new LinkedHashSet<>());
            dataResult.error().ifPresent(mapPartialResult -> Trimmed.LOGGER.error(mapPartialResult.message()));
        }
        isLoaded = true;
    }

    protected final <KEY, VAL> DataResult<Map<VAL, String>> resolveMap(Map<ResourceLocation, Set<Map.Entry<ResourceLocation, MapValue>>> unresolvedMaps, Map<KEY, Map<VAL, String>> resolvedMaps, ResourceLocation mapId, Function<ResourceLocation, KEY> mapKeyFactory, BiFunction<ResourceLocation, MapValue, VAL> keyFactory, LinkedHashSet<ResourceLocation> resolutionSet) {
        if (resolvedMaps.containsKey(mapId)) {
            return DataResult.success(resolvedMaps.get(mapId));
        }
        KEY key = mapKeyFactory.apply(mapId);

        Set<Map.Entry<ResourceLocation, MapValue>> entries = unresolvedMaps.get(mapId);

        if (entries == null) {
            return DataResult.error(() -> "Map %s does not exist!".formatted(mapId));
        }

        ImmutableMap.Builder<VAL, String> mapBuilder = ImmutableMap.builder();

        for (Map.Entry<ResourceLocation, MapValue> entry : entries) {
            VAL resolvedKey = keyFactory.apply(entry.getKey(), entry.getValue());
            if (resolvedKey != null) {
                mapBuilder.put(resolvedKey, entry.getValue().value());
            } else if (entry.getValue().isRequired()) {
                return DataResult.error(() -> "Map key %s is not present and is required!".formatted(entry.getKey()));
            }
        }

        return DataResult.success(resolvedMaps.computeIfAbsent(key, key1 -> mapBuilder.build()));
    }
}
