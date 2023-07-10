package dhyces.trimmed.api.maps;

public record OptionalMapEntry<K, V>(K key, V value, boolean isRequired) {
}
