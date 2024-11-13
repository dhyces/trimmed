package dev.dhyces.trimmed.impl.client.models.override.provider;

import dev.dhyces.trimmed.api.TrimmedReference;
import dev.dhyces.trimmed.api.client.override.provider.types.AnyTrimItemOverrideProvider;
import dev.dhyces.trimmed.api.client.override.provider.types.ComponentItemOverrideProvider;

public interface ItemOverrideProviders {
    static void bootstrap() {
        ItemOverrideProviderRegistry.register(TrimmedReference.id("component"), ComponentItemOverrideProvider.CODEC);
        ItemOverrideProviderRegistry.register(TrimmedReference.id("any_trim"), AnyTrimItemOverrideProvider.CODEC);
    }
}
