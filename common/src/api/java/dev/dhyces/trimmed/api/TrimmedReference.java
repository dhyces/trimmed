package dev.dhyces.trimmed.api;

import net.minecraft.resources.ResourceLocation;

public final class TrimmedReference {
    private TrimmedReference() {}

    public static final String MODID = "trimmed";
    public static ResourceLocation id(String id) {
        return ResourceLocation.fromNamespaceAndPath(MODID, id);
    }

    public static final String OVERRIDES_DIRECTORY = "trimmed/item_model_overrides";
}
