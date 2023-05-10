package dhyces.trimmed.impl.client.tags.manager;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.DataResult;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.impl.client.tags.ClientTagKey;
import dhyces.trimmed.impl.util.OptionalId;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Responsible for handling tag maps in the ClientTagManager.
 */
abstract class BaseTagHandler<K, V> {
    protected final Map<K, Set<V>> registeredTags = new HashMap<>();
    protected boolean isLoaded;

    public boolean contains(K tag) {
        return registeredTags.containsKey(tag);
    }

    public boolean doesTagContain(K tag, V value) {
        return registeredTags.getOrDefault(tag, Set.of()).contains(value);
    }

    public Stream<V> streamValues(ClientTagKey tag) {
        return registeredTags.getOrDefault(tag, Set.of()).stream();
    }

    @Nullable
    public Set<V> getSet(K tag) {
        return registeredTags.get(tag);
    }

    public boolean hasLoaded() {
        return isLoaded;
    }

    protected abstract K createTag(ResourceLocation tagId);

    protected abstract V createValue(OptionalId value);

    void clear() {
        registeredTags.clear();
        isLoaded = false;
    }

    void resolveTags(Map<ResourceLocation, Set<TagEntry>> unresolvedTags) {
        for (Map.Entry<ResourceLocation, Set<TagEntry>> entry : unresolvedTags.entrySet()) {
            DataResult<Set<V>> dataResult = resolveTag(unresolvedTags, registeredTags, entry.getKey(), this::createTag, this::createValue, new LinkedHashSet<>());
            dataResult.error().ifPresent(setPartialResult -> Trimmed.LOGGER.error(setPartialResult.message()));
        }
        isLoaded = true;
    }

    protected final <KEY, VAL> DataResult<Set<VAL>> resolveTag(Map<ResourceLocation, Set<TagEntry>> unresolvedTags, Map<KEY, Set<VAL>> resolvedTags, ResourceLocation tagId, Function<ResourceLocation, KEY> keyFactory, Function<OptionalId, VAL> valueFactory, LinkedHashSet<ResourceLocation> resolutionSet) {
        KEY key = keyFactory.apply(tagId);
        if (resolvedTags.containsKey(key)) {
            return DataResult.success(resolvedTags.get(key));
        }

        if (resolutionSet.contains(tagId)) {
            return DataResult.error(() -> "ClientTag cycle detected! " + resolutionSet.stream().map(ResourceLocation::toString).toList());
        }

        resolutionSet.add(tagId);

        ImmutableSet.Builder<VAL> builder = ImmutableSet.builder();

        Set<TagEntry> entries = unresolvedTags.get(tagId);

        if (entries == null) {
            return DataResult.error(() -> "Tag " + tagId + " does not exist!");
        }

        for (TagEntry entry : entries) {
            DataResult<Set<VAL>> entryResult = resolveTagEntry(unresolvedTags, resolvedTags, entry, keyFactory, valueFactory, resolutionSet);
            if (entryResult.error().isPresent()) {
                return entryResult;
            }
            builder.addAll(entryResult.result().get());
        }

        // Adds it to the registered map and returns the set
        return DataResult.success(resolvedTags.computeIfAbsent(key, k -> builder.build()));
    }

    protected <KEY, VAL> DataResult<Set<VAL>> resolveTagEntry(Map<ResourceLocation, Set<TagEntry>> unresolvedTags, Map<KEY, Set<VAL>> resolvedTags, TagEntry tagEntry, Function<ResourceLocation, KEY> keyFactory, Function<OptionalId, VAL> valueFactory, LinkedHashSet<ResourceLocation> resolutionSet) {
        if (tagEntry.isTag()) {
            if (!unresolvedTags.containsKey(tagEntry.getId()) && tagEntry.isRequired()) {
                return DataResult.error(() -> "Tag entry " + tagEntry.getId() + " is required, yet tag does not exist!");
            }
            return resolveTag(unresolvedTags, resolvedTags, tagEntry.getId(), keyFactory, valueFactory, resolutionSet);
        }
        VAL value = valueFactory.apply(OptionalId.from(tagEntry));
        if (value == null && tagEntry.isRequired()) {
            return DataResult.error(() -> "Tag entry " + tagEntry.getId() + " is required, yet element does not exist!");
        }
        return DataResult.success(value == null ? Set.of() : Set.of(value));
    }
}
