package dhyces.trimmed.impl.client.maps.manager;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Interner;
import com.mojang.serialization.DataResult;
import dhyces.trimmed.QuiltTrimmed;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.data.maps.MapValue;
import dhyces.trimmed.api.maps.LimitedBiMap;
import dhyces.trimmed.api.maps.LimitedMap;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public abstract class BaseMapHandler<MAPKEY, KEY, VAL> {
    protected final Map<MAPKEY, MapHolder<KEY, VAL>> internal = new HashMap<>();
    protected boolean isLoaded = false;

    @Nullable
    public LimitedMap<KEY, VAL> getMap(MAPKEY mapKey) {
        return internal.computeIfAbsent(mapKey, mapkey -> new MapHolder<>(this)).get();
    }

    @Nullable
    public LimitedBiMap<KEY, VAL> getBiMap(MAPKEY mapKey) {
        return internal.computeIfAbsent(mapKey, mapkey -> new MapHolder<>(this)).getBiMap();
    }

    /**
     * This is for the MapKey
     */
    protected abstract MAPKEY createMapKey(ResourceLocation mapId);

    /**
     * This is for the actual internal map, ie "minecraft:iron_ingot" -> "SomeValue"
     */
    protected abstract DataResult<KEY> createKey(ResourceLocation key);

    protected abstract DataResult<VAL> parseValue(MapValue mapValue);

    public boolean hasLoaded() {
        return isLoaded;
    }

    void clear() {
        internal.forEach((mapkey, keyvalMapHolder) -> keyvalMapHolder.clear());
        isLoaded = false;
    }

    void resolveMaps(Map<ResourceLocation, Set<Map.Entry<ResourceLocation, MapValue>>> unresolvedMaps) {
        for (Map.Entry<ResourceLocation, Set<Map.Entry<ResourceLocation, MapValue>>> entry : unresolvedMaps.entrySet()) {
            DataResult<MapHolder<KEY, VAL>> dataResult = resolveMap(unresolvedMaps, internal, entry.getKey(), this::createMapKey, this::createKey, this::parseValue, new LinkedHashSet<>());
            dataResult.error().ifPresent(mapPartialResult -> QuiltTrimmed.LOGGER.error(mapPartialResult.message()));
            updateListeners(this.createMapKey(entry.getKey()));
        }
        isLoaded = true;
    }

    @Deprecated
    protected void updateListeners(MAPKEY key) {
        // TODO
    }

    protected final <MK, K, V> DataResult<MapHolder<K, V>> resolveMap(Map<ResourceLocation, Set<Map.Entry<ResourceLocation, MapValue>>> unresolvedMaps, Map<MK, MapHolder<K, V>> resolvedMaps, ResourceLocation mapId, Function<ResourceLocation, MK> mapKeyFactory, Function<ResourceLocation, DataResult<K>> keyFactory, Function<MapValue, DataResult<V>> valueFactory, LinkedHashSet<ResourceLocation> resolutionSet) {
        if (resolvedMaps.containsKey(mapId) && !resolvedMaps.get(mapId).isEmpty()) {
            return DataResult.success(resolvedMaps.get(mapId));
        }
        MK key = mapKeyFactory.apply(mapId);

        Set<Map.Entry<ResourceLocation, MapValue>> entries = unresolvedMaps.get(mapId);

        if (entries == null) {
            return DataResult.error(() -> "Map %s does not exist!".formatted(mapId));
        }

        ImmutableMap.Builder<K, V> mapBuilder = ImmutableMap.builder();

        for (Map.Entry<ResourceLocation, MapValue> entry : entries) {
            DataResult<K> resolvedKey = keyFactory.apply(entry.getKey());
            if (resolvedKey.error().isPresent() && entry.getValue().isRequired()) {
                return resolvedKey.map(val -> null);
            }
            DataResult<V> parsedValue = valueFactory.apply(entry.getValue());
            if (parsedValue.error().isPresent()) {
                return parsedValue.map(v -> null);
            }
            mapBuilder.put(resolvedKey.result().get(), parsedValue.result().get());
        }

        MapHolder<K, V> holder = resolvedMaps.computeIfAbsent(key, key1 -> new MapHolder<>(mapBuilder.build()));
        holder.set(mapBuilder.build());
        return DataResult.success(holder);
    }

    public interface MapLoadListener<K> {
        void onReload(Map<K, MapValue> map);
    }
}
