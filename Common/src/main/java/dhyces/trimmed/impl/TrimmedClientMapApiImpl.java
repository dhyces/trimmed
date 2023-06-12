package dhyces.trimmed.impl;

import dhyces.trimmed.api.TrimmedClientMapApi;
import dhyces.trimmed.api.data.maps.MapValue;
import dhyces.trimmed.api.maps.LimitedMap;
import dhyces.trimmed.impl.client.maps.ClientMapKey;
import dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;
import dhyces.trimmed.impl.client.maps.manager.ClientMapManager;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public final class TrimmedClientMapApiImpl implements TrimmedClientMapApi {
    @Override
    public LimitedMap<ResourceLocation, MapValue> map(ClientMapKey clientMapKey) {
        return ClientMapManager.getUncheckedHandler().getMap(clientMapKey);
    }

    @Override
    public <K> LimitedMap<K, String> map(ClientRegistryMapKey<K> clientRegistryMapKey) {
        return ClientMapManager.getRegistryHandler(clientRegistryMapKey.getRegistryKey()).getMap(clientRegistryMapKey);
    }

    @Override
    public <K> String getRegistryClientValue(ClientRegistryMapKey<K> clientRegistryMapKey, K key) {
        return map(clientRegistryMapKey).get(key);
    }
}
