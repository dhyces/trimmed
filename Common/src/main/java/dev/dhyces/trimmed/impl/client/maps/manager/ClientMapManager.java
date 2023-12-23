package dev.dhyces.trimmed.impl.client.maps.manager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import dev.dhyces.trimmed.Trimmed;
import dev.dhyces.trimmed.api.client.util.ClientUtil;
import dev.dhyces.trimmed.api.data.maps.MapFile;
import dev.dhyces.trimmed.api.util.Utils;
import dev.dhyces.trimmed.impl.resources.PathInfo;
import dev.dhyces.trimmed.impl.resources.RegistryPathInfo;
import dev.dhyces.trimmed.modhelper.services.Services;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ClientMapManager implements PreparableReloadListener {
    private static final UncheckedMapHandler UNCHECKED_HANDLER = new UncheckedMapHandler();
    private static final Map<ResourceKey<? extends Registry<?>>, RegistryMapHandler<?>> REGISTRY_HANDLERS = new HashMap<>();

    private static final FileToIdConverter FILE_TO_ID_CONVERTER = FileToIdConverter.json("maps");

    public static UncheckedMapHandler getUncheckedHandler() {
//        if (!UNCHECKED_HANDLERS.hasLoaded()) {
//            Trimmed.LOGGER.error("Client maps aren't loaded yet! May result in unexpected behavior");
//        }
        return UNCHECKED_HANDLER;
    }

    public static <T> RegistryMapHandler<T> getRegistryHandler(ResourceKey<? extends Registry<T>> registryKey) {
//        if (REGISTRY_HANDLERS.isEmpty()) {
//            Trimmed.LOGGER.error("Client maps aren't loaded yet! May result in unexpected behavior");
//        }
        return Utils.unsafeCast(REGISTRY_HANDLERS.computeIfAbsent(registryKey, resourceKey -> new RegistryMapHandler<>(registryKey)));
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier pPreparationBarrier, ResourceManager pResourceManager, ProfilerFiller pPreparationsProfiler, ProfilerFiller pReloadProfiler, Executor pBackgroundExecutor, Executor pGameExecutor) {
        return load(pResourceManager).thenCompose(pPreparationBarrier::wait).thenRun(() -> Trimmed.logInDev("Client maps loaded!"));
    }

    private CompletableFuture<Unit> load(ResourceManager resourceManager) {
        UNCHECKED_HANDLER.clear();
        REGISTRY_HANDLERS.values().forEach(BaseMapHandler::clear);

        final Collection<PathInfo> foldersToSearch = PathInfo.gatherAllInfos(ClientUtil.getRegistryAccess());

        for (PathInfo pathInfo : foldersToSearch) {
            FileToIdConverter converter = FileToIdConverter.json("maps/" + pathInfo.getPath());
            Map<ResourceLocation, MapFile> unresolved = readResources(converter, resourceManager);

            if (!(pathInfo instanceof RegistryPathInfo registryPathInfo)) {
                UNCHECKED_HANDLER.resolveMaps(unresolved);
            } else {
                final ResourceKey<? extends Registry<?>> key = registryPathInfo.resourceKey();

                REGISTRY_HANDLERS.computeIfAbsent(key, resourceKey -> new RegistryMapHandler<>(registryPathInfo.castRegistryKey())).resolveMaps(unresolved);
            }
        }

        return CompletableFuture.completedFuture(Unit.INSTANCE);
    }

    private Map<ResourceLocation, MapFile> readResources(FileToIdConverter converter, ResourceManager resourceManager) {
        return converter.listMatchingResourceStacks(resourceManager).entrySet().stream()
                .map(entry -> {
                    ResourceLocation id = converter.fileToId(entry.getKey());
                    return Map.entry(id, readStack(id, entry.getValue()));
                }).collect(Util.toMap());
    }

    private MapFile readStack(ResourceLocation fileName, List<Resource> resourceStack) {
        MapFile.Builder builder = new MapFile.Builder();
        for (Resource resource : resourceStack) {
            try (BufferedReader reader = resource.openAsReader()) {
                JsonObject json = GsonHelper.parse(reader);
                Optional<MapFile> result = Services.PLATFORM_HELPER.decodeWithConditions(MapFile.CODEC, json);
                if (result.isEmpty()) {
                    Trimmed.LOGGER.debug("Skipping loading client map {} as its conditions were not met", fileName);
                    continue;
                }
                MapFile mapFile = result.get();
                if (mapFile.shouldReplace()) {
                    builder = new MapFile.Builder();
                }
                builder.merge(mapFile);
            } catch (JsonParseException | IOException e) {
                throw new RuntimeException("Failed to read %s from %s: ".formatted(fileName, resource.source().packId()), e);
            }
        }
        return builder.build();
    }
}
