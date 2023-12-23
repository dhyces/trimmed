package dev.dhyces.trimmed.impl.client.maps;

import dev.dhyces.trimmed.api.maps.ImmutableEntry;
import dev.dhyces.trimmed.api.maps.LimitedMap;
import dev.dhyces.trimmed.api.maps.OperableSupplier;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ApiLimitedMapImpl<K, V> implements LimitedMap<K, V> {
    private final OperableSupplier<Map<K, V>> backing;
    private final Predicate<K> requiredPredicate;

    public ApiLimitedMapImpl(OperableSupplier<Map<K, V>> underlying, Predicate<K> requiredPredicate) {
        this.backing = underlying;
        this.requiredPredicate = requiredPredicate;
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

    @Override
    public Optional<V> getOptional(Object key) {
        return Optional.ofNullable(get(key));
    }

    @Override
    public Stream<ImmutableEntry<K, V>> stream() {
        return backing.mapOrElse(kvMap -> kvMap.entrySet().stream().map(ImmutableEntry::from), Stream.empty());
    }

    @Override
    public boolean isRequired(K key) {
        return requiredPredicate.test(key);
    }

    @NotNull
    @Override
    public Iterator<ImmutableEntry<K, V>> iterator() {
        return backing.mapOrElse(map -> map.entrySet().stream().map(ImmutableEntry::from).iterator(), Stream.<ImmutableEntry<K, V>>empty().iterator());
    }

    @Override
    public void onUpdated(Map<K, V> map) {}
}
