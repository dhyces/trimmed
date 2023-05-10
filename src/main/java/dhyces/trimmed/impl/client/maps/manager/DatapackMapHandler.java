package dhyces.trimmed.impl.client.maps.manager;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.DataResult;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.data.maps.MapValue;
import dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;
import dhyces.trimmed.impl.util.OptionalId;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class DatapackMapHandler<T> extends BaseMapHandler<ClientRegistryMapKey<T>, Holder<T>> {

    private final Map<ClientRegistryMapKey<T>, Map<OptionalId, String>> intermediate = new HashMap<>();
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
    protected Holder<T> createKey(ResourceLocation key, MapValue value) {
        return registryAccess.registry(registryKey).flatMap(registry -> registry.getHolder(ResourceKey.create(registryKey, key))).orElse(null);
    }

    @Override
    void resolveMaps(Map<ResourceLocation, Set<Map.Entry<ResourceLocation, MapValue>>> unresolvedMaps) {
        for (Map.Entry<ResourceLocation, Set<Map.Entry<ResourceLocation, MapValue>>> entry : unresolvedMaps.entrySet()) {
            DataResult<Map<OptionalId, String>> dataResult = resolveMap(unresolvedMaps, intermediate, entry.getKey(), this::createMapKey, (key, value) -> new OptionalId(key, value.isRequired()), new LinkedHashSet<>());
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
        for (Map.Entry<ClientRegistryMapKey<T>, Map<OptionalId, String>> mapEntry : intermediate.entrySet()) {
            try {
                ImmutableMap.Builder<Holder<T>, String> mapBuilder = ImmutableMap.builder();
                for (Map.Entry<OptionalId, String> entry : mapEntry.getValue().entrySet()) {
                    Holder<T> holder = registryAccess.registry(registryKey)
                            .flatMap(registry -> registry.getHolder(ResourceKey.create(registryKey, entry.getKey().elementId())))
                            .orElse(null);
                    if (holder != null) {
                        mapBuilder.put(holder, entry.getValue());
                    } else if (entry.getKey().isRequired()) {
                        throw new RuntimeException("Datapack element %s does not exist! Failed to load %s".formatted(entry.getKey(), mapEntry.getKey()));
                    }
                }
                registeredMaps.put(mapEntry.getKey(), mapBuilder.build());
            } catch (RuntimeException e) {
                Trimmed.LOGGER.error(e.getMessage());
            }
        }
        isLoaded = true;
    }
}
