package dev.dhyces.trimmed.api.client;

import dev.dhyces.trimmed.api.maps.MapKeyResolver;
import net.minecraft.resources.ResourceLocation;

public final class ClientMapKeyResolvers {
    private ClientMapKeyResolvers() {}

    public static final MapKeyResolver<ResourceLocation> TEXTURE = new ClientMapKeyResolver<>(ResourceLocation.CODEC);
}
