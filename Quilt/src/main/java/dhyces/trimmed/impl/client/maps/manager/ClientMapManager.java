package dhyces.trimmed.impl.client.maps.manager;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dhyces.trimmed.QuiltTrimmed;
import dhyces.trimmed.api.client.util.ClientUtil;
import dhyces.trimmed.api.data.maps.MapFile;
import dhyces.trimmed.api.data.maps.MapValue;
import dhyces.trimmed.impl.resources.PathInfo;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.Util;
import net.minecraft.resources.FileToIdConverter;
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

    private static final FileToIdConverter FILE_TO_ID_CONVERTER = FileToIdConverter.json("maps");

    public static UncheckedMapHandler getUncheckedHandler() {
//        if (!UNCHECKED_HANDLERS.hasLoaded()) {
//            Trimmed.LOGGER.error("Client maps aren't loaded yet! May result in unexpected behavior");
//        }
        return UNCHECKED_HANDLER;
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier pPreparationBarrier, ResourceManager pResourceManager, ProfilerFiller pPreparationsProfiler, ProfilerFiller pReloadProfiler, Executor pBackgroundExecutor, Executor pGameExecutor) {
        return load(pResourceManager).thenCompose(pPreparationBarrier::wait).thenRun(() -> QuiltTrimmed.logInDev("Client maps loaded!"));
    }

    @SuppressWarnings("UnstableApiUsage")
    private CompletableFuture<Unit> load(ResourceManager resourceManager) {
        UNCHECKED_HANDLER.clear();

        final Collection<PathInfo> foldersToSearch = PathInfo.gatherAllInfos(ClientUtil.getRegistryAccess());

        for (PathInfo pathInfo : foldersToSearch) {
            FileToIdConverter converter = FileToIdConverter.json("maps/" + pathInfo.getPath());
            Map<ResourceLocation, Set<Map.Entry<ResourceLocation, MapValue>>> unresolved = readResources(converter, resourceManager);

            UNCHECKED_HANDLER.resolveMaps(unresolved);
        }

        return CompletableFuture.completedFuture(Unit.INSTANCE);
    }

    private Map<ResourceLocation, Set<Map.Entry<ResourceLocation, MapValue>>> readResources(FileToIdConverter converter, ResourceManager resourceManager) {
        return converter.listMatchingResourceStacks(resourceManager).entrySet().stream()
                .map(entry -> {
                    ResourceLocation id = converter.fileToId(entry.getKey());
                    return Map.entry(id, readStack(id, entry.getValue()).entrySet());
                }).collect(Util.toMap());
    }

    private Map<ResourceLocation, MapValue> readStack(ResourceLocation fileName, List<Resource> resourceStack) {
        ImmutableMap.Builder<ResourceLocation, MapValue> mapBuilder = ImmutableMap.builder();
        for (Resource resource : resourceStack) {
            try (BufferedReader reader = resource.openAsReader()) {
                JsonObject json = GsonHelper.parse(reader);
                if (!ResourceConditions.objectMatchesConditions(json)) {
                    QuiltTrimmed.LOGGER.debug("Skipping loading client map {} as its conditions were not met", fileName);
                    continue;
                }
                Optional<MapFile> mapFileOptional = MapFile.CODEC.parse(JsonOps.INSTANCE, json).resultOrPartial(QuiltTrimmed.LOGGER::error);
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
