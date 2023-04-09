package dhyces.trimmed.impl.client.tags;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.codec.SetCodec;
import dhyces.trimmed.api.util.CodecUtil;
import dhyces.trimmed.api.util.ResourcePath;
import dhyces.trimmed.api.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ClientTagManager implements PreparableReloadListener {
    private static final Map<ClientTagKey, Set<ResourceLocation>> UNCHECKED_SETS = new HashMap<>();
    private static final Map<ClientRegistryTagKey<?>, Set<?>> CHECKED_SETS = new HashMap<>();
    private static final Map<ClientRegistryTagKey<?>, Set<Holder.Reference<?>>> LAZY_CHECKED_SETS = new HashMap<>();
    private static final Map<ClientRegistryTagKey<?>, Set<ResourceKey<?>>> LAZY_TO_CHECK_SETS = new HashMap<>();

    private static boolean datapacksSynced;

    public static final SetCodec<ResourceLocation> UNCHECKED_CODEC = CodecUtil.setOf(ResourceLocation.CODEC);
    private static final Map<ForgeRegistry<?>, SetCodec<?>> USED_SET_CODECS = new HashMap<>();
    private static final Map<ResourceLocation, SetCodec<ResourceKey<?>>> USED_DATAPACK_SET_CODECS = new HashMap<>();

    public static Optional<Set<ResourceLocation>> getUnchecked(ClientTagKey clientTagKey) {
        if (UNCHECKED_SETS.isEmpty()) {
            Trimmed.LOGGER.error("Client tags aren't loaded yet or are empty! Tried to get " + clientTagKey);
            return Optional.empty();
        }
        return Optional.ofNullable(UNCHECKED_SETS.get(clientTagKey));
    }

    public static <T> Optional<Set<T>> getChecked(ClientRegistryTagKey<T> clientRegistryTagKey) {
        if (CHECKED_SETS.isEmpty()) {
            Trimmed.LOGGER.error("Client tags aren't loaded yet or are empty! Tried to get " + clientRegistryTagKey);
            return Optional.empty();
        }
        return Optional.ofNullable((Set<T>) CHECKED_SETS.get(clientRegistryTagKey));
    }

    public static <T> Optional<Set<Holder.Reference<T>>> getDatapacked(ClientRegistryTagKey<T> clientRegistryTagKey) {
        if (!datapacksSynced) {
            Trimmed.LOGGER.error("Datapacks are not yet loaded! Tried to get: " + clientRegistryTagKey);
            return Optional.empty();
        }
        return Optional.ofNullable(cast(LAZY_CHECKED_SETS.get(clientRegistryTagKey)));
    }

    public static void updateDatapacksSynced(RegistryAccess registryAccess) {
        if (datapacksSynced) {
            Trimmed.LOGGER.info("Datapacks have been updated! Client may need to reload...");
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.displayClientMessage(Component.translatable("trimmed.info.datapacksReloaded"), true);
            }
            return;
        }

        for (Map.Entry<ClientRegistryTagKey<?>, Set<ResourceKey<?>>> entry : LAZY_TO_CHECK_SETS.entrySet()) {
            Optional<? extends Registry<?>> datapackRegistryOptional = registryAccess.registry(entry.getKey().getRegistryKey());
            if (datapackRegistryOptional.isEmpty()) {
                Trimmed.LOGGER.error("Datapack registry " + entry.getKey().getRegistryKey().location() + " does not exist or is not synced to client!");
            } else {
                Set<Object> linkedSet = new LinkedHashSet<>();
                for (ResourceKey<?> id : entry.getValue()) {
                    datapackRegistryOptional.get().getHolder(cast(id)).ifPresent(linkedSet::add);
                }
                LAZY_CHECKED_SETS.put(entry.getKey(), cast(Collections.unmodifiableSet(linkedSet)));
            }
        }
        LAZY_TO_CHECK_SETS.clear();
        datapacksSynced = true;
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier pPreparationBarrier, ResourceManager pResourceManager, ProfilerFiller pPreparationsProfiler, ProfilerFiller pReloadProfiler, Executor pBackgroundExecutor, Executor pGameExecutor) {
        return load(pResourceManager).thenCompose(pPreparationBarrier::wait).thenRun(() -> Trimmed.LOGGER.debug("Client tags loaded!"));
    }

    @SuppressWarnings("UnstableApiUsage")
    private CompletableFuture<Unit> load(ResourceManager resourceManager) {
        datapacksSynced = false;
        CHECKED_SETS.clear();
        UNCHECKED_SETS.clear();
        for (Map.Entry<ResourceLocation, List<Resource>> entry : resourceManager.listResourceStacks("tags", Utils::endsInJson).entrySet()) {
            ResourcePath idPath = new ResourcePath(entry.getKey());
            if (entry.getKey().getPath().contains("unchecked")) {
                Set<ResourceLocation> readSet = readResources(entry.getValue(), UNCHECKED_CODEC);
                ClientTagKey tagKey = ClientTagKey.of(idPath.getFileNameOnly(5).asResourceLocation());
                UNCHECKED_SETS.put(tagKey, readSet);
            } else {
                String registryDirectoryPath = idPath.getDirectoryStringFrom("tags");
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
                    SetCodec<?> setCodec = USED_SET_CODECS.computeIfAbsent(registry, reg -> CodecUtil.setOf(reg.getCodec()));
                    Set<?> readSet = readResources(entry.getValue(), setCodec);
                    ClientRegistryTagKey<?> clientRegistryTagKey = ClientRegistryTagKey.of(registry.getRegistryKey(), idPath.getFileNameOnly(5).asResourceLocation());
                    CHECKED_SETS.put(clientRegistryTagKey, readSet);
                } else {
                    SetCodec<ResourceKey<?>> codec = USED_DATAPACK_SET_CODECS.computeIfAbsent(registryId, resourceLocation -> CodecUtil.setOf(cast(ResourceKey.codec(ResourceKey.createRegistryKey(resourceLocation)))));
                    Set<ResourceKey<?>> readTag = readResources(entry.getValue(), codec);
                    ClientRegistryTagKey<?> clientRegistryMapKey = ClientRegistryTagKey.of(ResourceKey.createRegistryKey(registryId), idPath.getFileNameOnly(5).asResourceLocation());
                    LAZY_TO_CHECK_SETS.put(clientRegistryMapKey, readTag);
                }
            }
        }
        return CompletableFuture.completedFuture(Unit.INSTANCE);
    }

    private <T> Set<T> readResources(List<Resource> resourceStack, SetCodec<T> codec) {
        ImmutableSet.Builder<T> setBuilder = ImmutableSet.builder();
        for (Resource resource : resourceStack) {
            try (BufferedReader reader = resource.openAsReader()) {
                JsonObject json = GsonHelper.parse(reader);
                boolean isReplace = json.get("replace") != null && json.get("replace").getAsBoolean();
                JsonArray pairs = json.getAsJsonArray("values");
                DataResult<Set<T>> result = codec.parse(JsonOps.INSTANCE, pairs);
                if (isReplace) {
                    setBuilder = ImmutableSet.builder();
                }
                setBuilder.addAll(result.getOrThrow(false, Trimmed.LOGGER::error));
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
