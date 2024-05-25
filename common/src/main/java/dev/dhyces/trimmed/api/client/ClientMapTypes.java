package dev.dhyces.trimmed.api.client;

import com.mojang.serialization.Codec;
import dev.dhyces.trimmed.api.maps.types.MapType;
import net.minecraft.resources.ResourceLocation;

// Map resolvers resolve the key object and directory. Map types hold the resolver for the key type and the value codec.
//  Map types can either be independent or "grouped" (under a named subdirectory, which then permits the "append" field).
//
public final class ClientMapTypes {
    private ClientMapTypes() {}

    public static final MapType<ResourceLocation, String> MATERIAL_SUFFIXES = MapType.simpleBuilder(ClientMapKeyResolvers.TEXTURE, Codec.STRING).build();
}
