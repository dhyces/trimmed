package dev.dhyces.trimmed.impl.client.tags.manager;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.dhyces.trimmed.api.client.tag.TagHolder;
import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.data.client.tag.ClientTagEntry;
import dev.dhyces.trimmed.api.data.client.tag.ClientTagFile;
import dev.dhyces.trimmed.impl.client.maps.KeyResolvers;
import dev.dhyces.trimmed.api.client.tag.ClientTagKey;
import dev.dhyces.trimmed.modhelper.services.Services;
import dev.dhyces.trimmed.Trimmed;
import dev.dhyces.trimmed.api.util.Utils;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.DependencySorter;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

public class ClientTagManager implements PreparableReloadListener {
    public static final String PATH = "trimmed/tags/";
    private static final Logger LOGGER = LoggerFactory.getLogger("Trimmed / Client Tags");
    private static final Map<ClientTagKey<?>, ClientTagHolder<?>> REGISTRY = new Reference2ObjectOpenHashMap<>();
    // TODO: Probably need to make these weak or figure out another solution
    private static RegistryAccess syncedAccess;
    private static final List<Consumer<RegistryAccess>> REQUIRE_SYNC = new ObjectArrayList<>();

    public static <T> TagHolder<T> getHolder(ClientTagKey<T> clientTagKey) {
        return getOrCreateHolder(clientTagKey);
    }

