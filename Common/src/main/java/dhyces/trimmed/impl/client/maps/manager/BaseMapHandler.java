package dhyces.trimmed.impl.client.maps.manager;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.DataResult;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.data.maps.MapValue;
import dhyces.trimmed.impl.client.maps.manager.delegates.BiMapMapDelegate;
import dhyces.trimmed.impl.client.maps.manager.delegates.HashMapDelegate;
import dhyces.trimmed.impl.client.maps.manager.delegates.LazyMapDelegate;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class BaseMapHandler<K, V> {
    protected final Map<K, Map<V, String>> registeredMaps = new HashMap<>();
    protected final Multimap<K, WeakReference<MapLoadListener<V>>> listeners = HashMultimap.create();
    protected boolean isLoaded = false;

    @Nullable
    public Map<V, String> getMap(K mapKey) {
        return registeredMaps.get(mapKey);
    }

    public <T> HashMapDelegate<V, T> hashMapDelegate(K key, Function<String, DataResult<T>> mappingFunction) {
        HashMapDelegate<V, T> delegate = new HashMapDelegate<>(mappingFunction);
        listeners.put(key, new WeakReference<>(delegate));
        return delegate;
    }

    public <T> BiMapMapDelegate<V, T> biMapDelegate(K key, Function<String, DataResult<T>> forwardMappingFunction, Function<String, DataResult<V>> inverseMappingFunction) {
        BiMapMapDelegate<V, T> delegate = new BiMapMapDelegate<>(forwardMappingFunction, inverseMappingFunction);
        listeners.put(key, new WeakReference<>(delegate));
        return delegate;
    }

    public <T> LazyMapDelegate<V, T> lazyMapDelegate(K key, Function<String, DataResult<T>> mappingFunction) {
        LazyMapDelegate<V, T> delegate = new LazyMapDelegate<>(mappingFunction);
        listeners.put(key, new WeakReference<>(delegate));
        return delegate;
    }

    public void addListener(K key, MapLoadListener<V> mapLoadListener) {
        listeners.put(key, new WeakReference<>(mapLoadListener));
    }

    /**
     * This is for the MapKey
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
            updateListeners(this.createMapKey(entry.getKey()));
        }
        isLoaded = true;
    }

    protected void updateListeners(K key) {
        Map<V, String> map = registeredMaps.get(key);
        for (Iterator<WeakReference<MapLoadListener<V>>> iter = listeners.get(key).iterator(); iter.hasNext();) {
            MapLoadListener<V> listener = iter.next().get();
            if (listener == null) {
                iter.remove();
            } else {
                listener.onReload(map);
            }
        }
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

    public interface MapLoadListener<K> {
        void onReload(Map<K, String> map);
    }
}
