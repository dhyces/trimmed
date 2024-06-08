package dev.dhyces.trimmed.impl.client.maps.manager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import dev.dhyces.trimmed.Trimmed;
import dev.dhyces.trimmed.api.data.map.MapAppendElement;
import dev.dhyces.trimmed.api.data.map.MapBuilder;
import dev.dhyces.trimmed.api.data.map.MapFile;
import dev.dhyces.trimmed.api.maps.MapHolder;
import dev.dhyces.trimmed.api.maps.types.MapType;
import dev.dhyces.trimmed.api.maps.MapKey;
import dev.dhyces.trimmed.modhelper.services.Services;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
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
    private final Map<MapKey<K, V>, ClientMapHolder<K, V, Map<K, V>>> holders;

    public MapHandler(MapKey<K, V> baseKey) {
        this.baseKey = baseKey;
        this.holders = new Reference2ObjectOpenHashMap<>();
    }

    MapHolder<K, V> getHolder(MapKey<K, V> mapKey) {
        return getOrCreateHolder(mapKey);
    }

    void clear() {
        holders.values().forEach(kvMapClientMapHolder -> {
            kvMapClientMapHolder.backing = null;
            kvMapClientMapHolder.optionalKeys = null;
        });
    }

    private ClientMapHolder<K, V, Map<K, V>> getOrCreateHolder(MapKey<K, V> key) {
        return holders.computeIfAbsent(key, ClientMapHolder::new);
    }

    void parse(ResourceLocation resolverPath, FileToIdConverter converter, ResourceManager resourceManager) {
        MapFile<K, V> base = readStack(baseKey.getMapId(), resourceManager.getResourceStack(resolverPath.withSuffix(".json")));
        // TODO: look back into this, I believe this will try to parse from any pack, not just the one with this namespace
        Map<ResourceLocation, MapFile<K, V>> children = readResources(converter, resourceManager);
        if (base.map().isEmpty() && base.appendElements().isEmpty() && children.isEmpty()) {
            Trimmed.LOGGER.debug("No maps to read, skipping %s".formatted(resolverPath));
            return;
        }

        DependencySorter<ResourceLocation, Entry<K, V>> dependencySorter = new DependencySorter<>();
        dependencySorter.addEntry(baseKey.getMapId(), new Entry<>(base));
        children.forEach((resourceLocation, vMapFile) -> dependencySorter.addEntry(resourceLocation, new Entry<>(vMapFile)));
        dependencySorter.orderByDependencies((resourceLocation, vEntry) -> {
            MapKey<K, V> key = baseKey.getMapId().equals(resourceLocation) ? baseKey : MapKey.fromBase(baseKey, resourceLocation.withPath(s -> s.substring(s.indexOf('/')+1)));
            ClientMapHolder<K, V, Map<K, V>> holder = getOrCreateHolder(key);
            Set<K> optionalElements = new ObjectOpenHashSet<>();
            Map<K, V> finishedMap = vEntry.file().map().entrySet().stream()
                    .peek(kvEntry -> {
                        if (!kvEntry.getValue().isRequired()) {
                            optionalElements.add(kvEntry.getKey());
                        }
                    })
                    .map(kMapValueEntry -> Map.entry(kMapValueEntry.getKey(), kMapValueEntry.getValue().value()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v, v2) -> v, baseKey.getType()::createMap));
            for (MapAppendElement element : vEntry.file().appendElements()) {
                MapKey<K, V> subKey = MapKey.fromBase(baseKey, element.mapId());
                MapHolder<K, V> mapHolder = getOrCreateHolder(subKey);
                if (mapHolder.isBound()) {
                    finishedMap.putAll(mapHolder.getMap());
                } else {
                    if (element.isRequired()) {
                        throw new IllegalStateException("Required entry \"%s\" cannot be found for \"%s\"".formatted(element.mapId(), resourceLocation));
                    }
                }
            }
            holder.update(finishedMap, optionalElements);
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
        MapBuilder<K, V> builder = new MapBuilder<>();
        for (Resource resource : resourceStack) {
            try (BufferedReader reader = resource.openAsReader()) {
                JsonObject json = GsonHelper.parse(reader);
                Optional<MapFile<K, V>> result = Services.PLATFORM_HELPER.decodeWithConditions(MapFile.codec(mapType.getKeyResolver().getCodec(), mapType.getValueCodec()), JsonOps.INSTANCE, json);
                if (result.isEmpty()) {
                    Trimmed.LOGGER.debug("Skipping loading client map {} as its conditions were not met", fileName);
                    continue;
                }
                MapFile<K, V> mapFile = result.get();
                if (mapFile.shouldReplace()) {
                    builder = new MapBuilder<>();
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

        private void update(M backing, Set<K> optionalKeys) {
            this.backing = backing;
            this.optionalKeys = optionalKeys;
        }

        @Override
        public MapKey<K, V> unwrapKey() {
            return key;
        }

        @Override
        public M getMap() {
            if (backing == null) {
                throw new IllegalStateException("Cannot access map for " + key + " because it doesn't exist!");
            }
            return backing;
        }

        @Override
        public boolean isRequired(K key) {
            return !optionalKeys.contains(key);
        }

        @Override
        public boolean isBound() {
            return backing != null;
        }
    }
}
