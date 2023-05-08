package dhyces.trimmed.impl;

import dhyces.trimmed.api.TrimmedApi;
import dhyces.trimmed.api.TrimmedTagApi;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProviderType;
import dhyces.trimmed.impl.client.override.provider.ItemOverrideProviderRegistry;
import dhyces.trimmed.impl.client.tags.ClientRegistryTagKey;
import dhyces.trimmed.impl.client.tags.ClientTagKey;
import dhyces.trimmed.impl.client.tags.manager.ClientTagManager;
import dhyces.trimmed.impl.client.tags.manager.DatapackTagHandler;
import dhyces.trimmed.impl.client.tags.manager.RegistryTagHandler;
import dhyces.trimmed.impl.client.tags.manager.UncheckedTagHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Function;

public final class TrimmedApiImpl implements TrimmedApi {
    @Override
    public <T extends ItemOverrideProvider> ItemOverrideProviderType<T> registerItemOverrideType(ResourceLocation id, ItemOverrideProviderType<T> providerType) {
        ItemOverrideProviderRegistry.register(id, providerType);
        return providerType;
    }
}