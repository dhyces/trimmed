package dev.dhyces.testmod.client;

import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.client.ClientKeyResolver;
import net.minecraft.resources.ResourceLocation;

public class TestClientKeyResolvers {
    public static final KeyResolver<ResourceLocation> TEST = new ClientKeyResolver<>(ResourceLocation.CODEC);
}
