package dhyces.trimmed.api;

import dhyces.trimmed.api.maps.LimitedMap;
import dhyces.trimmed.api.maps.OptionalMapEntry;
import dhyces.trimmed.impl.TrimmedClientMapApiImpl;
import dhyces.trimmed.impl.client.maps.ClientMapKey;
import dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public interface TrimmedClientMapApi {
    TrimmedClientMapApi INSTANCE = new TrimmedClientMapApiImpl();

    @ApiStatus.Experimental
    LimitedMap<ResourceLocation, String> map(ClientMapKey clientMapKey);

    @ApiStatus.Experimental
    Stream<OptionalMapEntry<ResourceLocation, String>> mapStream(ClientMapKey clientMapKey);

    @ApiStatus.Experimental
    <K> LimitedMap<K, String> map(ClientRegistryMapKey<K> clientRegistryMapKey);

    @Nullable
    String getUncheckedClientValue(ClientMapKey clientMapKey, ResourceLocation key);

    @Nullable
    <K> String getRegistryClientValue(ClientRegistryMapKey<K> clientRegistryMapKey, K key);
}
