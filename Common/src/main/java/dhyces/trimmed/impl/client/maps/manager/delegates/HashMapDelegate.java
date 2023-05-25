package dhyces.trimmed.impl.client.maps.manager.delegates;

import com.mojang.serialization.DataResult;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HashMapDelegate<K, V> extends BaseMapDelegate<K, V> {

    private final Map<K, V> delegate = new HashMap<>();

    public HashMapDelegate(Function<String, DataResult<V>> mappingFunction) {
        super(mappingFunction);
    }

    @ApiStatus.Internal
    @Override
    public void onReload(Map<K, String> underlyingMap) {
        delegate.clear();
        super.onReload(underlyingMap);
    }

    @Override
    protected void onMapped(K key, V mappedValue) {
        delegate.put(key, mappedValue);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return delegate.get(key);
    }

    @NotNull
    @UnmodifiableView
    @Override
    public Set<K> keySet() {
        return Collections.unmodifiableSet(delegate.keySet());
    }

    @NotNull
    @UnmodifiableView
    @Override
    public Collection<V> values() {
        return Collections.unmodifiableCollection(delegate.values());
    }

    @NotNull
    @UnmodifiableView
    @Override
    public Set<Entry<K, V>> entrySet() {
        return delegate.entrySet().stream().map(kvEntry -> Map.entry(kvEntry.getKey(), kvEntry.getValue())).collect(Collectors.toUnmodifiableSet());
    }
}
