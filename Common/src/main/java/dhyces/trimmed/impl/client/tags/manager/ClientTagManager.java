package dhyces.trimmed.impl.client.tags.manager;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dhyces.modhelper.services.Services;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.util.ResourcePath;
import dhyces.trimmed.impl.client.tags.ClientTagFile;
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

    public static final FileToIdConverter FILE_TO_ID_CONVERTER = FileToIdConverter.json("tags");

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
        return Optional.ofNullable(cast(REGISTRY_HANDLERS.get(registryKey)));
    }

    public static <T> Optional<DatapackTagHandler<T>> getDatapackedHandler(ResourceKey<? extends Registry<T>> registryKey) {
        if (DATAPACKED_HANDLERS.isEmpty()) {
            Trimmed.LOGGER.error("Client tags aren't loaded yet! May result in unexpected behavior");
        }
        return Optional.ofNullable(cast(DATAPACKED_HANDLERS.get(registryKey)));
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

    @SuppressWarnings("UnstableApiUsage")
    private <T> CompletableFuture<Unit> load(ResourceManager resourceManager) {
        UNCHECKED_HANDLER.clear();
        REGISTRY_HANDLERS.clear();
        DATAPACKED_HANDLERS.clear();
        final UnresolvedMap<T, Set<TagEntry>> readTags = new UnresolvedMap<>();
        for (Map.Entry<ResourceLocation, List<Resource>> entry : FILE_TO_ID_CONVERTER.listMatchingResourceStacks(resourceManager).entrySet()) {
            ResourcePath idPath = new ResourcePath(entry.getKey());
            Set<TagEntry> readEntries = readResources(entry.getKey(), entry.getValue());

            String registryDirectoryPath = idPath.getDirectoryStringFrom("tags");
            String[] registryDirectories = registryDirectoryPath.split("/");
            ResourceKey<? extends Registry<T>> registryId;
            if (registryDirectories.length > 1 && Services.PLATFORM_HELPER.isModLoaded(registryDirectories[0])) {
                registryId = ResourceKey.createRegistryKey(new ResourceLocation(registryDirectories[0], idPath.getDirectoryStringFrom(registryDirectories[0]))); // TODO: test this
            } else {
                registryId = ResourceKey.createRegistryKey(new ResourceLocation(registryDirectoryPath));
            }
            ResourceLocation truncated = idPath.getFileNameOnly(5).asResourceLocation();
            readTags.add(registryId, truncated, readEntries);
        }

        for (Map.Entry<ResourceKey<? extends Registry<T>>, Map<ResourceLocation, Set<TagEntry>>> entry : readTags) {
            ResourceKey<? extends Registry<T>> handlerKey = entry.getKey();
            Map<ResourceLocation, Set<TagEntry>> unresolved = entry.getValue();

            if (handlerKey.location().getPath().equals("unchecked")) {
                UNCHECKED_HANDLER.resolveTags(unresolved);
            } else {
                final boolean isModded = !handlerKey.location().getNamespace().equals("minecraft");

                if (BuiltInRegistries.REGISTRY.get(handlerKey.location()) != null || (isModded && Services.PLATFORM_HELPER.modRegistryExists(handlerKey))) {
                    REGISTRY_HANDLERS.computeIfAbsent(handlerKey, resourceKey -> new RegistryTagHandler<>(handlerKey)).resolveTags(unresolved);
                } else {
                    DATAPACKED_HANDLERS.computeIfAbsent(handlerKey, resourceKey -> new DatapackTagHandler<>(handlerKey)).resolveTags(unresolved);
                }
            }
        }

        return CompletableFuture.completedFuture(Unit.INSTANCE);
    }

    private Set<TagEntry> readResources(ResourceLocation fileName, List<Resource> resourceStack) {
        ImmutableSet.Builder<TagEntry> setBuilder = ImmutableSet.builder();
        for (Resource resource : resourceStack) {
            try (BufferedReader reader = resource.openAsReader()) {
                JsonObject json = GsonHelper.parse(reader);
                if (!Services.PLATFORM_HELPER.shouldPassConditions(json)) {
                    Trimmed.LOGGER.debug("Skipping loading client tag {} as its conditions were not met", fileName);
                    continue;
                }
                Optional<ClientTagFile> result = ClientTagFile.CODEC.parse(JsonOps.INSTANCE, json).resultOrPartial(Trimmed.LOGGER::error);
                if (result.isPresent()) {
                    ClientTagFile tagFile = result.get();
                    if (tagFile.isReplace()) {
                        setBuilder = ImmutableSet.builder();
                    }
                    setBuilder.addAll(tagFile.tags());
                }
            } catch (IOException e) {
                throw new RuntimeException(e); // TODO
            }
        }
        return setBuilder.build();
    }

    private static <T> T cast(Object o) {
        return (T) o;
    }
}