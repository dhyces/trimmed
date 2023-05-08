package dhyces.trimmed.impl.client.tags.manager;

import com.mojang.serialization.DataResult;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.impl.client.tags.ClientRegistryTagKey;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class DatapackTagHandler<V> extends BaseTagHandler<ClientRegistryTagKey<V>, Holder<V>> {

    private final Map<ClientRegistryTagKey<V>, Set<ResourceLocation>> intermediate = new HashMap<>();
    private final ResourceKey<Registry<V>> registryKey;
    private RegistryAccess registryAccess;

    public DatapackTagHandler(ResourceKey<Registry<V>> registry) {
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
        for (Map.Entry<ClientRegistryTagKey<V>, Set<ResourceLocation>> entry : intermediate.entrySet()) {
            Set<Holder<V>> linkedSet = new LinkedHashSet<>();
            for (ResourceLocation id : entry.getValue()) {
                Holder<V> holder = createValue(id);
                if (holder == null) {
                    // TODO: DO SOMETHING, LOG OR THROW
                }
                linkedSet.add(holder);
            }
            registeredTags.put(entry.getKey(), Collections.unmodifiableSet(linkedSet));
        }
        isLoaded = true;
    }

    @Override
    void resolveTags(Map<ResourceLocation, Set<TagEntry>> unresolvedTags) {
        for (Map.Entry<ResourceLocation, Set<TagEntry>> entry : unresolvedTags.entrySet()) {
            DataResult<Set<ResourceLocation>> dataResult = resolveTag(unresolvedTags, intermediate, entry.getKey(), this::createTag, Function.identity(), new LinkedHashSet<>());
            dataResult.error().ifPresent(setPartialResult -> Trimmed.LOGGER.error(setPartialResult.message()));
        }
    }

    @Override
    protected ClientRegistryTagKey<V> createTag(ResourceLocation tagId) {
        return ClientRegistryTagKey.of(registryKey, tagId);
    }

    @Nullable
    @Override
    protected Holder<V> createValue(ResourceLocation valueId) {
        return registryAccess.registry(registryKey).flatMap(vs -> vs.getHolder(ResourceKey.create(registryKey, valueId))).orElse(null);
    }
}
