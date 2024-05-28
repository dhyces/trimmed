package dev.dhyces.trimmed.api.client;

import dev.dhyces.trimmed.Trimmed;
import dev.dhyces.trimmed.api.maps.MapKey;
import net.minecraft.resources.ResourceLocation;

public class ClientMapKeys {
    public static final MapKey<ResourceLocation, String> MATERIAL_SUFFIXES = MapKey.of(ClientMapTypes.TEXTURE_SUFFIX, Trimmed.id("material_suffixes"));
    public static final MapKey<ResourceLocation, String> DARKER_MATERIAL_SUFFIXES = MATERIAL_SUFFIXES.makeSubKey("darker_material_suffixes");

    public static final MapKey<ResourceLocation, ResourceLocation> TRIM_MATERIAL_OVERRIDES = MapKey.of(ClientMapTypes.TEXTURE_MAPPING, Trimmed.id("trim_material_overrides"));
    public static final MapKey<ResourceLocation, ResourceLocation> IRON_MATERIAL_OVERRIDES = TRIM_MATERIAL_OVERRIDES.makeSubKey("iron");
    public static final MapKey<ResourceLocation, ResourceLocation> GOLD_MATERIAL_OVERRIDES = TRIM_MATERIAL_OVERRIDES.makeSubKey("gold");
    public static final MapKey<ResourceLocation, ResourceLocation> DIAMOND_MATERIAL_OVERRIDES = TRIM_MATERIAL_OVERRIDES.makeSubKey("diamond");
    public static final MapKey<ResourceLocation, ResourceLocation> NETHERITE_MATERIAL_OVERRIDES = TRIM_MATERIAL_OVERRIDES.makeSubKey("netherite");
    
    public static final MapKey<ResourceLocation, ResourceLocation> TRIM_OVERLAYS = MapKey.of(ClientMapTypes.TEXTURE_MAPPING, Trimmed.id("trim_overlays"));
    public static final MapKey<ResourceLocation, ResourceLocation> IRON_ARMOR_OVERLAYS = TRIM_OVERLAYS.makeSubKey("iron");
    public static final MapKey<ResourceLocation, ResourceLocation> GOLD_ARMOR_OVERLAYS = TRIM_OVERLAYS.makeSubKey("gold");
    public static final MapKey<ResourceLocation, ResourceLocation> DIAMOND_ARMOR_OVERLAYS = TRIM_OVERLAYS.makeSubKey("diamond");
    public static final MapKey<ResourceLocation, ResourceLocation> NETHERITE_ARMOR_OVERLAYS = TRIM_OVERLAYS.makeSubKey("netherite");
    public static final MapKey<ResourceLocation, ResourceLocation> LEATHER_ARMOR_OVERLAYS = TRIM_OVERLAYS.makeSubKey("leather");
    public static final MapKey<ResourceLocation, ResourceLocation> CHAINMAIL_ARMOR_OVERLAYS = TRIM_OVERLAYS.makeSubKey("chainmail");
    public static final MapKey<ResourceLocation, ResourceLocation> TURTLE_ARMOR_OVERLAYS = TRIM_OVERLAYS.makeSubKey("turtle");
}
