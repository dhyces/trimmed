package dev.dhyces.trimmed.impl.client.atlas;

import dev.dhyces.trimmed.Trimmed;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public class TrimmedSpriteSourceTypes {
    public static final SpriteSourceType OPEN_PALETTED_PERMUTATIONS = new SpriteSourceType(OpenPalettedPermutations.CODEC);

    public static void bootstrap(BiConsumer<ResourceLocation, SpriteSourceType> registrar) {
        registrar.accept(Trimmed.id("open_paletted_permutations"), OPEN_PALETTED_PERMUTATIONS);
    }
}
