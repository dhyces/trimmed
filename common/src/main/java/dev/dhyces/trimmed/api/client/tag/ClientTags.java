package dev.dhyces.trimmed.api.client.tag;

import dev.dhyces.trimmed.Trimmed;
import dev.dhyces.trimmed.api.client.map.ClientKeyResolvers;
import dev.dhyces.trimmed.impl.client.tags.ClientTagKey;
import net.minecraft.resources.ResourceLocation;

public final class ClientTags {
    private ClientTags() {}

    public static final ClientTagKey<ResourceLocation> TRIM_ITEM_TEXTURES = ClientTagKey.of(ClientKeyResolvers.TEXTURE, Trimmed.id("trim_item_overlays"));
    public static final ClientTagKey<ResourceLocation> TRIM_PATTERN_TEXTURES = ClientTagKey.of(ClientKeyResolvers.TEXTURE, Trimmed.id("trim_armor_patterns"));
}
