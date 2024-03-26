package dev.dhyces.trimmed.api.client;

import dev.dhyces.trimmed.api.maps.MapType;
import net.minecraft.resources.ResourceLocation;

public class ClientMapTypes {
    // TODO: Figure out registration. Probably make it separate from instantiation.
    public static final MapType<ResourceLocation, ResourceLocation> TRIM_MATERIAL_PERMUTATIONS = MapType.builder(ResourceLocation.CODEC, ResourceLocation.CODEC).build();
}
