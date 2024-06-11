package dev.dhyces.testmod.client;

import dev.dhyces.testmod.TestKeyResolvers;
import dev.dhyces.testmod.TrimmedTest;
import dev.dhyces.testmod.client.providers.MyProviderTypes;
import dev.dhyces.testmod.registry.custom.CustomRegistration;
import dev.dhyces.trimmed.api.TrimmedClientApiConsumer;
import dev.dhyces.trimmed.api.client.TrimmedClientApiEntrypoint;

@SuppressWarnings("unused")
@TrimmedClientApiConsumer(TrimmedTest.MODID)
public class TrimmedApiCompat implements TrimmedClientApiEntrypoint {
    @Override
    public void registration(TrimmedClientRegistration registration) {
        registration.registerItemOverrideType(TrimmedTest.id("block_state"), MyProviderTypes.BLOCK_STATE);
        registration.registerKeyResolver(CustomRegistration.KEY.location(), TestKeyResolvers.CUSTOM_OBJ);
        registration.registerKeyResolver(TrimmedTest.id("test"), TestClientKeyResolvers.TEST);
        registration.registerBaseMapKey(TestClientMapKeys.MANUAL_TEST_MAP);
        registration.registerBaseMapKey(TestClientMapKeys.MANUAL_SCANNER_DESCS);
        registration.registerBaseMapKey(TestClientMapKeys.MANUAL_TEST_ITEM_MAP);
        registration.registerBaseMapKey(TestClientMapKeys.MANUAL_TEST_BIOME_MAP);
        registration.registerBaseMapKey(TestClientMapKeys.DATAGEN_TEST_DAMAGE_TYPE_MAP);
        registration.registerBaseMapKey(TestClientMapKeys.DATAGEN_TEST_MAP_2);
        registration.registerBaseMapKey(TestClientMapKeys.DATAGEN_TEST_BLOCK_MAP);
        registration.registerBaseMapKey(TestClientMapKeys.DATAGEN_ENTITY_TRANSFORM);
    }
}
