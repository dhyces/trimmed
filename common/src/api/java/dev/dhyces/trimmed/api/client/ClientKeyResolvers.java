package dev.dhyces.trimmed.api.client;

import dev.dhyces.trimmed.api.KeyResolver;
import net.minecraft.resources.ResourceLocation;

public final class ClientKeyResolvers {
    private ClientKeyResolvers() {}

    public static final KeyResolver<ResourceLocation> TEXTURE = new ClientKeyResolver<>((resourceLocation, dynamicOps) -> resourceLocation);
}
