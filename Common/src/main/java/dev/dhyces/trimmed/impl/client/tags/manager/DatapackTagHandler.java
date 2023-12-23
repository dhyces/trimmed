package dev.dhyces.trimmed.impl.client.tags.manager;

import com.mojang.serialization.DataResult;
import dev.dhyces.trimmed.Trimmed;
import dev.dhyces.trimmed.impl.client.tags.ClientRegistryTagKey;
import dev.dhyces.trimmed.impl.util.OptionalId;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class DatapackTagHandler<V> extends BaseTagHandler<ClientRegistryTagKey<V>, Holder<V>> {

    private final Map<ClientRegistryTagKey<V>, Set<OptionalId>> intermediate = new HashMap<>();
    private final ResourceKey<? extends Registry<V>> registryKey;
    private RegistryAccess registryAccess;

    public DatapackTagHandler(ResourceKey<? extends Registry<V>> registry) {
        this.registryKey = registry;
    }

    void update(RegistryAccess registryAccess) {
        this.registryAccess = registryAccess;
        clear();
        Optional<Registry<V>> datapackRegistryOptional = registryAccess.registry(registryKey);
        if (datapackRegistryOptional.isEmpty()) {
            Trimmed.LOGGER.error("Datapack registry " + registryKey.location() + " does not exist or is not synced to client!");
            return;
        }
        for (Map.Entry<ClientRegistryTagKey<V>, Set<OptionalId>> entry : intermediate.entrySet()) {
            try {
                Set<Holder<V>> linkedSet = new LinkedHashSet<>();
                for (OptionalId tagEntry : entry.getValue()) {
                    Holder<V> holder = createValue(tagEntry);
                    if (holder == null) {
                        if (tagEntry.isRequired()) {
                            throw new RuntimeException("Datapack element %s does not exist! Failed to load %s".formatted(tagEntry, entry.getKey()));
                        }
                    } else {
                        linkedSet.add(holder);
                    }
                }
                registeredTags.put(entry.getKey(), Collections.unmodifiableSet(linkedSet));
            } catch (RuntimeException e) {
                Trimmed.LOGGER.error(e.getMessage());
            }
        }
        isLoaded = true;
    }

    @Override
    void resolveTags(Map<ResourceLocation, Set<TagEntry>> unresolvedTags) {
        for (Map.Entry<ResourceLocation, Set<TagEntry>> entry : unresolvedTags.entrySet()) {
            DataResult<Set<OptionalId>> dataResult = resolveTag(unresolvedTags, intermediate, entry.getKey(), this::createTag, Function.identity(), new LinkedHashSet<>());
            dataResult.error().ifPresent(setPartialResult -> Trimmed.LOGGER.error(setPartialResult.message()));
        }
    }

    @Override
    protected ClientRegistryTagKey<V> createTag(ResourceLocation tagId) {
        return ClientRegistryTagKey.of(registryKey, tagId);
    }

    @Nullable
    @Override
    protected Holder<V> createValue(OptionalId value) {
        return registryAccess.registry(registryKey).flatMap(vs -> vs.getHolder(ResourceKey.create(registryKey, value.elementId()))).orElse(null);
    }
}
