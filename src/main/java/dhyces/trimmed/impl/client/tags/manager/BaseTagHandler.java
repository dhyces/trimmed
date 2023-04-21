package dhyces.trimmed.impl.client.tags.manager;

import com.google.common.collect.ImmutableSet;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.impl.client.tags.ClientTagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import org.codehaus.plexus.util.dag.CycleDetectedException;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

/**
 * Responsible for handling tag maps in the ClientTagManager.
 * K: Key, I: Intermediate, V: Value (parsed, finished, ready, loaded value)
 */
abstract class BaseTagHandler<K, I, V> {
    protected final Map<K, Set<I>> registeredTags = new HashMap<>();
    protected boolean isLoaded;

    public abstract boolean contains(K tag);

    public abstract boolean doesTagContain(K tag, V value);

    public abstract Stream<V> streamValues(K tag);

    @Nullable
    public abstract Set<V> getSet(K tag);

    public boolean hasLoaded() {
        return isLoaded;
    }

    protected abstract K createTag(ResourceLocation tagId);

    protected abstract I createValue(ResourceLocation valueId);

    void clear() {
        registeredTags.clear();
        isLoaded = false;
    }

    void resolveTags(Map<ResourceLocation, Set<TagEntry>> unresolvedTags) {
        for (Map.Entry<ResourceLocation, Set<TagEntry>> entry : unresolvedTags.entrySet()) {
            try {
                resolveTag(unresolvedTags, entry.getKey(), new LinkedHashSet<>());
            } catch (CycleDetectedException e) {
                Trimmed.LOGGER.error(e.getMessage());
            }
        }
        isLoaded = true;
    }

    Set<I> resolveTag(Map<ResourceLocation, Set<TagEntry>> unresolvedTags, ResourceLocation tagId, LinkedHashSet<ResourceLocation> resolutionSet) throws CycleDetectedException {
        K key = createTag(tagId);
        if (registeredTags.containsKey(key)) {
            return registeredTags.get(key);
        }

        if (resolutionSet.contains(tagId)) {
            throw new CycleDetectedException("ClientTag cycle detected! ", resolutionSet.stream().map(ResourceLocation::toString).toList());
        }

        resolutionSet.add(tagId);

        ImmutableSet.Builder<I> builder = ImmutableSet.builder();

        for (TagEntry entry : unresolvedTags.get(tagId)) {
            if (entry.isTag()) {
                builder.addAll(resolveTag(unresolvedTags, entry.getId(), resolutionSet));
            } else {
                builder.add(createValue(entry.getId()));
            }
        }

        // Adds it to the registered map and returns the set
        return registeredTags.computeIfAbsent(key, k -> builder.build());
    }
}
