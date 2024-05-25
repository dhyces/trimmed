package dev.dhyces.trimmed.api.client;

import dev.dhyces.trimmed.Trimmed;
import dev.dhyces.trimmed.impl.client.tags.ClientTagKey;

public class UncheckedClientTags {
    public static final ClientTagKey ALL_TRIM_ITEM_TEXTURES = ClientTagKey.of(Trimmed.id("all_armor_trim_item_textures"));
    public static final ClientTagKey ALL_TRIM_PATTERN_TEXTURES = ClientTagKey.of(Trimmed.id("all_armor_trim_pattern_textures"));
    public static final ClientTagKey CUSTOM_TRIM_ITEM_TEXTURES = ClientTagKey.of(Trimmed.id("custom_armor_trim_item_textures"));
    public static final ClientTagKey CUSTOM_TRIM_PATTERN_TEXTURES = ClientTagKey.of(Trimmed.id("custom_armor_trim_pattern_textures"));
    public static final ClientTagKey VANILLA_TRIM_ITEM_TEXTURES = ClientTagKey.of(Trimmed.id("vanilla_armor_trim_item_textures"));
    public static final ClientTagKey VANILLA_TRIM_PATTERN_TEXTURES = ClientTagKey.of(Trimmed.id("vanilla_armor_trim_pattern_textures"));
}
