package dev.dhyces.trimmed.api.maps;

public interface BiMapAccess<K, V> extends MapAccess<K, V> {
    BiMapAccess<V, K> inverse();
}
