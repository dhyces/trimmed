package dhyces.trimmed.impl.client.tags;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.codec.SetCodec;
import dhyces.trimmed.api.util.CodecUtil;
import dhyces.trimmed.api.util.ResourcePath;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
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
import org.codehaus.plexus.util.dag.CycleDetectedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

public class ClientTagManager implements PreparableReloadListener {
    private static final Map<ClientTagKey, Set<ResourceLocation>> UNCHECKED_SETS = new HashMap<>();
    private static final Map<ClientRegistryTagKey<?>, Set<?>> REGISTRY_SETS = new HashMap<>();
    private static final Map<ClientRegistryTagKey<?>, Set<Holder.Reference<?>>> DATAPACKED_SETS = new HashMap<>();
    private static final Map<ClientRegistryTagKey<?>, Set<ResourceKey<?>>> DATAPACKED_KEYS_TO_RESOLVE = new HashMap<>();

    private static boolean datapacksSynced;

    private static final Map<ForgeRegistry<?>, SetCodec<?>> USED_SET_CODECS = new HashMap<>();
    private static final Map<ResourceLocation, SetCodec<ResourceKey<?>>> USED_DATAPACK_SET_CODECS = new HashMap<>();

    public static final FileToIdConverter FILE_TO_ID_CONVERTER = FileToIdConverter.json("tags");

    public static Optional<Set<ResourceLocation>> getUnchecked(ClientTagKey clientTagKey) {
        if (UNCHECKED_SETS.isEmpty()) {
            Trimmed.LOGGER.error("Client tags aren't loaded yet or are empty! Tried to get " + clientTagKey);
            return Optional.empty();
        }
        return Optional.ofNullable(UNCHECKED_SETS.get(clientTagKey));
    }

    public static <T> Optional<Set<T>> getChecked(ClientRegistryTagKey<T> clientRegistryTagKey) {
        if (REGISTRY_SETS.isEmpty()) {
            Trimmed.LOGGER.error("Client tags aren't loaded yet or are empty! Tried to get " + clientRegistryTagKey);
            return Optional.empty();
        }
        return Optional.ofNullable((Set<T>) REGISTRY_SETS.get(clientRegistryTagKey));
    }

    public static <T> Optional<Set<Holder.Reference<T>>> getDatapacked(ClientRegistryTagKey<T> clientRegistryTagKey) {
        if (!datapacksSynced) {
            Trimmed.LOGGER.error("Datapacks are not yet loaded! Tried to get: " + clientRegistryTagKey);
            return Optional.empty();
        }
        return Optional.ofNullable(cast(DATAPACKED_SETS.get(clientRegistryTagKey)));
    }

    public static void updateDatapacksSynced(RegistryAccess registryAccess) {
        if (datapacksSynced) {
            Trimmed.LOGGER.info("Datapacks have been updated! Client may need to reload...");
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.displayClientMessage(Component.translatable("trimmed.info.datapacksReloaded"), true);
            }
            return;
        }

