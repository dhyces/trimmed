package dhyces.trimmed.impl.client.maps.manager;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.DataResult;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.data.maps.MapAppendElement;
import dhyces.trimmed.api.data.maps.MapFile;
import dhyces.trimmed.api.data.maps.MapValue;
import dhyces.trimmed.api.maps.LimitedBiMap;
import dhyces.trimmed.api.maps.LimitedMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class BaseMapHandler<MAPKEY, KEY, VAL> {
    protected final Map<MAPKEY, MapHolder<KEY, VAL>> internal;
    protected final Supplier<Map<KEY, VAL>> holderMapSupplier;
    protected boolean isLoaded = false;

    public BaseMapHandler(Supplier<Map<KEY, VAL>> mapSupplier) {
        this.internal = new Reference2ObjectOpenHashMap<>();
        this.holderMapSupplier = mapSupplier;
    }

    public LimitedMap<KEY, VAL> getMap(MAPKEY mapKey) {
        return internal.computeIfAbsent(mapKey, mapkey -> new MapHolder<>(holderMapSupplier.get(), new HashSet<>())).get();
    }

    public LimitedBiMap<KEY, VAL> getBiMap(MAPKEY mapKey) {
        return internal.computeIfAbsent(mapKey, mapkey -> new MapHolder<>(holderMapSupplier.get(), new HashSet<>())).getBiMap();
    }

    @ApiStatus.Internal
    public Optional<MapHolder<KEY, VAL>> getHolder(MAPKEY mapKey) {
        return Optional.ofNullable(internal.get(mapKey));
    }

    public VAL getValue(MAPKEY mapKey, KEY key) {
        if (!internal.containsKey(mapKey)) {
            return null;
        }

        Map<KEY, VAL> backingMap = internal.get(mapKey).getBacking();
        if (backingMap == null) {
            return null;
        }

        return backingMap.get(key);
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

    void resolveMaps(Map<ResourceLocation, MapFile> unresolvedMaps) {
        for (Map.Entry<ResourceLocation, MapFile> entry : unresolvedMaps.entrySet()) {
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

    protected final <MK, K, V> DataResult<MapHolder<K, V>> resolveMap(Map<ResourceLocation, MapFile> unresolvedMaps, Map<MK, MapHolder<K, V>> resolvedMaps, ResourceLocation mapId, Function<ResourceLocation, MK> mapKeyFactory, Function<ResourceLocation, DataResult<K>> keyFactory, Function<MapValue, DataResult<V>> valueFactory, ReferenceSet<MK> resolutionSet) {
        MK key = mapKeyFactory.apply(mapId);
        if (resolvedMaps.containsKey(key) && !resolvedMaps.get(key).isEmpty()) {
            return DataResult.success(resolvedMaps.get(key));
        }

        if (resolutionSet.contains(key)) {
            return DataResult.error(() -> "ClientMap cycle detected! " + resolutionSet.stream().map(Object::toString).toList());
        }

        MapFile mapFile = unresolvedMaps.get(mapId);

        if (mapFile == null) {
            return DataResult.error(() -> "Map %s does not exist!".formatted(mapId));
        }

        resolutionSet.add(key);

        ImmutableMap.Builder<K, V> mapBuilder = ImmutableMap.builder();
        // Track optionals instead of required, since it's likely to be smaller
        ImmutableSet.Builder<K> optionalEntriesBuilder = ImmutableSet.builder();

        for (Map.Entry<ResourceLocation, MapValue> entry : mapFile.map().entrySet()) {
            DataResult<K> resolvedKeyResult = keyFactory.apply(entry.getKey());
            boolean requiredEntry = entry.getValue().isRequired();
            if (resolvedKeyResult.error().isPresent() && requiredEntry) {
                return resolvedKeyResult.map(val -> null);
            }
            K resolvedKey = resolvedKeyResult.result().get();

            DataResult<V> parsedValue = valueFactory.apply(entry.getValue());
            if (parsedValue.error().isPresent()) {
                return parsedValue.map(v -> null);
            }

            mapBuilder.put(resolvedKey, parsedValue.result().get());

            if (!requiredEntry) {
                optionalEntriesBuilder.add(resolvedKey);
            }
        }

        for (MapAppendElement appendElement : mapFile.appendElements()) {
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

        MapHolder<K, V> holder;
        if (resolvedMaps.containsKey(key)) {
            holder = resolvedMaps.get(key);
            holder.update(mapBuilder.buildKeepingLast(), optionalEntriesBuilder.build());
        } else {
            holder = new MapHolder<>(mapBuilder.buildKeepingLast(), optionalEntriesBuilder.build());
            resolvedMaps.put(key, holder);
        }

        return DataResult.success(holder);
    }
}
