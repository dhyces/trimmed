package dhyces.trimmed.impl.client.maps.manager;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.DataResult;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.data.maps.MapEntry;
import dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.Function;

public class DatapackMapHandler<T> extends BaseMapHandler<ClientRegistryMapKey<T>, Holder<T>> {

    private final Map<ClientRegistryMapKey<T>, Map<ResourceLocation, String>> intermediate = new HashMap<>();
    private RegistryAccess registryAccess;
    private final ResourceKey<? extends Registry<T>> registryKey;

    public DatapackMapHandler(ResourceKey<? extends Registry<T>> registryKey) {
        this.registryKey = registryKey;
    }

    @Override
    protected ClientRegistryMapKey<T> createMapKey(ResourceLocation mapId) {
        return ClientRegistryMapKey.of(registryKey, mapId);
    }

    @Override
    protected Holder<T> createKey(ResourceLocation keyId) {
        return registryAccess.registry(registryKey).flatMap(registry -> registry.getHolder(ResourceKey.create(registryKey, keyId))).orElse(null);
    }

    @Override
    void resolveMaps(Map<ResourceLocation, Map<ResourceLocation, MapEntry>> unresolvedMaps) {
        for (Map.Entry<ResourceLocation, Map<ResourceLocation, MapEntry>> entry : unresolvedMaps.entrySet()) {
            DataResult<Map<ResourceLocation, String>> dataResult = resolveMap(unresolvedMaps, intermediate, entry.getKey(), this::createMapKey, Function.identity(), new LinkedHashSet<>());
            dataResult.error().ifPresent(mapPartialResult -> Trimmed.LOGGER.error(mapPartialResult.message()));
        }
    }

    void update(RegistryAccess registryAccess) {
        this.registryAccess = registryAccess;
        clear();
        Optional<Registry<T>> datapackRegistryOptional = registryAccess.registry(registryKey);
        if (datapackRegistryOptional.isEmpty()) {
            Trimmed.LOGGER.error("Datapack registry " + registryKey.location() + " does not exist or is not synced to client!");
            return;
        }
        for (Map.Entry<ClientRegistryMapKey<T>, Map<ResourceLocation, String>> mapEntry : intermediate.entrySet()) {
            try {
                ImmutableMap.Builder<Holder<T>, String> mapBuilder = ImmutableMap.builder();
                for (Map.Entry<ResourceLocation, String> entry : mapEntry.getValue().entrySet()) {
                    Holder<T> holder = createKey(entry.getKey());
                    if (holder == null) {
                        throw new RuntimeException("Datapack element %s does not exist! Failed to load %s".formatted(entry.getKey(), mapEntry.getKey()));
                    }
                    mapBuilder.put(holder, entry.getValue());
                }
                registeredMaps.put(mapEntry.getKey(), mapBuilder.build());
            } catch (RuntimeException e) {
                Trimmed.LOGGER.error(e.getMessage());
            }
        }
        isLoaded = true;
    }
}
