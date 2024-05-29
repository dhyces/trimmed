package dev.dhyces.trimmed.api.client.map;

import dev.dhyces.trimmed.api.maps.KeyResolver;
import net.minecraft.resources.ResourceLocation;

public final class ClientKeyResolvers {
    private ClientKeyResolvers() {}

    public static final KeyResolver<ResourceLocation> TEXTURE = new ClientKeyResolver<>(ResourceLocation.CODEC);
}
