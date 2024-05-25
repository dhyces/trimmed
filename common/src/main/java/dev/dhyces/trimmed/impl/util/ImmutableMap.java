package dev.dhyces.trimmed.impl.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface ImmutableMap<K, V> extends Map<K, V> {

    @Nullable
    @Override
    default V put(K key, V value) {
        throw new UnsupportedOperationException("Map is immutable");
    }

    @Override
    default void putAll(@NotNull Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("Map is immutable");
    }

    @Override
    default void clear() {
        throw new UnsupportedOperationException("Map is immutable");
    }

    @Override
    default V remove(Object key) {
        throw new UnsupportedOperationException("Map is immutable");
    }

    @NotNull
    @UnmodifiableView
    @Override
    Set<K> keySet();

    @NotNull
    @UnmodifiableView
    @Override
    Collection<V> values();

    @NotNull
    @UnmodifiableView
    @Override
    Set<Entry<K, V>> entrySet();
}
