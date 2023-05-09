package dhyces.trimmed.impl;

import dhyces.trimmed.api.TrimmedMapApi;
import dhyces.trimmed.impl.client.maps.ClientMapKey;
import dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;
import dhyces.trimmed.impl.client.maps.manager.ClientMapManager;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Optional;

public class TrimmedMapApiImpl implements TrimmedMapApi {
    @Override
    public Map<ResourceLocation, String> getUncheckedClientMap(ClientMapKey clientMapKey) {
        return ClientMapManager.getUncheckedHandler().getMap(clientMapKey);
    }

    @Override
    public <K> Map<K, String> getRegistryClientMap(ClientRegistryMapKey<K> clientRegistryMapKey) {
        return ClientMapManager.getRegistryHandler(clientRegistryMapKey.getRegistryKey()).map(handler -> handler.getMap(clientRegistryMapKey)).orElse(null);
    }

    @Override
    public <K> Map<Holder<K>, String> getDatapackedClientMap(ClientRegistryMapKey<K> clientRegistryMapKey) {
        return ClientMapManager.getDatapackedHandler(clientRegistryMapKey.getRegistryKey()).map(handler -> handler.getMap(clientRegistryMapKey)).orElse(null);
    }

    @Override
    public Optional<Map<ResourceLocation, String>> getSafeUncheckedClientMap(ClientMapKey clientMapKey) {
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
