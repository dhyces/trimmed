package dev.dhyces.testmod.client.providers;

import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProviderType;

public class MyProviderTypes {
    public static final ItemOverrideProviderType<BlockStateItemOverrideProvider> BLOCK_STATE = () -> BlockStateItemOverrideProvider.CODEC;
}
