package dev.dhyces.trimmed.impl.client.tags.manager;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.dhyces.trimmed.api.client.tag.TagHolder;
import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.data.tag.ClientTagEntry;
import dev.dhyces.trimmed.api.data.tag.ClientTagFile;
import dev.dhyces.trimmed.impl.client.maps.KeyResolvers;
import dev.dhyces.trimmed.api.client.tag.ClientTagKey;
import dev.dhyces.trimmed.modhelper.services.Services;
import dev.dhyces.trimmed.Trimmed;
import dev.dhyces.trimmed.api.util.Utils;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.Util;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.DependencySorter;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Unit;
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

//    public static void updateDatapacksSynced(RegistryAccess registryAccess) {
//        for (DatapackTagHandler<?> handler : REGISTRY.values()) {
//            handler.update(registryAccess);
//        }
//    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier pPreparationBarrier, ResourceManager pResourceManager, ProfilerFiller pPreparationsProfiler, ProfilerFiller pReloadProfiler, Executor pBackgroundExecutor, Executor pGameExecutor) {
        return load(pResourceManager).thenCompose(pPreparationBarrier::wait).thenRun(() -> Trimmed.logInDev("Client tags loaded!"));
    }

    private CompletableFuture<Void> load(ResourceManager resourceManager) {
        REGISTRY.values().forEach(ClientTagHolder::reset);

        return CompletableFuture.allOf(
                StreamSupport.stream(KeyResolvers.getEntries().spliterator(), true)
                        .map(entry -> CompletableFuture.runAsync(() -> resolveTags(entry.getKey(), entry.getValue(), resourceManager))).toArray(CompletableFuture[]::new)
        );
    }

    private <T> void resolveTags(ResourceLocation registryId, KeyResolver<T> keyResolver, ResourceManager resourceManager) {
        String resolverPath = PATH + Utils.namespacedPath(registryId, '/');
        FileToIdConverter converter = FileToIdConverter.json(resolverPath);
        Map<ResourceLocation, Set<ClientTagEntry<T>>> unresolved = Utils.unsafeCast(readMap(converter, resourceManager, keyResolver));
        DependencySorter<ResourceLocation, TagSetEntry<T>> sorter = new DependencySorter<>();
        unresolved.forEach((resourceLocation, entries) -> sorter.addEntry(resourceLocation, new TagSetEntry<>(entries)));
        sorter.orderByDependencies((resourceLocation, tagEntrySet) -> {
            try {
                resolveEntry(resourceLocation, tagEntrySet, keyResolver);
            } catch (IllegalStateException e) {
                LOGGER.error("Could not resolve entry", e);
            }
        });
    }

    private <T> Map<ResourceLocation, Set<ClientTagEntry<T>>> readMap(FileToIdConverter converter, ResourceManager resourceManager, KeyResolver<T> keyResolver) {
        ImmutableMap.Builder<ResourceLocation, Set<ClientTagEntry<T>>> builder = ImmutableMap.builder();
        for (Map.Entry<ResourceLocation, List<Resource>> entry : converter.listMatchingResourceStacks(resourceManager).entrySet()) {
            ResourceLocation id = converter.fileToId(entry.getKey());
            try {
                builder.put(id, readResources(id, entry.getValue(), keyResolver));
            } catch (IllegalStateException e) {
                LOGGER.error("Failed to parse client tag", e);
            }
        }
        return builder.build();
    }

    private <T> Set<ClientTagEntry<T>> readResources(ResourceLocation fileName, List<Resource> resourceStack, KeyResolver<T> keyResolver) {
        ImmutableSet.Builder<ClientTagEntry<T>> setBuilder = ImmutableSet.builder();
        for (Resource resource : resourceStack) {
            try (BufferedReader reader = resource.openAsReader()) {
                JsonObject json = GsonHelper.parse(reader);
                Optional<ClientTagFile<T>> result = Services.PLATFORM_HELPER.decodeWithConditions(ClientTagFile.codec(keyResolver), json);
                if (result.isEmpty()) {
                    LOGGER.debug("Skipping loading client tag {} as its conditions were not met", fileName);
                    continue;
                }
                ClientTagFile<T> tagFile = result.get();
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

    private <T> void resolveEntry(ResourceLocation id, TagSetEntry<T> tagSetEntry, KeyResolver<T> resolver) {
        ClientTagKey<T> key = ClientTagKey.of(resolver, id);
        Set<T> set;
        Set<T> optionalSet;
        if (resolver instanceof KeyResolver.RegistryWrapper<T>) {
            set = new ReferenceLinkedOpenHashSet<>();
            optionalSet = new ReferenceLinkedOpenHashSet<>();
        } else {
            set = new ObjectLinkedOpenHashSet<>();
            optionalSet = new ObjectLinkedOpenHashSet<>();
        }

        for (ClientTagEntry<T> tagEntry : tagSetEntry.entries()) {
            if (tagEntry.isTag()) {
                ClientTagHolder<T> holder = getExistingHolder(tagEntry.element().right().orElseThrow());
                if (holder != null && holder.backingSet != null) {
                    holder.mergeInto(set, optionalSet);
                } else if (tagEntry.isRequired()) {
                    throw new IllegalStateException("Could not get required tag \"%s\" for \"%s\"".formatted(tagEntry.getTag(), id));
                }
            } else {
                T element = tagEntry.getElement();
                if (element == null) {
                    if (tagEntry.isRequired()) {
                        throw new IllegalStateException("Could not parse required element \"%s\" for \"%s\"".formatted(tagEntry.getElement(), id));
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

    record TagSetEntry<T>(Set<ClientTagEntry<T>> entries) implements DependencySorter.Entry<ResourceLocation> {

        @Override
        public void visitRequiredDependencies(Consumer<ResourceLocation> visitor) {
            entries.forEach(tagEntry -> {
                if (tagEntry.isRequired()) {
                    tagEntry.element().ifRight(ClientTagKey::getTagId);
                }
            });
        }

        @Override
        public void visitOptionalDependencies(Consumer<ResourceLocation> visitor) {
            entries.forEach(tagEntry -> {
                if (!tagEntry.isRequired()) {
                    tagEntry.element().ifRight(ClientTagKey::getTagId);
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
                throw new IllegalStateException("Cannot access tag set for " + key + " because it doesn't exist!");
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
