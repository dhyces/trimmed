package dhyces.trimmed.impl.client.maps.manager;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Interner;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.data.maps.MapAppendElement;
import dhyces.trimmed.api.data.maps.MapValue;
import dhyces.trimmed.api.maps.LimitedBiMap;
import dhyces.trimmed.api.maps.LimitedMap;
import dhyces.trimmed.impl.client.maps.ClientMapKey;
import dhyces.trimmed.impl.mixin.TagEntryAccessor;
import dhyces.trimmed.impl.util.OptionalId;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public abstract class BaseMapHandler<MAPKEY, KEY, VAL> {
    protected final Map<MAPKEY, MapHolder<KEY, VAL>> internal = new HashMap<>();
    protected boolean isLoaded = false;

    @Nullable
    public LimitedMap<KEY, VAL> getMap(MAPKEY mapKey) {
        return internal.computeIfAbsent(mapKey, mapkey -> new MapHolder<>()).get();
    }

    @Nullable
    public LimitedBiMap<KEY, VAL> getBiMap(MAPKEY mapKey) {
        return internal.computeIfAbsent(mapKey, mapkey -> new MapHolder<>()).getBiMap();
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

    void resolveMaps(Map<ResourceLocation, Pair<Map<ResourceLocation, MapValue>, Set<MapAppendElement>>> unresolvedMaps) {
        for (Map.Entry<ResourceLocation, Pair<Map<ResourceLocation, MapValue>, Set<MapAppendElement>>> entry : unresolvedMaps.entrySet()) {
            DataResult<MapHolder<KEY, VAL>> dataResult = resolveMap(unresolvedMaps, internal, entry.getKey(), this::createMapKey, this::createKey, this::parseValue, new ReferenceLinkedOpenHashSet<>());
            dataResult.error().ifPresent(mapPartialResult -> Trimmed.LOGGER.error(mapPartialResult.message()));
            updateListeners(this.createMapKey(entry.getKey()));
        }
        isLoaded = true;
    }

    @Deprecated
    protected void updateListeners(MAPKEY key) {
        // TODO
    }

    protected final <MK, K, V> DataResult<MapHolder<K, V>> resolveMap(Map<ResourceLocation, Pair<Map<ResourceLocation, MapValue>, Set<MapAppendElement>>> unresolvedMaps, Map<MK, MapHolder<K, V>> resolvedMaps, ResourceLocation mapId, Function<ResourceLocation, MK> mapKeyFactory, Function<ResourceLocation, DataResult<K>> keyFactory, Function<MapValue, DataResult<V>> valueFactory, ReferenceSet<MK> resolutionSet) {
        if (resolvedMaps.containsKey(mapId) && !resolvedMaps.get(mapId).isEmpty()) {
            return DataResult.success(resolvedMaps.get(mapId));
        }
        MK key = mapKeyFactory.apply(mapId);

        if (resolutionSet.contains(key)) {
            return DataResult.error(() -> "ClientMap cycle detected! " + resolutionSet.stream().map(Object::toString).toList());
        }

        Pair<Map<ResourceLocation, MapValue>, Set<MapAppendElement>> mapFile = unresolvedMaps.get(mapId);

        if (mapFile == null) {
            return DataResult.error(() -> "Map %s does not exist!".formatted(mapId));
        }

        resolutionSet.add(key);

        ImmutableMap.Builder<K, V> mapBuilder = ImmutableMap.builder();

        for (Map.Entry<ResourceLocation, MapValue> entry : mapFile.getFirst().entrySet()) {
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

        for (MapAppendElement appendElement : mapFile.getSecond()) {
            MK appendedMapKey = mapKeyFactory.apply(appendElement.mapId());
            if (resolvedMaps.containsKey(appendedMapKey) && resolvedMaps.get(appendedMapKey).getBacking() != null) {
                mapBuilder.putAll(resolvedMaps.get(appendedMapKey).getBacking());
            } else {
                DataResult<MapHolder<K, V>> result = resolveMap(unresolvedMaps, resolvedMaps, appendElement.mapId(), mapKeyFactory, keyFactory, valueFactory, resolutionSet);
                if (result.error().isPresent()) {
                    return result;
                }
                mapBuilder.putAll(result.result().get().getBacking());
            }
        }

        MapHolder<K, V> holder = resolvedMaps.computeIfAbsent(key, key1 -> new MapHolder<>(mapBuilder.buildKeepingLast()));
        holder.set(mapBuilder.build());
        return DataResult.success(holder);
    }
}
