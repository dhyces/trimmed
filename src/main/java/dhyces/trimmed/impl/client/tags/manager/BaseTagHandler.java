package dhyces.trimmed.impl.client.tags.manager;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.DataResult;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.impl.client.tags.ClientTagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Responsible for handling tag maps in the ClientTagManager.
 * K: Key, I: Intermediate, V: Value (parsed, finished, ready, loaded value)
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

    protected abstract V createValue(ResourceLocation valueId);

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

    static <KEY, VAL> DataResult<Set<VAL>> resolveTag(Map<ResourceLocation, Set<TagEntry>> unresolvedTags, Map<KEY, Set<VAL>> resolvedTags, ResourceLocation tagId, Function<ResourceLocation, KEY> keyFactory, Function<ResourceLocation, VAL> valueFactory, LinkedHashSet<ResourceLocation> resolutionSet) {
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
            if (entry.isTag()) {
                DataResult<Set<VAL>> result = resolveTag(unresolvedTags, resolvedTags, entry.getId(), keyFactory, valueFactory, resolutionSet);
                if (result.error().isPresent()) {
                    return result;
                }
                result.result().ifPresent(builder::addAll);
            } else {
                builder.add(valueFactory.apply(entry.getId()));
            }
        }

        // Adds it to the registered map and returns the set
        return DataResult.success(resolvedTags.computeIfAbsent(key, k -> builder.build()));
    }
}
