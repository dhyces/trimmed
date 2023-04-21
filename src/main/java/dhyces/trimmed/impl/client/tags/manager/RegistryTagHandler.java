package dhyces.trimmed.impl.client.tags.manager;

import dhyces.trimmed.impl.client.tags.ClientRegistryTagKey;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryManager;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Stream;

public class RegistryTagHandler<V> extends BaseTagHandler<ClientRegistryTagKey<V>, V, V> {

    private final ResourceKey<Registry<V>> registry;

    public RegistryTagHandler(ResourceKey<Registry<V>> registry) {
        this.registry = registry;
    }

    @Override
    public boolean contains(ClientRegistryTagKey<V> tag) {
        return registeredTags.containsKey(tag);
    }

    @Override
    public boolean doesTagContain(ClientRegistryTagKey<V> tag, V value) {
        return registeredTags.getOrDefault(tag, Set.of()).contains(value);
    }

    @Override
    public Stream<V> streamValues(ClientRegistryTagKey<V> tag) {
        return registeredTags.getOrDefault(tag, Set.of()).stream();
    }

    @Nullable
    @Override
    public Set<V> getSet(ClientRegistryTagKey<V> tag) {
        return registeredTags.get(tag);
    }

    @Override
    protected ClientRegistryTagKey<V> createTag(ResourceLocation tagId) {
        return ClientRegistryTagKey.of(registry, tagId);
    }

    @Override
    protected V createValue(ResourceLocation valueId) {
        return RegistryManager.ACTIVE.getRegistry(registry).getValue(valueId);
    }
}
