package dev.dhyces.trimmed.impl.client.tags.manager;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import dev.dhyces.trimmed.impl.util.RegistryType;
import dev.dhyces.trimmed.modhelper.services.Services;
import dev.dhyces.trimmed.Trimmed;
import dev.dhyces.trimmed.api.client.util.ClientUtil;
import dev.dhyces.trimmed.api.util.Utils;
import dev.dhyces.trimmed.impl.client.tags.ClientTagFile;
import dev.dhyces.trimmed.impl.resources.PathInfo;
import dev.dhyces.trimmed.impl.resources.RegistryPathInfo;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagEntry;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ClientTagManager implements PreparableReloadListener {
    private static final UncheckedTagHandler UNCHECKED_HANDLER = new UncheckedTagHandler();
    private static final Map<ResourceKey<? extends Registry<?>>, RegistryTagHandler<?>> REGISTRY_HANDLERS = new HashMap<>();
    private static final Map<ResourceKey<? extends Registry<?>>, DatapackTagHandler<?>> DATAPACKED_HANDLERS = new HashMap<>();

    public static UncheckedTagHandler getUncheckedHandler() {
        if (!UNCHECKED_HANDLER.hasLoaded()) {
            Trimmed.LOGGER.error("Client tags aren't loaded yet! May result in unexpected behavior");
        }
        return UNCHECKED_HANDLER;
    }

    public static <T> Optional<RegistryTagHandler<T>> getRegistryHandler(ResourceKey<? extends Registry<T>> registryKey) {
        if (REGISTRY_HANDLERS.isEmpty()) {
            Trimmed.LOGGER.error("Client tags aren't loaded yet! May result in unexpected behavior");
        }
        return Optional.ofNullable(Utils.unsafeCast(REGISTRY_HANDLERS.get(registryKey)));
    }

    public static <T> Optional<DatapackTagHandler<T>> getDatapackedHandler(ResourceKey<? extends Registry<T>> registryKey) {
        if (DATAPACKED_HANDLERS.isEmpty()) {
            Trimmed.LOGGER.error("Datapack client tags aren't loaded yet! May result in unexpected behavior");
        }
        return Optional.ofNullable(Utils.unsafeCast(DATAPACKED_HANDLERS.get(registryKey)));
    }

    public static void updateDatapacksSynced(RegistryAccess registryAccess) {
        for (DatapackTagHandler<?> handler : DATAPACKED_HANDLERS.values()) {
            handler.update(registryAccess);
        }
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier pPreparationBarrier, ResourceManager pResourceManager, ProfilerFiller pPreparationsProfiler, ProfilerFiller pReloadProfiler, Executor pBackgroundExecutor, Executor pGameExecutor) {
        return load(pResourceManager).thenCompose(pPreparationBarrier::wait).thenRun(() -> Trimmed.logInDev("Client tags loaded!"));
    }

    private CompletableFuture<Unit> load(ResourceManager resourceManager) {
        UNCHECKED_HANDLER.clear();
        REGISTRY_HANDLERS.clear();
        DATAPACKED_HANDLERS.clear();
        final Collection<PathInfo> foldersToSearch = PathInfo.gatherAllInfos(ClientUtil.getRegistryAccess());

        for (PathInfo pathInfo : foldersToSearch) {
            FileToIdConverter converter = FileToIdConverter.json("tags/" + pathInfo.getPath());
            Map<ResourceLocation, Set<TagEntry>> unresolved = readMap(converter, resourceManager);

            if (!(pathInfo instanceof RegistryPathInfo registryPathInfo)) {
                UNCHECKED_HANDLER.resolveTags(unresolved);
            } else {
                final ResourceKey<? extends Registry<?>> key = registryPathInfo.resourceKey();

                if (registryPathInfo.registryType() == RegistryType.STATIC) {
                    REGISTRY_HANDLERS.computeIfAbsent(key, resourceKey -> new RegistryTagHandler<>(registryPathInfo.castRegistryKey())).resolveTags(unresolved);
                } else {
                    DATAPACKED_HANDLERS.computeIfAbsent(key, resourceKey -> new DatapackTagHandler<>(registryPathInfo.castRegistryKey())).resolveTags(unresolved);
                }
            }
        }

        return CompletableFuture.completedFuture(Unit.INSTANCE);
    }

    private Map<ResourceLocation, Set<TagEntry>> readMap(FileToIdConverter converter, ResourceManager resourceManager) {
        return converter.listMatchingResourceStacks(resourceManager).entrySet().stream()
                .map(entry -> {
                    ResourceLocation id = converter.fileToId(entry.getKey());
                    return Map.entry(id, readResources(id, entry.getValue()));
                }).collect(Util.toMap());
    }

    private Set<TagEntry> readResources(ResourceLocation fileName, List<Resource> resourceStack) {
        ImmutableSet.Builder<TagEntry> setBuilder = ImmutableSet.builder();
        for (Resource resource : resourceStack) {
            try (BufferedReader reader = resource.openAsReader()) {
                JsonObject json = GsonHelper.parse(reader);
                Optional<ClientTagFile> result = Services.PLATFORM_HELPER.decodeWithConditions(ClientTagFile.CODEC, json);
                if (result.isEmpty()) {
                    Trimmed.LOGGER.debug("Skipping loading client tag {} as its conditions were not met", fileName);
                    continue;
                }
                ClientTagFile tagFile = result.get();
                if (tagFile.isReplace()) {
                    setBuilder = ImmutableSet.builder();
                }
                setBuilder.addAll(tagFile.tags());
            } catch (JsonParseException | IOException e) {
                throw new RuntimeException("Failed to read %s from %s: ".formatted(fileName, resource.source().packId()), e);
            }
        }
        return setBuilder.build();
    }
}
