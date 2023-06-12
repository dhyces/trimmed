package dhyces.trimmed.impl.client.maps.manager;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import dhyces.trimmed.api.maps.LimitedBiMap;
import dhyces.trimmed.api.maps.LimitedMap;
import org.jetbrains.annotations.ApiStatus;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public class MapHolder<K, V> {
    private Map<K, V> backing;
    private final Interner<LimitedMap<K, V>> mapInterner = Interners.newWeakInterner();
    private SoftReference<LimitedMap<K, V>> directRef;

    public MapHolder() {
        this(new HashMap<>());
    }

    public MapHolder(Map<K, V> backing) {
        this.backing = backing;
    }

    public void clear() {
        this.backing = null;
    }

    public boolean isEmpty() {
        return this.backing == null;
    }

    public void set(Map<K, V> backing) {
        if (this.backing == null) {
            this.backing = backing;
        }
    }

    public LimitedMap<K, V> get() {
        if (directRef == null || directRef.get() == null) {
            directRef = new SoftReference<>(LimitedMap.adapter(() -> backing));
        }
        return directRef.get();
    }

    public LimitedBiMap<K, V> getBiMap() {
        return (LimitedBiMap<K, V>) mapInterner.intern(LimitedBiMap.biMapAdapter(backing));
    }
}