        for (Map.Entry<ClientRegistryTagKey<?>, Set<ResourceKey<?>>> entry : DATAPACKED_KEYS_TO_RESOLVE.entrySet()) {
            Optional<? extends Registry<?>> datapackRegistryOptional = registryAccess.registry(entry.getKey().getRegistryKey());
            if (datapackRegistryOptional.isEmpty()) {
                Trimmed.LOGGER.error("Datapack registry " + entry.getKey().getRegistryKey().location() + " does not exist or is not synced to client!");
            } else {
                Set<Object> linkedSet = new LinkedHashSet<>();
                for (ResourceKey<?> id : entry.getValue()) {
                    datapackRegistryOptional.get().getHolder(cast(id)).ifPresent(linkedSet::add);
                }
                DATAPACKED_SETS.put(entry.getKey(), cast(Collections.unmodifiableSet(linkedSet)));
            }
        }
        DATAPACKED_KEYS_TO_RESOLVE.clear();
        datapacksSynced = true;
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier pPreparationBarrier, ResourceManager pResourceManager, ProfilerFiller pPreparationsProfiler, ProfilerFiller pReloadProfiler, Executor pBackgroundExecutor, Executor pGameExecutor) {
        return load(pResourceManager).thenCompose(pPreparationBarrier::wait).thenRun(() -> Trimmed.LOGGER.debug("Client tags loaded!"));
    }

    @SuppressWarnings("UnstableApiUsage")
    private CompletableFuture<Unit> load(ResourceManager resourceManager) {
        datapacksSynced = false;
        REGISTRY_SETS.clear();
        UNCHECKED_SETS.clear();
        DATAPACKED_SETS.clear();
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

            try {
                if (directoryPath.getPath().equals("unchecked")) {
                    for (Map.Entry<ResourceLocation, Set<TagEntry>> tagEntry : unresolved.entrySet()) {
                        resolveTag(unresolved, cast(UNCHECKED_SETS), ClientTagKey::of, Function.identity(), tagEntry.getKey(), new LinkedHashSet<>());
                    }
                } else {
                    final boolean isModded = !directoryPath.getNamespace().equals("minecraft");

                    if (BuiltInRegistries.REGISTRY.get(directoryPath) != null || (isModded && RegistryManager.ACTIVE.getRegistry(directoryPath) != null)) {
                        ForgeRegistry<?> registry = RegistryManager.ACTIVE.getRegistry(directoryPath);
                        ResourceKey<?> registryKey = registry.getRegistryKey();
                        for (Map.Entry<ResourceLocation, Set<TagEntry>> tagEntry : unresolved.entrySet()) {
                            resolveTag(unresolved, REGISTRY_SETS, id -> ClientRegistryTagKey.of(cast(registryKey), id), registry::getValue, tagEntry.getKey(), new LinkedHashSet<>());
                        }
                    } else {
                        ResourceKey<?> datapackRegistryKey = ResourceKey.createRegistryKey(directoryPath);
                        for (Map.Entry<ResourceLocation, Set<TagEntry>> tagEntry : unresolved.entrySet()) {
                            resolveTag(unresolved, cast(DATAPACKED_KEYS_TO_RESOLVE), id -> ClientRegistryTagKey.of(cast(datapackRegistryKey), id), resourceLocation -> ResourceKey.create(cast(datapackRegistryKey), resourceLocation), tagEntry.getKey(), new LinkedHashSet<>());
                        }
                    }
                }
            } catch (CycleDetectedException e) {
                Trimmed.LOGGER.error(e.getMessage());
            }
        }

        return CompletableFuture.completedFuture(Unit.INSTANCE);
    }

    private <K, V> Set<?> resolveTag(Map<ResourceLocation, Set<TagEntry>> unresolvedTags, Map<K, Set<?>> registered, Function<ResourceLocation, K> tagKeyFactory, Function<ResourceLocation, V> valueResolver, ResourceLocation tagId, LinkedHashSet<ResourceLocation> resolutionSet) throws CycleDetectedException {
        K key = tagKeyFactory.apply(tagId);
        if (registered.containsKey(key)) {
            return registered.get(key);
        }

        if (resolutionSet.contains(tagId)) {
            throw new CycleDetectedException("ClientTag cycle detected! ", resolutionSet.stream().map(ResourceLocation::toString).toList());
        }

        resolutionSet.add(tagId);

        ImmutableSet.Builder builder = ImmutableSet.builder();

        for (TagEntry entry : unresolvedTags.get(tagId)) {
            if (entry.isTag()) {
                builder.addAll(resolveTag(unresolvedTags, registered, tagKeyFactory, valueResolver, entry.getId(), resolutionSet));
            } else {
                builder.add(valueResolver.apply(entry.getId()));
            }
        }

        // Adds it to the registered map and returns the set
        return registered.computeIfAbsent(key, k -> builder.build());
    }

    private Set<TagEntry> readResources(ResourceLocation resourceLocation, List<Resource> resourceStack) {
        ImmutableSet.Builder<TagEntry> setBuilder = ImmutableSet.builder();
        for (Resource resource : resourceStack) {
            try (BufferedReader reader = resource.openAsReader()) {
                JsonObject json = GsonHelper.parse(reader);
                if (!CraftingHelper.processConditions(json, "conditions", ICondition.IContext.TAGS_INVALID)) {
                    Trimmed.LOGGER.debug("Skipping loading recipe {} as it's conditions were not met", resourceLocation);
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
