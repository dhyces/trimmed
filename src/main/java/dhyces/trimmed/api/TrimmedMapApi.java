package dhyces.trimmed.api;

import dhyces.trimmed.impl.client.maps.ClientMapKey;
import dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;
import dhyces.trimmed.impl.util.OptionalId;
import net.minecraft.core.Holder;

import java.util.Map;
import java.util.Optional;

public interface TrimmedMapApi {
    Map<OptionalId, String> getUncheckedClientMap(ClientMapKey clientMapKey);

    <K> Map<K, String> getRegistryClientMap(ClientRegistryMapKey<K> clientRegistryMapKey);

    <K> Map<Holder<K>, String> getDatapackedClientMap(ClientRegistryMapKey<K> clientRegistryMapKey);

    Optional<Map<OptionalId, String>> getSafeUncheckedClientMap(ClientMapKey clientMapKey);

    <K> Optional<Map<K, String>> getSafeRegistryClientMap(ClientRegistryMapKey<K> clientRegistryMapKey);

    <K> Optional<Map<Holder<K>, String>> getSafeDatapackedClientMap(ClientRegistryMapKey<K> clientRegistryMapKey);
}
