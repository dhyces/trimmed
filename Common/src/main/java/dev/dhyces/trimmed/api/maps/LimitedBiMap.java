package dev.dhyces.trimmed.api.maps;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface LimitedBiMap<K, V> extends LimitedMap<K, V> {
    LimitedBiMap<V, K> inverse();

    static <K, V> LimitedBiMap<K, V> biMapAdapter(Map<K, V> map, Predicate<K> requiredPredicate) {
        return new BiMapAdapter<>(map, requiredPredicate);
    }

    final class BiMapAdapter<K, V> implements LimitedBiMap<K, V> {

        private BiMap<K, V> backing;
        private final Predicate<K> requiredPredicate;
        private LimitedBiMap<V, K> inverse;

        BiMapAdapter(Map<K, V> backed, Predicate<K> requiredPredicate) {
            this(HashBiMap.create(backed), requiredPredicate);
        }

        BiMapAdapter(BiMap<K, V> backing, Predicate<K> requiredPredicate) {
            this.backing = backing;
            this.requiredPredicate = requiredPredicate;
        }

        @Override
        public LimitedBiMap<V, K> inverse() {
            if (inverse == null) {
                inverse = new Inverse();
            }
            return inverse;
        }

        @Override
        public V get(Object key) {
            return backing.get(key);
        }

        @Override
        public V getOrDefault(Object key, V defaultValue) {
            return backing.getOrDefault(key, defaultValue);
        }

        @Override
        public V getOrSupply(Object key, Supplier<V> defaultSupplier) {
            if (!backing.containsKey(key)) {
                return defaultSupplier.get();
            }
            return backing.get(key);
        }

        @Override
        public <T> T mapUnsafe(Object key, Function<V, T> mappingFunction) {
            return mappingFunction.apply(backing.get(key));
        }

        @Override
        public <T> Optional<T> getAndMap(Object key, Function<V, T> mappingFunction) {
            return Optional.ofNullable(backing.get(key)).map(mappingFunction);
        }

        @Override
        public Optional<V> getOptional(Object key) {
            return Optional.ofNullable(backing.get(key));
        }

        @Override
        public Stream<ImmutableEntry<K, V>> stream() {
            return backing.entrySet().stream().map(ImmutableEntry::from);
        }

        @Override
        public boolean isRequired(K key) {
            return requiredPredicate.test(key);
        }

        @NotNull
        @Override
        public Iterator<ImmutableEntry<K, V>> iterator() {
            return backing.entrySet().stream().map(ImmutableEntry::from).iterator();
        }

        @Override
        public void onUpdated(Map<K, V> map) {
            backing = HashBiMap.create(map);
        }

        final class Inverse implements LimitedBiMap<V, K> {

            private BiMapAdapter<K, V> forward() {
                return BiMapAdapter.this;
            }

            private BiMap<V, K> adaptedInverse() {
                return backing.inverse();
            }

            @Override
            public LimitedBiMap<K, V> inverse() {
                return forward();
            }

            @Override
            public K get(Object value) {
                return adaptedInverse().get(value);
            }

            @Override
            public K getOrDefault(Object value, K defaultValue) {
                return adaptedInverse().getOrDefault(value, defaultValue);
            }

            @Override
            public K getOrSupply(Object value, Supplier<K> defaultSupplier) {
                if (!adaptedInverse().containsKey(value)) {
                    return defaultSupplier.get();
                }
                return adaptedInverse().get(value);
            }

            @Override
            public <T> T mapUnsafe(Object value, Function<K, T> mappingFunction) {
                return mappingFunction.apply(adaptedInverse().get(value));
            }

            @Override
            public <T> Optional<T> getAndMap(Object value, Function<K, T> mappingFunction) {
                return Optional.ofNullable(adaptedInverse().get(value)).map(mappingFunction);
            }

            @Override
            public Optional<K> getOptional(Object value) {
                return Optional.ofNullable(adaptedInverse().get(value));
            }

            @Override
            public Stream<ImmutableEntry<V, K>> stream() {
                return adaptedInverse().entrySet().stream().map(ImmutableEntry::from);
            }

            @Override
            public boolean isRequired(V value) {
                if (adaptedInverse().containsKey(value)) {
                    return false;
                }
                return requiredPredicate.test(adaptedInverse().get(value));
            }

            @Override
            public void onUpdated(Map<V, K> map) {
            }

            @NotNull
            @Override
            public Iterator<ImmutableEntry<V, K>> iterator() {
                return adaptedInverse().entrySet().stream().map(ImmutableEntry::from).iterator();
            }
        }
    }
}
