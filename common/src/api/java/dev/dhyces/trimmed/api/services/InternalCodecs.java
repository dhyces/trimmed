package dev.dhyces.trimmed.api.services;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.dhyces.trimmed.api.client.models.source.ModelSource;
import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;

public interface InternalCodecs {
    Codec<ModelSource> getModelSourceRegistryCodec();
    Codec<MapCodec<? extends ItemOverrideProvider>> getItemOverrideProviderRegistryCodec();
}
