package dhyces.trimmed.impl.client.maps.manager.delegates;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.DataResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BiMapMapDelegate<K, V> extends BaseMapDelegate<K, V> implements BiMap<K, V> {

    private final BiMap<K, V> delegate;
    private final Function<String, DataResult<K>> inverseMappingFunction;

    BiMapMapDelegate(Function<String, DataResult<V>> forwardMappingFunction, Function<String, DataResult<K>> inverseMappingFunction) {
        super(forwardMappingFunction);
        delegate = HashBiMap.create();
        this.inverseMappingFunction = inverseMappingFunction;
    }

    private BiMapMapDelegate(Function<String, DataResult<V>> forwardMappingFunction, Function<String, DataResult<K>> inverseMappingFunction, BiMap<K, V> map) {
        super(forwardMappingFunction);
        delegate = map;
        this.inverseMappingFunction = inverseMappingFunction;
    }

    @Override
    void onReload(Map<K, String> underlyingMap) {
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

    @Nullable
    @Override
    public  V put(K key, V value) {
        return super.put(key, value);
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        super.putAll(m);
    }

    @NotNull
    @UnmodifiableView
    @Override
    public Set<K> keySet() {
        return Collections.unmodifiableSet(delegate.keySet());
    }

    @Nullable
    @Override
    public V forcePut(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @UnmodifiableView
    @Override
    public Set<V> values() {
        return Collections.unmodifiableSet(delegate.values());
    }

    @UnmodifiableView
    @Override
    public BiMap<V, K> inverse() {
        return new BiMapMapDelegate<>(inverseMappingFunction, mappingFunction, delegate.inverse());
    }

    @NotNull
    @UnmodifiableView
    @Override
    public Set<Entry<K, V>> entrySet() {
        return delegate.entrySet().stream().map(kvEntry -> Map.entry(kvEntry.getKey(), kvEntry.getValue())).collect(Collectors.toUnmodifiableSet());
    }
}
