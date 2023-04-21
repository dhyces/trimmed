package dhyces.trimmed.impl.client.tags.manager;

import dhyces.trimmed.Trimmed;
import dhyces.trimmed.impl.client.tags.ClientRegistryTagKey;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class DatapackTagHandler<V> extends BaseTagHandler<ClientRegistryTagKey<V>, ResourceLocation, Holder<V>> {

    private final Map<ClientRegistryTagKey<V>, Set<Holder<V>>> loaded = new HashMap<>();
    private final ResourceKey<Registry<V>> registryKey;
    private boolean needsClientReload;

    public DatapackTagHandler(ResourceKey<Registry<V>> registry) {
        this.registryKey = registry;
    }

    @Override
    public boolean contains(ClientRegistryTagKey<V> tag) {
        return loaded.containsKey(tag);
    }

    @Override
    public boolean doesTagContain(ClientRegistryTagKey<V> tag, Holder<V> value) {
        return loaded.getOrDefault(tag, Set.of()).contains(value);
    }

    @Override
    public Stream<Holder<V>> streamValues(ClientRegistryTagKey<V> tag) {
        return loaded.getOrDefault(tag, Set.of()).stream();
    }

    @Nullable
    @Override
    public Set<Holder<V>> getSet(ClientRegistryTagKey<V> tag) {
        return loaded.get(tag);
    }

    @Override
    void clear() {
        super.clear();
        loaded.clear();
    }

    public boolean isReliableData() {
        return !needsClientReload;
    }

    void update(RegistryAccess registryAccess) {
        Optional<Registry<V>> datapackRegistryOptional = registryAccess.registry(registryKey);
        if (datapackRegistryOptional.isEmpty()) {
            Trimmed.LOGGER.error("Datapack registry " + registryKey.location() + " does not exist or is not synced to client!");
            return;
        }
        for (Map.Entry<ClientRegistryTagKey<V>, Set<ResourceLocation>> entry : registeredTags.entrySet()) {
            Set<Holder<V>> linkedSet = new LinkedHashSet<>();
            for (ResourceLocation id : entry.getValue()) {
                datapackRegistryOptional.get().getHolder(ResourceKey.create(registryKey, id)).ifPresent(linkedSet::add);
            }
            loaded.put(entry.getKey(), Collections.unmodifiableSet(linkedSet));
        }
        needsClientReload = true;
    }

    @Override
    protected ClientRegistryTagKey<V> createTag(ResourceLocation tagId) {
        return ClientRegistryTagKey.of(registryKey, tagId);
    }

    @Override
    protected ResourceLocation createValue(ResourceLocation valueId) {
        return valueId;
    }

}
