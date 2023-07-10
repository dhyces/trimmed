package dhyces.trimmed.impl.client.maps.manager;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import dhyces.trimmed.api.maps.LimitedBiMap;
import dhyces.trimmed.api.maps.LimitedMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.Set;

@ApiStatus.Internal
public class MapHolder<K, V> {
    private Map<K, V> backing;
    private Set<K> optionalKeys;
    private final Interner<LimitedMap<K, V>> mapInterner = Interners.newWeakInterner();
    private SoftReference<LimitedMap<K, V>> directRef;

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
            // TODO: handle updating observers
        }
    }

    @Nullable
    public Map<K, V> getBacking() {
        return backing;
    }

    public LimitedMap<K, V> get() {
        if (directRef == null || directRef.get() == null) {
            directRef = new SoftReference<>(LimitedMap.adapter(this::getBacking, this::isRequired));
        }
        return directRef.get();
    }

    public LimitedBiMap<K, V> getBiMap() {
        return (LimitedBiMap<K, V>) mapInterner.intern(LimitedBiMap.biMapAdapter(backing, this::isRequired));
    }
}
