package dev.dhyces.trimmed.impl.client.atlas;

import com.mojang.serialization.MapCodec;
import dev.dhyces.trimmed.Trimmed;
import dev.dhyces.trimmed.impl.mixin.client.AtlasSourceManagerAccessor;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class TrimmedSpriteSourceTypes {
    private static SpriteSourceType openPalettedPermutationsRegistered;
    public static final Supplier<SpriteSourceType> OPEN_PALETTED_PERMUTATIONS = () -> openPalettedPermutationsRegistered;

    public static void bootstrap(BiFunction<ResourceLocation, MapCodec<? extends SpriteSource>, SpriteSourceType> registrar) {
        openPalettedPermutationsRegistered = registrar.apply(Trimmed.id("open_paletted_permutations"), OpenPalettedPermutations.CODEC);
    }
}
