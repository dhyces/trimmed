package dhyces.trimmed.api;

import dhyces.trimmed.api.data.maps.MapValue;
import dhyces.trimmed.api.maps.LimitedMap;
import dhyces.trimmed.impl.TrimmedClientMapApiImpl;
import dhyces.trimmed.impl.client.maps.ClientMapKey;
import dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public interface TrimmedClientMapApi {
    TrimmedClientMapApi INSTANCE = new TrimmedClientMapApiImpl();
    LimitedMap<ResourceLocation, MapValue> getUncheckedClientMap(ClientMapKey clientMapKey);

    <K> LimitedMap<K, String> getRegistryClientMap(ClientRegistryMapKey<K> clientRegistryMapKey);

    <K> String getRegistryClientValue(ClientRegistryMapKey<K> clientRegistryMapKey, K key);

    Optional<LimitedMap<ResourceLocation, MapValue>> getSafeUncheckedClientMap(ClientMapKey clientMapKey);

    <K> Optional<LimitedMap<K, String>> getSafeRegistryClientMap(ClientRegistryMapKey<K> clientRegistryMapKey);
}
