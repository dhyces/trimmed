package dhyces.trimmed.api;

import dhyces.trimmed.impl.client.maps.ClientMapKey;
import dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public interface TrimmedMapApi {
    Map<ResourceLocation, String> getUncheckedMap(ClientMapKey clientMapKey);

    <K> Map<K, String> getRegistryMap(ClientRegistryMapKey<K> clientRegistryMapKey);

    <K> Map<K, String> getDatapackedMap(ClientRegistryMapKey<K> clientRegistryMapKey);
}
