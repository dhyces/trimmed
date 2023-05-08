package dhyces.trimmed.impl;

import dhyces.trimmed.api.TrimmedMapApi;
import dhyces.trimmed.impl.client.maps.ClientMapKey;
import dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class TrimmedMapApiImpl implements TrimmedMapApi {
    @Override
    public Map<ResourceLocation, String> getUncheckedMap(ClientMapKey clientMapKey) {
        return null;
    }

    @Override
    public <K> Map<K, String> getRegistryMap(ClientRegistryMapKey<K> clientRegistryMapKey) {
        return null;
    }

    @Override
    public <K> Map<K, String> getDatapackedMap(ClientRegistryMapKey<K> clientRegistryMapKey) {
        return null;
    }
}
