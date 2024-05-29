package dev.dhyces.trimmed.impl.client.tags.manager;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import dev.dhyces.trimmed.api.client.tag.TagHolder;
import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.impl.client.maps.KeyResolvers;
import dev.dhyces.trimmed.impl.client.tags.ClientTagKey;
import dev.dhyces.trimmed.impl.mixin.TagEntryAccessor;
import dev.dhyces.trimmed.modhelper.services.Services;
import dev.dhyces.trimmed.Trimmed;
import dev.dhyces.trimmed.api.util.Utils;
import dev.dhyces.trimmed.impl.client.tags.ClientTagFile;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.Util;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagEntry;
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
import java.util.function.Function;

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

    private CompletableFuture<Unit> load(ResourceManager resourceManager) {
        REGISTRY.values().forEach(ClientTagHolder::reset);

        for (Map.Entry<ResourceLocation, KeyResolver<?>> entry : KeyResolvers.getEntries()) {
            String resolverPath = PATH + Utils.namespacedPath(entry.getKey(), '/');
            FileToIdConverter converter = FileToIdConverter.json(resolverPath);
            Map<ResourceLocation, Set<TagEntry>> unresolved = readMap(converter, resourceManager);
            DependencySorter<ResourceLocation, TagSetEntry> sorter = new DependencySorter<>();
            unresolved.forEach((resourceLocation, entries) -> sorter.addEntry(resourceLocation, new TagSetEntry(entries)));
            sorter.orderByDependencies((resourceLocation, tagEntrySet) ->
                    resolveEntry(resourceLocation, tagEntrySet, entry.getValue())
            );
        }

//        final Collection<PathInfo> foldersToSearch = PathInfo.gatherAllInfos(ClientUtil.getRegistryAccess());

//        for (PathInfo pathInfo : foldersToSearch) {
//            FileToIdConverter converter = FileToIdConverter.json("trimmed/tags/" + pathInfo.getPath());
//            Map<ResourceLocation, Set<TagEntry>> unresolved = readMap(converter, resourceManager);
//
//            if (!(pathInfo instanceof RegistryPathInfo registryPathInfo)) {
//                UNCHECKED_HANDLER.resolveTags(unresolved);
//            } else {
//                final ResourceKey<? extends Registry<?>> key = registryPathInfo.resourceKey();
//
//                if (registryPathInfo.registryType() == RegistryType.STATIC) {
//                    REGISTRY_HANDLERS.computeIfAbsent(key, resourceKey -> new RegistryTagHandler<>(registryPathInfo.castRegistryKey())).resolveTags(unresolved);
//                } else {
//                    REGISTRY.computeIfAbsent(key, resourceKey -> new DatapackTagHandler<>(registryPathInfo.castRegistryKey())).resolveTags(unresolved);
//                }
//            }
//        }

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
                    LOGGER.debug("Skipping loading client tag {} as its conditions were not met", fileName);
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

    private <T> void resolveEntry(ResourceLocation id, TagSetEntry tagSetEntry, KeyResolver<T> resolver) {
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

        for (TagEntry tagEntry : tagSetEntry.entries()) {
            TagEntryAccessor accessor = (TagEntryAccessor) tagEntry;
            if (accessor.isTag()) {
                ClientTagHolder<T> holder = getExistingHolder(ClientTagKey.of(resolver, accessor.getId()));
                if (holder != null && holder.backingSet != null) {
                    holder.mergeInto(set, optionalSet);
                } else if (accessor.isRequired()) {
                    throw new IllegalStateException("Could not get required tag \"%s\" for \"%s\"".formatted(accessor.getId(), id));
                }
            } else {
                T element = resolver.getCodec().parse(JsonOps.INSTANCE, new JsonPrimitive(accessor.getId().toString())).mapOrElse(Function.identity(), tError -> null);
                if (element == null) {
                    if (accessor.isRequired()) {
                        throw new IllegalStateException("Could not parse required element \"%s\" for \"%s\"".formatted(accessor.getId(), id));
                    }
                } else {
                    set.add(element);
                    if (!accessor.isRequired()) {
                        optionalSet.add(element);
                    }
                }
            }
        }
        getOrCreateHolder(key).update(set, optionalSet);
    }

    record TagSetEntry(Set<TagEntry> entries) implements DependencySorter.Entry<ResourceLocation> {

        @Override
        public void visitRequiredDependencies(Consumer<ResourceLocation> visitor) {
            entries.forEach(tagEntry -> tagEntry.visitRequiredDependencies(visitor));
        }

        @Override
        public void visitOptionalDependencies(Consumer<ResourceLocation> visitor) {
            entries.forEach(tagEntry -> tagEntry.visitOptionalDependencies(visitor));
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
