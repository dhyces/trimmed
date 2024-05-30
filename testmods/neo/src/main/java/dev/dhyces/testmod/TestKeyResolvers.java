package dev.dhyces.testmod;

import dev.dhyces.testmod.registry.custom.CustomObj;
import dev.dhyces.testmod.registry.custom.CustomRegistration;
import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.client.TrimmedClientMapApi;

public class TestKeyResolvers {
    public static final KeyResolver.RegistryWrapper<CustomObj> CUSTOM_OBJ = KeyResolver.RegistryWrapper.createStatic(CustomRegistration.CUSTOM_REGISTRY);
}
