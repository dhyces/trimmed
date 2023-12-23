package dev.dhyces.trimmed.impl.client.maps.manager;

import dev.dhyces.trimmed.api.maps.LimitedBiMap;
import dev.dhyces.trimmed.api.maps.LimitedMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.Set;

@ApiStatus.Internal
public class MapHolder<K, V> {
    private Map<K, V> backing;
    private Set<K> optionalKeys;
    private SoftReference<LimitedBiMap<K, V>> bimapRef;
    private SoftReference<LimitedMap<K, V>> mapRef;

    public MapHolder(Map<K, V> backing, Set<K> optionalKeys) {
        this.backing = backing;
        this.optionalKeys = optionalKeys;
    }

    public void clear() {
        this.backing = null;
        this.optionalKeys = null;
    }

    public boolean isEmpty() {
        return this.backing == null;
    }

    public boolean isRequired(K key) {
        return !optionalKeys.contains(key);
    }

    void update(Map<K, V> backing, Set<K> optionalKeys) {
        if (this.backing == null) {
            this.backing = backing;
            this.optionalKeys = optionalKeys;
            updateListeners();
        }
    }

    private void updateListeners() {
        if (mapRef != null && mapRef.get() != null) {mapRef.get().onUpdated(backing);}
        if (bimapRef != null && bimapRef.get() != null) {bimapRef.get().onUpdated(backing);}
    }

    @Nullable
    public Map<K, V> getBacking() {
        return backing;
    }

    public LimitedMap<K, V> get() {
        if (mapRef == null || mapRef.get() == null) {
            mapRef = new SoftReference<>(LimitedMap.adapter(this::getBacking, this::isRequired));
        }
        return mapRef.get();
    }

    public LimitedBiMap<K, V> getBiMap() {
        if (bimapRef == null || bimapRef.get() == null) {
            bimapRef = new SoftReference<>(LimitedBiMap.biMapAdapter(backing, this::isRequired));
        }
        return bimapRef.get();
    }
}
