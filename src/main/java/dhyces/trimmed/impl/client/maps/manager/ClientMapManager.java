package dhyces.trimmed.impl.client.maps.manager;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.data.maps.MapValue;
import dhyces.trimmed.api.data.maps.MapFile;
import dhyces.trimmed.api.util.ResourcePath;
import dhyces.trimmed.impl.util.UnresolvedMap;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.RegistryManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ClientMapManager implements PreparableReloadListener {
    private static final UncheckedMapHandler UNCHECKED_HANDLERS = new UncheckedMapHandler();
    private static final Map<ResourceKey<? extends Registry<?>>, RegistryMapHandler<?>> REGISTRY_HANDLERS = new HashMap<>();
    private static final Map<ResourceKey<? extends Registry<?>>, DatapackMapHandler<?>> DATAPACKED_HANDLERS = new HashMap<>();

    private static final FileToIdConverter FILE_TO_ID_CONVERTER = FileToIdConverter.json("maps");

    public static UncheckedMapHandler getUncheckedHandler() {
        if (!UNCHECKED_HANDLERS.hasLoaded()) {
            Trimmed.LOGGER.error("Client maps aren't loaded yet! May result in unexpected behavior");
        }
        return UNCHECKED_HANDLERS;
    }

    public static <T> Optional<RegistryMapHandler<T>> getRegistryHandler(ResourceKey<? extends Registry<T>> registryKey) {
        if (REGISTRY_HANDLERS.isEmpty()) {
            Trimmed.LOGGER.error("Client maps aren't loaded yet! May result in unexpected behavior");
        }
        return Optional.ofNullable(cast(REGISTRY_HANDLERS.get(registryKey)));
    }

    public static <T> Optional<DatapackMapHandler<T>> getDatapackedHandler(ResourceKey<? extends Registry<T>> registryKey) {
        if (DATAPACKED_HANDLERS.isEmpty()) {
            Trimmed.LOGGER.error("Client maps aren't loaded yet! May result in unexpected behavior");
        }
        return Optional.ofNullable(cast(DATAPACKED_HANDLERS.get(registryKey)));
    }

    public static void updateDatapacksSynced(RegistryAccess registryAccess) {
        DATAPACKED_HANDLERS.values().forEach(datapackMapHandler -> datapackMapHandler.update(registryAccess));
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier pPreparationBarrier, ResourceManager pResourceManager, ProfilerFiller pPreparationsProfiler, ProfilerFiller pReloadProfiler, Executor pBackgroundExecutor, Executor pGameExecutor) {
        return load(pResourceManager).thenCompose(pPreparationBarrier::wait).thenRun(() -> Trimmed.LOGGER.debug("Client maps loaded!"));
    }

    @SuppressWarnings("UnstableApiUsage")
    private <T> CompletableFuture<Unit> load(ResourceManager resourceManager) {
        UNCHECKED_HANDLERS.clear();
        REGISTRY_HANDLERS.clear();
        DATAPACKED_HANDLERS.clear();
        final UnresolvedMap<T, Set<Map.Entry<ResourceLocation, MapValue>>> readMaps = new UnresolvedMap<>();
        for (Map.Entry<ResourceLocation, List<Resource>> entry : FILE_TO_ID_CONVERTER.listMatchingResourceStacks(resourceManager).entrySet()) {
            ResourcePath idPath = new ResourcePath(entry.getKey());
            Map<ResourceLocation, MapValue> readMap = readResources(entry.getKey(), entry.getValue());

            String registryDirectoryPath = idPath.getDirectoryStringFrom("maps");
            String[] registryDirectories = registryDirectoryPath.split("/");
            ResourceKey<? extends Registry<T>> registryId;
            if (registryDirectories.length > 1 && ModList.get().isLoaded(registryDirectories[0])) {
                registryId = ResourceKey.createRegistryKey(new ResourceLocation(registryDirectories[0], idPath.getDirectoryStringFrom(registryDirectories[0]))); // TODO: test this
            } else {
                registryId = ResourceKey.createRegistryKey(new ResourceLocation(registryDirectoryPath));
            }
            ResourceLocation truncated = idPath.getFileNameOnly(5).asResourceLocation();
            readMaps.add(registryId, truncated, readMap.entrySet());
        }

        for (Map.Entry<ResourceKey<? extends Registry<T>>, Map<ResourceLocation, Set<Map.Entry<ResourceLocation, MapValue>>>> entry : readMaps) {
            ResourceKey<? extends Registry<T>> handlerKey = entry.getKey();
            Map<ResourceLocation, Set<Map.Entry<ResourceLocation, MapValue>>> unresolved = entry.getValue();

            if (handlerKey.location().getPath().equals("unchecked")) {
                UNCHECKED_HANDLERS.resolveMaps(unresolved);
            } else {
                final boolean isModded = !handlerKey.location().getNamespace().equals("minecraft");

                if (BuiltInRegistries.REGISTRY.get(handlerKey.location()) != null || (isModded && RegistryManager.ACTIVE.getRegistry(handlerKey) != null)) {
                    REGISTRY_HANDLERS.computeIfAbsent(handlerKey, resourceKey -> new RegistryMapHandler<>(handlerKey)).resolveMaps(unresolved);
                } else {
                    DATAPACKED_HANDLERS.computeIfAbsent(handlerKey, resourceKey -> new DatapackMapHandler<>(handlerKey)).resolveMaps(unresolved);
                }
            }
        }
        return CompletableFuture.completedFuture(Unit.INSTANCE);
    }

    private Map<ResourceLocation, MapValue> readResources(ResourceLocation fileName, List<Resource> resourceStack) {
        ImmutableMap.Builder<ResourceLocation, MapValue> mapBuilder = ImmutableMap.builder();
        for (Resource resource : resourceStack) {
            try (BufferedReader reader = resource.openAsReader()) {
                JsonObject json = GsonHelper.parse(reader);
                if (!CraftingHelper.processConditions(json, "conditions", ICondition.IContext.TAGS_INVALID)) {
                    Trimmed.LOGGER.debug("Skipping loading client map {} as it's conditions were not met", fileName);
                    continue;
                }
                Optional<MapFile> mapFileOptional = MapFile.CODEC.parse(JsonOps.INSTANCE, json).resultOrPartial(Trimmed.LOGGER::error);
                if (mapFileOptional.isPresent()) {
                    MapFile mapFile = mapFileOptional.get();
                    if (mapFile.shouldReplace()) {
                        mapBuilder = ImmutableMap.builder();
                    }
                    mapBuilder.putAll(mapFile.map());
                }
            } catch (IOException e) {
                throw new RuntimeException(e); // TODO
            }
        }
        return mapBuilder.build();
    }

    private static <T> T cast(Object o) {
        return (T) o;
    }
}
