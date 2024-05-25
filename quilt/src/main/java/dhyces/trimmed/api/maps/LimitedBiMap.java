package dhyces.trimmed.api.maps;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface LimitedBiMap<K, V> extends LimitedMap<K, V> {
    LimitedBiMap<V, K> inverse();

    static <K, V> LimitedBiMap<K, V> biMapAdapter(Map<K, V> map) {
        return new BiMapAdapter<>(map);
    }

    final class BiMapAdapter<K, V> implements LimitedBiMap<K, V> {

        private final BiMap<K, V> backing;
        private LimitedBiMap<V, K> inverse;

        BiMapAdapter(Map<K, V> backed) {
            this(HashBiMap.create(backed));
        }

        BiMapAdapter(BiMap<K, V> backing) {
            this.backing = backing;
        }

        @Override
        public LimitedBiMap<V, K> inverse() {
            if (inverse == null) {
                inverse = new BiMapAdapter<>(backing.inverse());
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

        @NotNull
        @Override
        public Iterator<ImmutableEntry<K, V>> iterator() {
            return backing.entrySet().stream().map(ImmutableEntry::from).iterator();
        }
    }
}
