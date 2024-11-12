package dev.dhyces.trimmed.api.client.map;

import dev.dhyces.trimmed.api.TrimmedReference;
import dev.dhyces.trimmed.api.maps.MapKey;
import net.minecraft.resources.ResourceLocation;

public class ClientMapKeys {
    public static final MapKey<ResourceLocation, String> MATERIAL_SUFFIXES = MapKey.baseKeyOf(ClientMapTypes.TEXTURE_SUFFIX, TrimmedReference.id("material_suffixes"));
    public static final MapKey<ResourceLocation, String> DARKER_MATERIAL_SUFFIXES = MATERIAL_SUFFIXES.makeSubKey(TrimmedReference.id("darker_material_suffixes"));

    public static final MapKey<ResourceLocation, ResourceLocation> TRIM_MATERIAL_OVERRIDES = MapKey.baseKeyOf(ClientMapTypes.TEXTURE_MAPPING, TrimmedReference.id("trim_material_overrides"));
    public static final MapKey<ResourceLocation, ResourceLocation> IRON_MATERIAL_OVERRIDES = TRIM_MATERIAL_OVERRIDES.makeSubKey(TrimmedReference.id("iron"));
    public static final MapKey<ResourceLocation, ResourceLocation> GOLD_MATERIAL_OVERRIDES = TRIM_MATERIAL_OVERRIDES.makeSubKey(TrimmedReference.id("gold"));
    public static final MapKey<ResourceLocation, ResourceLocation> DIAMOND_MATERIAL_OVERRIDES = TRIM_MATERIAL_OVERRIDES.makeSubKey(TrimmedReference.id("diamond"));
    public static final MapKey<ResourceLocation, ResourceLocation> NETHERITE_MATERIAL_OVERRIDES = TRIM_MATERIAL_OVERRIDES.makeSubKey(TrimmedReference.id("netherite"));
    
    public static final MapKey<ResourceLocation, ResourceLocation> TRIM_OVERLAYS = MapKey.baseKeyOf(ClientMapTypes.TEXTURE_MAPPING, TrimmedReference.id("trim_overlays"));
    public static final MapKey<ResourceLocation, ResourceLocation> IRON_ARMOR_OVERLAYS = TRIM_OVERLAYS.makeSubKey(TrimmedReference.id("iron"));
    public static final MapKey<ResourceLocation, ResourceLocation> GOLD_ARMOR_OVERLAYS = TRIM_OVERLAYS.makeSubKey(TrimmedReference.id("gold"));
    public static final MapKey<ResourceLocation, ResourceLocation> DIAMOND_ARMOR_OVERLAYS = TRIM_OVERLAYS.makeSubKey(TrimmedReference.id("diamond"));
    public static final MapKey<ResourceLocation, ResourceLocation> NETHERITE_ARMOR_OVERLAYS = TRIM_OVERLAYS.makeSubKey(TrimmedReference.id("netherite"));
    public static final MapKey<ResourceLocation, ResourceLocation> LEATHER_ARMOR_OVERLAYS = TRIM_OVERLAYS.makeSubKey(TrimmedReference.id("leather"));
    public static final MapKey<ResourceLocation, ResourceLocation> CHAINMAIL_ARMOR_OVERLAYS = TRIM_OVERLAYS.makeSubKey(TrimmedReference.id("chainmail"));
    public static final MapKey<ResourceLocation, ResourceLocation> TURTLE_ARMOR_OVERLAYS = TRIM_OVERLAYS.makeSubKey(TrimmedReference.id("turtle"));
}
