package dev.dhyces.trimmed.api.client;

import dev.dhyces.trimmed.Trimmed;
import dev.dhyces.trimmed.impl.client.maps.ClientMapKey;
import net.minecraft.resources.ResourceLocation;

public class ClientMapKeys {
    public static final ClientMapKey<ResourceLocation, ResourceLocation> DARKER_TRIM_MATERIALS = ClientMapKey.of(ClientMapTypes.TRIM_MATERIAL_PERMUTATIONS, Trimmed.id("darker_trim_materials"));
    public static final ClientMapKey<ResourceLocation, ResourceLocation> TRIM_MATERIALS = ClientMapKey.of(ClientMapTypes.TRIM_MATERIAL_PERMUTATIONS, Trimmed.id("trim_materials"));
}
