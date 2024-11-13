package dev.dhyces.trimmed.impl.client.services;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.dhyces.trimmed.api.client.models.source.ModelSource;
import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dev.dhyces.trimmed.api.services.InternalCodecs;
import dev.dhyces.trimmed.impl.client.models.override.provider.ItemOverrideProviderRegistry;
import dev.dhyces.trimmed.impl.client.models.source.ModelSourceRegistry;

public class InternalCodecsImpl implements InternalCodecs {
    @Override
    public Codec<ModelSource> getModelSourceRegistryCodec() {
        return ModelSourceRegistry.CODEC;
    }

    @Override
    public Codec<MapCodec<? extends ItemOverrideProvider>> getItemOverrideProviderRegistryCodec() {
        return ItemOverrideProviderRegistry.CODEC;
    }
}
