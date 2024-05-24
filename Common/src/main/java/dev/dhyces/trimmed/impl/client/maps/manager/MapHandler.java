package dev.dhyces.trimmed.impl.client.maps.manager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.dhyces.trimmed.Trimmed;
import dev.dhyces.trimmed.api.data.maps.MapAppendElement;
import dev.dhyces.trimmed.api.data.maps.MapFile;
import dev.dhyces.trimmed.api.maps.MapHolder;
import dev.dhyces.trimmed.api.maps.types.MapType;
import dev.dhyces.trimmed.impl.client.maps.MapKey;
import dev.dhyces.trimmed.modhelper.services.Services;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.DependencySorter;
import net.minecraft.util.GsonHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class MapHandler<K, V> {
    private final MapKey<K, V> baseKey;
    private final Map<MapKey<K, V>, ClientMapHolder<K, V, Map<K, V>>> map;

    public MapHandler(MapKey<K, V> baseKey) {
        this.baseKey = baseKey;
        this.map = new Reference2ObjectOpenHashMap<>();
    }

    MapHolder<K, V> getHolder(MapKey<K, V> mapKey) {
        return map.get(mapKey);
    }

    public void clear() {
        map.values().forEach(kvMapClientMapHolder -> {
            kvMapClientMapHolder.backing = null;
            kvMapClientMapHolder.optionalKeys = null;
        });
    }

    private ClientMapHolder<K, V, Map<K, V>> getOrCreateHolder(MapKey<K, V> key) {
        return map.computeIfAbsent(key, ClientMapHolder::new);
    }

    void parse(ResourceLocation resolverPath, FileToIdConverter converter, ResourceManager resourceManager) {
        MapFile<K, V> base = readStack(baseKey.getMapId(), resourceManager.getResourceStack(resolverPath.withSuffix(".json")));
        Map<ResourceLocation, MapFile<K, V>> children = readResources(converter, resourceManager);
        if (base.map().isEmpty() && base.appendElements().isEmpty() && children.isEmpty()) {
            throw new IllegalStateException("No maps to read, skipping %s".formatted(resolverPath));
        }

        DependencySorter<ResourceLocation, Entry<K, V>> dependencySorter = new DependencySorter<>();
        dependencySorter.addEntry(baseKey.getMapId(), new Entry<>(base));
        children.forEach((resourceLocation, vMapFile) -> dependencySorter.addEntry(resourceLocation, new Entry<>(vMapFile)));
        dependencySorter.orderByDependencies((resourceLocation, vEntry) -> {
            ClientMapHolder<K, V, Map<K, V>> holder = getOrCreateHolder(MapKey.of(baseKey.getType(), resourceLocation));
            Set<K> optionalElements = new HashSet<>();
            Map<K, V> finishedMap = vEntry.file().map().entrySet().stream()
                    .peek(kvEntry -> {
                        if (!kvEntry.getValue().isRequired()) {
                            optionalElements.add(kvEntry.getKey());
                        }
                    })
                    .map(kMapValueEntry -> Map.entry(kMapValueEntry.getKey(), kMapValueEntry.getValue().value()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v, v2) -> v, baseKey.getType()::createMap));
            for (MapAppendElement element : vEntry.file().appendElements()) {
                Map<K, V> map = getOrCreateHolder(MapKey.of(baseKey.getType(), element.mapId())).getMap();
                if (map != null) {
                    finishedMap.putAll(map);
                }
            }
            holder.backing = finishedMap;
            holder.optionalKeys = optionalElements;
        });
    }

    private Map<ResourceLocation, MapFile<K, V>> readResources(FileToIdConverter converter, ResourceManager resourceManager) {
        return converter.listMatchingResourceStacks(resourceManager).entrySet().stream()
                .map(entry -> {
                    ResourceLocation id = converter.fileToId(entry.getKey());
                    return Map.entry(id, readStack(id, entry.getValue()));
                }).collect(Util.toMap());
    }

    private MapFile<K, V> readStack(ResourceLocation fileName, List<Resource> resourceStack) {
        MapType<K, V> mapType = baseKey.getType();
        MapFile.Builder<K, V> builder = new MapFile.Builder<>();
        for (Resource resource : resourceStack) {
            try (BufferedReader reader = resource.openAsReader()) {
                JsonObject json = GsonHelper.parse(reader);
                Optional<MapFile<K, V>> result = Services.PLATFORM_HELPER.decodeWithConditions(MapFile.codec(mapType.getKeyResolver().getCodec(), mapType.getValueCodec()), json);
                if (result.isEmpty()) {
                    Trimmed.LOGGER.debug("Skipping loading client map {} as its conditions were not met", fileName);
                    continue;
                }
                MapFile<K, V> mapFile = result.get();
                if (mapFile.shouldReplace()) {
                    builder = new MapFile.Builder<>();
                }
                builder.merge(mapFile);
            } catch (JsonParseException | IOException e) {
                throw new RuntimeException("Failed to read %s from %s: ".formatted(fileName, resource.source().packId()), e);
            }
        }
        return builder.build();
    }

    private record Entry<K, V>(MapFile<K, V> file) implements DependencySorter.Entry<ResourceLocation> {
        @Override
        public void visitRequiredDependencies(Consumer<ResourceLocation> visitor) {
            file.appendElements().forEach(mapAppendElement -> {
                if (mapAppendElement.isRequired()) {
                    visitor.accept(mapAppendElement.mapId());
                }
            });
        }

        @Override
        public void visitOptionalDependencies(Consumer<ResourceLocation> visitor) {
            file.appendElements().forEach(mapAppendElement -> {
                if (!mapAppendElement.isRequired()) {
                    visitor.accept(mapAppendElement.mapId());
                }
            });
        }
    }

    static class ClientMapHolder<K, V, M extends Map<K, V>> implements MapHolder.Typed<K, V, M> {
        private final MapKey<K, V> key;
        M backing;
        Set<K> optionalKeys;

        public ClientMapHolder(MapKey<K, V> key) {
            this.key = key;
        }

        @Override
        public MapKey<K, V> getKey() {
            return key;
        }

        @Override
        public M getMap() {
            return backing;
        }

        @Override
        public boolean isRequired(K key) {
            return optionalKeys.contains(key);
        }
    }
}
