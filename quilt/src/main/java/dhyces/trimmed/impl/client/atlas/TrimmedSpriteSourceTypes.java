package dhyces.trimmed.impl.client.atlas;

import dhyces.trimmed.impl.mixin.client.AtlasSourceManagerAccessor;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;

public class TrimmedSpriteSourceTypes {
    public static final SpriteSourceType OPEN_PALETTED_PERMUTATIONS = AtlasSourceManagerAccessor.invokeRegister("trimmed:open_paletted_permutations", OpenPalettedPermutations.CODEC);

    public static void bootstrap() {}
}
