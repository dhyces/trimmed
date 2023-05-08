package dhyces.trimmed.impl.client.tags.manager;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.util.ResourcePath;
import dhyces.trimmed.impl.client.tags.ClientTagFile;
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
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ClientTagManager implements PreparableReloadListener {
    private static final UncheckedTagHandler UNCHECKED_HANDLER = new UncheckedTagHandler();
    private static final Map<ResourceKey<Registry<?>>, RegistryTagHandler<?>> REGISTRY_HANDLERS = new HashMap<>();
    private static final Map<ResourceKey<Registry<?>>, DatapackTagHandler<?>> DATAPACKED_HANDLERS = new HashMap<>();

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
        return load(pResourceManager).thenCompose(pPreparationBarrier::wait).thenRun(() -> Trimmed.LOGGER.debug("Client tags loaded!"));
    }

    @SuppressWarnings("UnstableApiUsage")
    private CompletableFuture<Unit> load(ResourceManager resourceManager) {
        UNCHECKED_HANDLER.clear();
        REGISTRY_HANDLERS.clear();
        DATAPACKED_HANDLERS.clear();
        final Map<ResourceLocation, Map<ResourceLocation, Set<TagEntry>>> readEntryMap = new HashMap<>();
        for (Map.Entry<ResourceLocation, List<Resource>> entry : FILE_TO_ID_CONVERTER.listMatchingResourceStacks(resourceManager).entrySet()) {
            ResourcePath idPath = new ResourcePath(entry.getKey());
            Set<TagEntry> readEntries = readResources(entry.getKey(), entry.getValue());

            String registryDirectoryPath = idPath.getDirectoryStringFrom("tags");
            String[] registryDirectories = registryDirectoryPath.split("/");
            ResourceLocation registryId;
            if (registryDirectories.length > 1 && ModList.get().isLoaded(registryDirectories[0])) {
                registryId = new ResourceLocation(registryDirectories[0], idPath.getDirectoryStringFrom(registryDirectories[0])); // TODO: test this
            } else {
                registryId = new ResourceLocation(registryDirectoryPath);
            }
            ResourceLocation truncated = idPath.getFileNameOnly(5).asResourceLocation();
            readEntryMap.computeIfAbsent(registryId, resourceLocation -> new HashMap<>()).put(truncated, readEntries);
        }

        for (Map.Entry<ResourceLocation, Map<ResourceLocation, Set<TagEntry>>> entry : readEntryMap.entrySet()) {
            ResourceLocation directoryPath = entry.getKey();
            Map<ResourceLocation, Set<TagEntry>> unresolved = entry.getValue();

            if (directoryPath.getPath().equals("unchecked")) {
                UNCHECKED_HANDLER.resolveTags(unresolved);
            } else {
                final boolean isModded = !directoryPath.getNamespace().equals("minecraft");

                if (BuiltInRegistries.REGISTRY.get(directoryPath) != null || (isModded && RegistryManager.ACTIVE.getRegistry(directoryPath) != null)) {
                    ForgeRegistry<?> registry = RegistryManager.ACTIVE.getRegistry(directoryPath);
                    ResourceKey<?> registryKey = registry.getRegistryKey();
                    REGISTRY_HANDLERS.computeIfAbsent(cast(registryKey), registryResourceKey -> new RegistryTagHandler<>(cast(registryResourceKey))).resolveTags(unresolved);
                } else {
                    ResourceKey<?> datapackRegistryKey = ResourceKey.createRegistryKey(directoryPath);
                    DATAPACKED_HANDLERS.computeIfAbsent(cast(datapackRegistryKey), registryResourceKey -> new DatapackTagHandler<>(cast(registryResourceKey))).resolveTags(unresolved);
                }
            }
        }

        return CompletableFuture.completedFuture(Unit.INSTANCE);
    }

    private Set<TagEntry> readResources(ResourceLocation resourceLocation, List<Resource> resourceStack) {
        ImmutableSet.Builder<TagEntry> setBuilder = ImmutableSet.builder();
        for (Resource resource : resourceStack) {
            try (BufferedReader reader = resource.openAsReader()) {
                JsonObject json = GsonHelper.parse(reader);
                if (!CraftingHelper.processConditions(json, "conditions", ICondition.IContext.TAGS_INVALID)) {
                    Trimmed.LOGGER.debug("Skipping loading client tag {} as it's conditions were not met", resourceLocation);
                    continue;
                }
                ClientTagFile result = ClientTagFile.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, Trimmed.LOGGER::error);
                if (result.isReplace()) {
                    setBuilder = ImmutableSet.builder();
                }
                setBuilder.addAll(result.tags());
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
