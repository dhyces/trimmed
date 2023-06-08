package dhyces.trimmed.impl.client.tags.manager;

import dhyces.modhelper.services.Services;
import dhyces.trimmed.impl.client.tags.ClientRegistryTagKey;
import dhyces.trimmed.impl.util.OptionalId;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class RegistryTagHandler<V> extends BaseTagHandler<ClientRegistryTagKey<V>, V> {

    private final ResourceKey<? extends Registry<V>> registry;

    public RegistryTagHandler(ResourceKey<? extends Registry<V>> registry) {
        this.registry = registry;
    }

    @Override
    protected ClientRegistryTagKey<V> createTag(ResourceLocation tagId) {
        return ClientRegistryTagKey.of(registry, tagId);
    }

    @Override
    protected V createValue(OptionalId value) {
        return Services.PLATFORM_HELPER.getRegistryValue(registry, value.elementId());
    }
}