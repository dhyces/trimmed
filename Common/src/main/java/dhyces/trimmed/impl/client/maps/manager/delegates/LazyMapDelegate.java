package dhyces.trimmed.impl.client.maps.manager.delegates;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.DataResult;
import dhyces.trimmed.Trimmed;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.function.Function;

public class LazyMapDelegate<K, V> extends BaseMapDelegate<K, V> {

    private Map<K, String> reference;

    public LazyMapDelegate(Function<String, DataResult<V>> mappingFunction) {
        super(mappingFunction);
    }

    @ApiStatus.Internal
    @Override
    public void onReload(Map<K, String> underlyingMap) {
        this.reference = underlyingMap;
        super.onReload(underlyingMap);
    }

    @Override
    protected void onMapped(K key, V mappedValue) {
        // NO-OP
    }

    @Override
    public int size() {
        return reference.size();
    }

    @Override
    public boolean isEmpty() {
        return reference.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return reference.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return reference.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return map(reference.get(key)).resultOrPartial(Trimmed.LOGGER::error).orElse(null);
    }

    @NotNull
    @UnmodifiableView
    @Override
    public Set<K> keySet() {
        return Collections.unmodifiableSet(reference.keySet());
    }

    @NotNull
    @UnmodifiableView
    @Override
    public Collection<V> values() {
        ImmutableList.Builder<V> builder = ImmutableList.builder();
        for (String str : reference.values()) {
            DataResult<V> result = map(str);
            if (result.error().isPresent()) {
                Trimmed.LOGGER.error("Delegate failed to map value {%s}. ".formatted(str) + result.error().get().message());
                return Collections.emptyList();
            }
            builder.add(result.result().get());
        }
        return builder.build();
    }

    @NotNull
    @UnmodifiableView
    @Override
    public Set<Entry<K, V>> entrySet() {
        ImmutableSet.Builder<Entry<K, V>> builder = ImmutableSet.builder();
        for (Entry<K, String> entry : reference.entrySet()) {
            DataResult<V> result = map(entry.getValue());
            if (result.error().isPresent()) {
                Trimmed.LOGGER.error("Delegate failed to map value {%s}. ".formatted(entry.getValue()) + result.error().get().message());
                return Collections.emptySet();
            }
            builder.add(Map.entry(entry.getKey(), result.result().get()));
        }
        return builder.build();
    }
}
