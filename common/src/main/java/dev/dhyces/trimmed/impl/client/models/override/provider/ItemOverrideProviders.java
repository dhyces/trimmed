package dev.dhyces.trimmed.impl.client.models.override.provider;

import dev.dhyces.trimmed.api.TrimmedReference;
import dev.dhyces.trimmed.api.client.override.provider.providers.AnyTrimItemOverrideProvider;
import dev.dhyces.trimmed.api.client.override.provider.providers.ComponentItemOverrideProvider;

public interface ItemOverrideProviders {
    static void bootstrap() {
        ItemOverrideProviderRegistry.register(TrimmedReference.id("component"), ComponentItemOverrideProvider.CODEC);
        ItemOverrideProviderRegistry.register(TrimmedReference.id("any_trim"), AnyTrimItemOverrideProvider.CODEC);
    }
}
