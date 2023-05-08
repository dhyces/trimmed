package dhyces.trimmed.impl.client.tags.manager;

import dhyces.trimmed.impl.client.tags.ClientRegistryTagKey;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryManager;

public class RegistryTagHandler<V> extends BaseTagHandler<ClientRegistryTagKey<V>, V> {

    private final ResourceKey<Registry<V>> registry;

    public RegistryTagHandler(ResourceKey<Registry<V>> registry) {
        this.registry = registry;
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
