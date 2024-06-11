package dev.dhyces.testmod;

import dev.dhyces.testmod.registry.custom.CustomObj;
import dev.dhyces.testmod.registry.custom.CustomRegistration;
import dev.dhyces.trimmed.api.KeyResolver;

public class TestKeyResolvers {
    public static final KeyResolver.Static<CustomObj> CUSTOM_OBJ = new KeyResolver.Static<>(CustomRegistration.CUSTOM_REGISTRY);
}
