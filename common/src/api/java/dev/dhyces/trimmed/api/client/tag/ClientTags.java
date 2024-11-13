package dev.dhyces.trimmed.api.client.tag;

import dev.dhyces.trimmed.api.TrimmedReference;
import dev.dhyces.trimmed.api.client.ClientKeyResolvers;
import net.minecraft.resources.ResourceLocation;

public final class ClientTags {
    private ClientTags() {}

    public static final ClientTagKey<ResourceLocation> TRIM_ITEM_TEXTURES = ClientTagKey.of(ClientKeyResolvers.TEXTURE, TrimmedReference.id("trim_item_overlays"));
    public static final ClientTagKey<ResourceLocation> ARMOR_TRIM_PATTERNS_TEXTURES = ClientTagKey.of(ClientKeyResolvers.TEXTURE, TrimmedReference.id("armor_trim_patterns"));
}
