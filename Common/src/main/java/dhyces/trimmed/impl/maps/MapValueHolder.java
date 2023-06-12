package dhyces.trimmed.impl.maps;

import dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;

public interface MapValueHolder<T> {
    boolean hasValue();
    <V> V getValue(ClientRegistryMapKey<T> mapKey);
}
