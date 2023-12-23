package dev.dhyces.trimmed.api.maps;

import java.util.Map;

public interface ImmutableEntry<K, V> {
    K getKey();
    V getValue();

    static <K, V> ImmutableEntry<K, V> basic(K key, V value) {
        return new Impl<>(key, value);
    }

    static <K, V> ImmutableEntry<K, V> from(Map.Entry<K, V> entry) {
        return new Impl<>(entry.getKey(), entry.getValue());
    }

    final class Impl<K, V> implements ImmutableEntry<K, V> {
        private final K key;
        private final V value;

        Impl(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }
    }
}
