package dev.dhyces.trimmed.impl.client.tags.manager;

import dev.dhyces.trimmed.impl.util.OptionalId;
import dev.dhyces.trimmed.modhelper.services.Services;
import dev.dhyces.trimmed.api.client.util.ClientUtil;
import dev.dhyces.trimmed.impl.client.tags.ClientRegistryTagKey;
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
        return Services.PLATFORM_HELPER.getRegistryValue(ClientUtil.getRegistryAccess(), registry, value.elementId());
    }
}
