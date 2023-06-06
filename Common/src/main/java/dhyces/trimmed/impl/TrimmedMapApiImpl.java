package dhyces.trimmed.impl;

import dhyces.trimmed.api.TrimmedMapApi;
import dhyces.trimmed.impl.client.maps.ClientMapKey;
import dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;
import dhyces.trimmed.impl.client.maps.manager.ClientMapManager;
import dhyces.trimmed.impl.util.OptionalId;
import net.minecraft.core.Holder;

import java.util.Map;
import java.util.Optional;

public final class TrimmedMapApiImpl implements TrimmedMapApi {
    @Override
    public Map<OptionalId, String> getUncheckedClientMap(ClientMapKey clientMapKey) {
        return ClientMapManager.getUncheckedHandler().getMap(clientMapKey);
    }

    @Override
    public <K> Map<K, String> getRegistryClientMap(ClientRegistryMapKey<K> clientRegistryMapKey) {
        return ClientMapManager.getRegistryHandler(clientRegistryMapKey.getRegistryKey()).getMap(clientRegistryMapKey);
    }

    @Override
    public <K> Map<Holder<K>, String> getDatapackedClientMap(ClientRegistryMapKey<K> clientRegistryMapKey) {
        return ClientMapManager.getDatapackedHandler(clientRegistryMapKey.getRegistryKey()).getMap(clientRegistryMapKey);
    }

    @Override
    public <K> String getRegistryClientValue(ClientRegistryMapKey<K> clientRegistryMapKey, K key) {
        return getRegistryClientMap(clientRegistryMapKey).get(key);
    }

    @Override
    public <K> String getDatapackedClientValue(ClientRegistryMapKey<K> clientRegistryMapKey, Holder<K> key) {
        return getDatapackedClientMap(clientRegistryMapKey).get(key);
    }

    @Override
    public Optional<Map<OptionalId, String>> getSafeUncheckedClientMap(ClientMapKey clientMapKey) {
        return Optional.ofNullable(getUncheckedClientMap(clientMapKey));
    }

    @Override
    public <K> Optional<Map<K, String>> getSafeRegistryClientMap(ClientRegistryMapKey<K> clientRegistryMapKey) {
        return Optional.ofNullable(getRegistryClientMap(clientRegistryMapKey));
    }

    @Override
    public <K> Optional<Map<Holder<K>, String>> getSafeDatapackedClientMap(ClientRegistryMapKey<K> clientRegistryMapKey) {
        return Optional.ofNullable(getDatapackedClientMap(clientRegistryMapKey));
    }
}
