package dev.dhyces.trimmed.api.client;

import dev.dhyces.trimmed.Trimmed;
import dev.dhyces.trimmed.impl.client.maps.MapKey;
import net.minecraft.resources.ResourceLocation;

public class ClientMapKeys {
    public static final MapKey<ResourceLocation, String> TRIM_MATERIALS = MapKey.of(ClientMapTypes.MATERIAL_SUFFIXES, Trimmed.id("material_suffixes"));
    public static final MapKey<ResourceLocation, String> DARKER_TRIM_MATERIALS = TRIM_MATERIALS.makeSubKey("darker_material_suffixes");
}
