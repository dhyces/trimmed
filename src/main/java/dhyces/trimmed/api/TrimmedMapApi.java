package dhyces.trimmed.api;

import dhyces.trimmed.impl.client.maps.ClientMapKey;
import dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Optional;

public interface TrimmedMapApi {
    Map<ResourceLocation, String> getUncheckedClientMap(ClientMapKey clientMapKey);

    <K> Map<K, String> getRegistryClientMap(ClientRegistryMapKey<K> clientRegistryMapKey);

    <K> Map<Holder<K>, String> getDatapackedClientMap(ClientRegistryMapKey<K> clientRegistryMapKey);

    Optional<Map<ResourceLocation, String>> getSafeUncheckedClientMap(ClientMapKey clientMapKey);

    <K> Optional<Map<K, String>> getSafeRegistryClientMap(ClientRegistryMapKey<K> clientRegistryMapKey);

    <K> Optional<Map<Holder<K>, String>> getSafeDatapackedClientMap(ClientRegistryMapKey<K> clientRegistryMapKey);
}
