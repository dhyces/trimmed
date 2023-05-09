package dhyces.trimmed.impl.client.maps.manager;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.data.maps.MapEntry;
import dhyces.trimmed.api.util.ResourcePath;
import dhyces.trimmed.impl.client.maps.ClientMapKey;
import dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;
import net.minecraft.core.Holder;
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

public class ClientMapManager implements PreparableReloadListener {
    private static final UncheckedMapHandler UNCHECKED_HANDLERS = new UncheckedMapHandler();
    private static final Map<ResourceKey<?>, RegistryMapHandler<?>> REGISTRY_HANDLERS = new HashMap<>();
    private static final Map<ResourceKey<?>, DatapackMapHandler<?>> DATAPACKED_HANDLERS = new HashMap<>();
    private static final Map<ClientRegistryMapKey<?>, Map<ResourceKey<?>, String>> LAZY_TO_CHECK_MAPS = new HashMap<>();

    private static final FileToIdConverter FILE_TO_ID_CONVERTER = FileToIdConverter.json("maps");

    public static UncheckedMapHandler getUnchecked(ClientMapKey clientMapKey) {
        if (!UNCHECKED_HANDLERS.hasLoaded()) {
            Trimmed.LOGGER.error("Maps have not been loaded or are empty! Tried to get: " + clientMapKey);
            return Optional.empty();
        }
        return Optional.ofNullable(UNCHECKED_HANDLERS.getMap(clientMapKey));
    }

    public static <T> RegistryMapHandler<T> getChecked(ClientRegistryMapKey<T> clientRegistryMapKey) {
        if (REGISTRY_HANDLERS.isEmpty()) {
            Trimmed.LOGGER.error("Maps have not been loaded or are empty! Tried to get: " + clientRegistryMapKey);
            return Optional.empty();
        }
        return Optional.ofNullable((Map<T, String>) REGISTRY_HANDLERS.get(clientRegistryMapKey));
    }

    public static <T> Optional<Map<Holder.Reference<T>, String>> getDatapacked(ClientRegistryMapKey<T> clientRegistryMapKey) {
        if (!datapacksSynced) {
            Trimmed.LOGGER.error("Datapacks are not yet loaded! Tried to get: " + clientRegistryMapKey);
            return Optional.empty();
        }
        return Optional.ofNullable(cast(DATAPACKED_HANDLERS.get(clientRegistryMapKey)));
    }

    public static void updateDatapacksSynced(RegistryAccess registryAccess) {
        if (datapacksSynced) {
            Trimmed.LOGGER.info("Datapacks have been updated! Client may need to reload...");
            return;
        }

        for (Map.Entry<ClientRegistryMapKey<?>, Map<ResourceKey<?>, String>> entry : LAZY_TO_CHECK_MAPS.entrySet()) {
            Optional<? extends Registry<?>> datapackRegistryOptional = registryAccess.registry(entry.getKey().getRegistryKey());
            if (datapackRegistryOptional.isEmpty()) {
                Trimmed.LOGGER.error("Datapack registry " + entry.getKey().getRegistryKey().location() + " does not exist or is not synced to client!");
            } else {
                ImmutableMap.Builder<Object, String> mapBuilder = ImmutableMap.builder();
                for (Map.Entry<ResourceKey<?>, String> entryTransform : entry.getValue().entrySet()) {
                    datapackRegistryOptional.get().getHolder(cast(entryTransform.getKey())).ifPresent(o -> mapBuilder.put(o, entryTransform.getValue()));
                }
                DATAPACKED_HANDLERS.put(entry.getKey(), cast(mapBuilder.build()));
            }
        }
        LAZY_TO_CHECK_MAPS.clear();
        datapacksSynced = true;
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier pPreparationBarrier, ResourceManager pResourceManager, ProfilerFiller pPreparationsProfiler, ProfilerFiller pReloadProfiler, Executor pBackgroundExecutor, Executor pGameExecutor) {
        return load(pResourceManager, pBackgroundExecutor).thenCompose(pPreparationBarrier::wait).thenRun(() -> Trimmed.LOGGER.debug("Client maps loaded!"));
    }

