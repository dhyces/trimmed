package dev.dhyces.trimmed.api.maps;

import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

@ApiStatus.Experimental
public interface MapAccess<K, V> extends Iterable<ImmutableEntry<K, V>>, MapObserver<K, V> {
    V get(Object key);
    V getOrDefault(Object key, V defaultValue);
    V getOrElse(Object key, Supplier<V> defaultSupplier);
    Optional<V> getOptional(Object key);
    Stream<ImmutableEntry<K, V>> stream();
    boolean isRequired(K key);
}