    @SuppressWarnings("unchecked")
    private static <T> ClientTagHolder<T> getOrCreateHolder(ClientTagKey<T> clientTagKey) {
        return (ClientTagHolder<T>) REGISTRY.computeIfAbsent(clientTagKey, ClientTagHolder::new);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static <T> ClientTagHolder<T> getExistingHolder(ClientTagKey<T> clientTagKey) {
        return (ClientTagHolder<T>) REGISTRY.get(clientTagKey);
    }

    public static void updateDatapacksSynced(RegistryAccess registryAccess) {
        syncedAccess = registryAccess;
        for (Consumer<RegistryAccess> consumer : REQUIRE_SYNC) {
            consumer.accept(registryAccess);
        }
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier pPreparationBarrier, ResourceManager pResourceManager, ProfilerFiller pPreparationsProfiler, ProfilerFiller pReloadProfiler, Executor pBackgroundExecutor, Executor pGameExecutor) {
        return load(pResourceManager).thenCompose(pPreparationBarrier::wait).thenRun(() -> Trimmed.logInDev("Client tags loaded!"));
    }

    private CompletableFuture<Void> load(ResourceManager resourceManager) {
        REGISTRY.values().forEach(ClientTagHolder::reset);
        REQUIRE_SYNC.clear();

        return CompletableFuture.allOf(StreamSupport.stream(KeyResolvers.getEntries().spliterator(), false)
                .map(entry -> CompletableFuture.runAsync(() -> {
                    if (entry.getValue().requiresActiveWorld()) {
                        REQUIRE_SYNC.add(registryAccess ->
                                resolveTags(entry.getKey(), entry.getValue(), resourceManager, registryAccess.createSerializationContext(JsonOps.INSTANCE))
                        );
                    }

                    if (!entry.getValue().requiresActiveWorld()) {
                        resolveTags(entry.getKey(), entry.getValue(), resourceManager, JsonOps.INSTANCE);
                    } else if (syncedAccess != null) {
                        resolveTags(entry.getKey(), entry.getValue(), resourceManager, syncedAccess.createSerializationContext(JsonOps.INSTANCE));
                    }
                }))
                .toArray(CompletableFuture[]::new)
        );
    }

    private <T> void resolveTags(ResourceLocation registryId, KeyResolver<T> keyResolver, ResourceManager resourceManager, DynamicOps<JsonElement> jsonOps) {
        String resolverPath = PATH + Utils.namespacedPath(registryId);
        FileToIdConverter converter = FileToIdConverter.json(resolverPath);
        Map<ResourceLocation, Set<ClientTagEntry>> unresolved = Utils.unsafeCast(readMap(converter, resourceManager, keyResolver, jsonOps));
        DependencySorter<ResourceLocation, TagSetEntry<T>> sorter = new DependencySorter<>();
        unresolved.forEach((resourceLocation, entries) -> sorter.addEntry(resourceLocation, new TagSetEntry<>(entries)));
        sorter.orderByDependencies((resourceLocation, tagEntrySet) -> {
            try {
                resolveEntry(resourceLocation, tagEntrySet, keyResolver, jsonOps);
            } catch (IllegalStateException e) {
                LOGGER.error("Could not resolve entry", e);
            }
        });
    }

    private <T> Map<ResourceLocation, Set<ClientTagEntry>> readMap(FileToIdConverter converter, ResourceManager resourceManager, KeyResolver<T> keyResolver, DynamicOps<JsonElement> jsonOps) {
        ImmutableMap.Builder<ResourceLocation, Set<ClientTagEntry>> builder = ImmutableMap.builder();
        for (Map.Entry<ResourceLocation, List<Resource>> entry : converter.listMatchingResourceStacks(resourceManager).entrySet()) {
            ResourceLocation id = converter.fileToId(entry.getKey());
            try {
                builder.put(id, readResources(id, entry.getValue(), keyResolver, jsonOps));
            } catch (IllegalStateException e) {
                LOGGER.error("Failed to parse client tag", e);
            }
        }
        return builder.build();
    }

    private <T> Set<ClientTagEntry> readResources(ResourceLocation fileName, List<Resource> resourceStack, KeyResolver<T> keyResolver, DynamicOps<JsonElement> jsonOps) {
        ImmutableSet.Builder<ClientTagEntry> setBuilder = ImmutableSet.builder();
        for (Resource resource : resourceStack) {
            try (BufferedReader reader = resource.openAsReader()) {
                JsonObject json = GsonHelper.parse(reader);
                Optional<ClientTagFile> result = Services.PLATFORM_HELPER.decodeWithConditions(ClientTagFile.CODEC, jsonOps, json);
                if (result.isEmpty()) {
                    LOGGER.debug("Skipping loading client tag {} as its conditions were not met", fileName);
                    continue;
                }
                ClientTagFile tagFile = result.get();
                if (tagFile.replace()) {
                    setBuilder = ImmutableSet.builder();
                }
                setBuilder.addAll(tagFile.entries());
            } catch (JsonParseException | IOException e) {
                throw new IllegalStateException("Failed to read %s from %s: ".formatted(fileName, resource.source().packId()), e);
            }
        }
        return setBuilder.build();
    }

    private <T> void resolveEntry(ResourceLocation id, TagSetEntry<T> tagSetEntry, KeyResolver<T> resolver, DynamicOps<JsonElement> jsonOps) {
        ClientTagKey<T> key = ClientTagKey.of(resolver, id);
        Set<T> set;
        Set<T> optionalSet;
        if (resolver instanceof KeyResolver.RegistryResolver<T>) {
            set = new ReferenceLinkedOpenHashSet<>();
            optionalSet = new ReferenceLinkedOpenHashSet<>();
        } else {
            set = new ObjectLinkedOpenHashSet<>();
            optionalSet = new ObjectLinkedOpenHashSet<>();
        }

        for (ClientTagEntry tagEntry : tagSetEntry.entries()) {
            if (tagEntry.isTag()) {
                ClientTagKey<T> clientTagKey = tagEntry.getTag(resolver);
                ClientTagHolder<T> holder = getExistingHolder(clientTagKey);
                if (holder != null && holder.backingSet != null) {
                    holder.mergeInto(set, optionalSet);
                } else if (tagEntry.isRequired()) {
                    throw new IllegalStateException("Could not get required client tag \"%s\" for \"%s\"".formatted(clientTagKey.getTagId(), id));
                }
            } else {
                T element = resolver.decode(tagEntry.getId(), jsonOps);
                if (element == null) {
                    if (tagEntry.isRequired()) {
                        throw new IllegalStateException("Could not parse required element for \"%s\"".formatted(id));
                    }
                } else {
                    set.add(element);
                    if (!tagEntry.isRequired()) {
                        optionalSet.add(element);
                    }
                }
            }
        }
        getOrCreateHolder(key).update(set, optionalSet);
    }

    record TagSetEntry<T>(Set<ClientTagEntry> entries) implements DependencySorter.Entry<ResourceLocation> {

        @Override
        public void visitRequiredDependencies(Consumer<ResourceLocation> visitor) {
            entries.forEach(tagEntry -> {
                if (tagEntry.isTag() && tagEntry.isRequired()) {
                    visitor.accept(tagEntry.getId());
                }
            });
        }

        @Override
        public void visitOptionalDependencies(Consumer<ResourceLocation> visitor) {
            entries.forEach(tagEntry -> {
                if (tagEntry.isTag() && !tagEntry.isRequired()) {
                    visitor.accept(tagEntry.getId());
                }
            });
        }
    }

    static class ClientTagHolder<T> implements TagHolder<T> {
        private final ClientTagKey<T> key;
        private Set<T> backingSet;
        private Set<T> optionalElements;

        public ClientTagHolder(ClientTagKey<T> key) {
            this.key = key;
        }

        private void update(Set<T> backingSet, Set<T> optionalElements) {
            this.backingSet = backingSet;
            this.optionalElements = optionalElements;
        }

        private void reset() {
            this.backingSet = null;
            this.optionalElements = null;
        }

        private void mergeInto(Set<T> set, Set<T> optionalElements) {
            set.addAll(backingSet);
            if (this.optionalElements != null) {
                optionalElements.addAll(this.optionalElements);
            }
        }

        @Override
        public ClientTagKey<T> unwrapKey() {
            return key;
        }

        @Override
        public Set<T> getSet() {
            if (backingSet == null) {
                Trimmed.LOGGER.error("Cannot access tag set for {} because it doesn't exist!", key);
                return Set.of();
            }
            return backingSet;
        }

        @Override
        public boolean isRequired(T element) {
            if (optionalElements == null) {
                return true;
            }
            return optionalElements.contains(element);
        }

        @Override
        public boolean isBound() {
            return backingSet != null;
        }
    }
}