    @SuppressWarnings("UnstableApiUsage")
    private CompletableFuture<Unit> load(ResourceManager resourceManager, Executor backgroundExecutor) {
        UNCHECKED_HANDLERS.clear();
        REGISTRY_HANDLERS.clear();
        DATAPACKED_HANDLERS.clear();
        final Map<ResourceLocation, Map<ResourceLocation, Map<ResourceLocation, MapEntry>>> readEntryMap = new HashMap<>();
        for (Map.Entry<ResourceLocation, List<Resource>> entry : FILE_TO_ID_CONVERTER.listMatchingResourceStacks(resourceManager).entrySet()) {
            ResourcePath idPath = new ResourcePath(entry.getKey());
            if (entry.getKey().getPath().contains("unchecked")) {
                Map<ResourceLocation, String> readMap = readResources(entry.getKey(), entry.getValue(), UNCHECKED_CODEC);
                ClientMapKey mapKey = ClientMapKey.of(idPath.getFileNameOnly(5).asResourceLocation());
                UNCHECKED_HANDLERS.put(mapKey, readMap);
            } else {
                String registryDirectoryPath = idPath.getDirectoryStringFrom("maps");
                String[] registryDirectories = registryDirectoryPath.split("/");
                ResourceLocation registryId;
                final boolean isModded;
                if (registryDirectories.length > 1 && ModList.get().isLoaded(registryDirectories[0])) {
                    registryId = new ResourceLocation(registryDirectories[0], idPath.getDirectoryStringFrom(registryDirectories[0])); // TODO: test this
                    isModded = true;
                } else {
                    registryId = new ResourceLocation(registryDirectoryPath);
                    isModded = false;
                }

                if (BuiltInRegistries.REGISTRY.get(registryId) != null || (isModded && RegistryManager.ACTIVE.getRegistry(registryId) != null)) {
                    ForgeRegistry<?> registry = RegistryManager.ACTIVE.getRegistry(registryId);
                    UnboundedMapCodec<?, String> mapCodec = USED_MAP_CODECS.computeIfAbsent(registry, reg -> Codec.unboundedMap(reg.getCodec(), Codec.STRING));
                    Map<?, String> readMap = readResources(entry.getKey(), entry.getValue(), mapCodec);
                    ClientRegistryMapKey<?> clientRegistryMapKey = ClientRegistryMapKey.of(registry.getRegistryKey(), idPath.getFileNameOnly(5).asResourceLocation());
                    REGISTRY_HANDLERS.put(clientRegistryMapKey, readMap);
                } else {
                    UnboundedMapCodec<ResourceKey<?>, String> codec = USED_DATAPACK_MAP_CODECS.computeIfAbsent(registryId, resourceLocation -> Codec.unboundedMap(cast(ResourceKey.codec(ResourceKey.createRegistryKey(resourceLocation))), Codec.STRING));
                    Map<ResourceKey<?>, String> readMap = readResources(entry.getKey(), entry.getValue(), codec);
                    ClientRegistryMapKey<?> clientRegistryMapKey = ClientRegistryMapKey.of(ResourceKey.createRegistryKey(registryId), idPath.getFileNameOnly(5).asResourceLocation());
                    LAZY_TO_CHECK_MAPS.put(clientRegistryMapKey, readMap);
                }
            }
        }
        return CompletableFuture.completedFuture(Unit.INSTANCE);
    }

    private <T> Map<T, String> readResources(ResourceLocation resourceLocation, List<Resource> resourceStack, Codec<Map<T, String>> codec) {
        ImmutableMap.Builder<T, String> mapBuilder = ImmutableMap.builder();
        for (Resource resource : resourceStack) {
            try (BufferedReader reader = resource.openAsReader()) {
                JsonObject json = GsonHelper.parse(reader);
                if (!CraftingHelper.processConditions(json, "conditions", ICondition.IContext.TAGS_INVALID)) {
                    Trimmed.LOGGER.debug("Skipping loading client map {} as it's conditions were not met", resourceLocation);
                    continue;
                }
                boolean isReplace = json.get("replace") != null && json.get("replace").getAsBoolean();
                JsonObject pairs = json.getAsJsonObject("pairs");
                DataResult<Map<T, String>> result = codec.parse(JsonOps.INSTANCE, pairs);
                if (isReplace) {
                    mapBuilder = ImmutableMap.builder();
                }
                mapBuilder.putAll(result.getOrThrow(false, Trimmed.LOGGER::error));
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
