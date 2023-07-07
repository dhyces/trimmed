package dhyces.trimmed.api.maps;

import dhyces.trimmed.impl.client.maps.ApiLimitedMapImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface LimitedMap<K, V> extends Iterable<ImmutableEntry<K, V>> {

    V get(Object key);
    V getOrDefault(Object key, V defaultValue);
    V getOrSupply(Object key, Supplier<V> defaultSupplier);
    <T> T mapUnsafe(Object key, Function<V, T> mappingFunction);
    <T> Optional<T> getAndMap(Object key, Function<V, T> mappingFunction);

    Stream<ImmutableEntry<K, V>> stream();

    static <K, V> LimitedMap<K, V> adapter(OperableSupplier<Map<K, V>> backing) {
        return new ApiLimitedMapImpl<>(backing);
    }

    static <K, O, V> LimitedMap<K, V> mappingAdapter(Supplier<Map<K, O>> backing, Function<O, V> mappingFunc) {
        return new MappingAdapter<>(backing, mappingFunc);
    }

    final class MappingAdapter<K, O, V> implements LimitedMap<K, V> {
        private final Supplier<Map<K, O>> backing;
        private final Function<O, V> mappingFunction;

        MappingAdapter(Supplier<Map<K, O>> underlying, Function<O, V> mappingFunction) {
            this.backing = underlying;
            this.mappingFunction = mappingFunction;
        }

        @Override
        public V get(Object key) {
            return mappingFunction.apply(backing.get().get(key));
        }

        @Override
        public V getOrDefault(Object key, V defaultValue) {
            if (!backing.get().containsKey(key)) {
                return defaultValue;
            }
            return mappingFunction.apply(backing.get().get(key));
        }

        @Override
        public V getOrSupply(Object key, Supplier<V> defaultSupplier) {
            if (!backing.get().containsKey(key)) {
                return defaultSupplier.get();
            }
            return mappingFunction.apply(backing.get().get(key));
        }

        @Override
        public <T> T mapUnsafe(Object key, Function<V, T> mappingFunction) {
            return mappingFunction.apply(this.mappingFunction.apply(backing.get().get(key)));
        }

        @Override
        public <T> Optional<T> getAndMap(Object key, Function<V, T> mappingFunction) {
            return Optional.ofNullable(this.mappingFunction.apply(backing.get().get(key))).map(mappingFunction);
        }

        @Override
        public Stream<ImmutableEntry<K, V>> stream() {
            return backing.get().entrySet().stream().map(koEntry -> ImmutableEntry.basic(koEntry.getKey(), mappingFunction.apply(koEntry.getValue())));
        }

        @NotNull
        @Override
        public Iterator<ImmutableEntry<K, V>> iterator() {
            return backing.get().entrySet().stream()
                    .map(entry -> ImmutableEntry.basic(entry.getKey(), mappingFunction.apply(entry.getValue())))
                    .iterator();
        }
    }
}
