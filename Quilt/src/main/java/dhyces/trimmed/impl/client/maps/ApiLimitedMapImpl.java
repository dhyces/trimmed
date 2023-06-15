package dhyces.trimmed.impl.client.maps;

import dhyces.trimmed.api.maps.ImmutableEntry;
import dhyces.trimmed.api.maps.LimitedMap;
import dhyces.trimmed.api.maps.OperableSupplier;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ApiLimitedMapImpl<K, V> implements LimitedMap<K, V> {
    private final OperableSupplier<Map<K, V>> backing;

    public ApiLimitedMapImpl(OperableSupplier<Map<K, V>> underlying) {
        this.backing = underlying;
    }

    @Override
    public V get(Object key) {
        return backing.mapOrElse(map -> map.get(key), null);
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return backing.mapOrElse(map -> map.getOrDefault(key, defaultValue), defaultValue);
    }

    @Override
    public V getOrSupply(Object key, Supplier<V> defaultSupplier) {
        if (!backing.mapOrElse(map -> map.containsKey(key), false)) {
            return defaultSupplier.get();
        }
        return get(key);
    }

    @Override
    public <T> T mapUnsafe(Object key, Function<V, T> mappingFunction) {
        return mappingFunction.apply(get(key));
    }

    @Override
    public <T> Optional<T> getAndMap(Object key, Function<V, T> mappingFunction) {
        return Optional.ofNullable(get(key)).map(mappingFunction);
    }

    @NotNull
    @Override
    public Iterator<ImmutableEntry<K, V>> iterator() {
        return backing.mapOrElse(map -> map.entrySet().stream().map(ImmutableEntry::from).iterator(), Stream.<ImmutableEntry<K, V>>empty().iterator());
    }
}
