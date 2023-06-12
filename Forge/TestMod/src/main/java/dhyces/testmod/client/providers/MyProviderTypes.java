package dhyces.testmod.client.providers;

import dhyces.testmod.TrimmedTest;
import dhyces.trimmed.api.TrimmedClientApi;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProviderType;

public class MyProviderTypes {
    public static final ItemOverrideProviderType<BlockStateItemOverrideProvider> BLOCK_STATE = TrimmedClientApi.INSTANCE.registerItemOverrideType(TrimmedTest.id("block_state"), () -> BlockStateItemOverrideProvider.CODEC);

    public static void init() {}
}
