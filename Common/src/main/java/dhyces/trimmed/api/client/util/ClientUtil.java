package dhyces.trimmed.api.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.Nullable;

public class ClientUtil {
    @Nullable
    public static RegistryAccess getRegistryAccess() {
        if (Minecraft.getInstance().level == null) {
            return null;
        }
        return Minecraft.getInstance().level.registryAccess();
    }

    public static boolean textureExists(ResourceLocation atlas, ResourceLocation textureId) {
        return Minecraft.getInstance().getTextureAtlas(atlas).apply(textureId).contents().name() != MissingTextureAtlasSprite.getLocation();
    }

    public static boolean trimTextureExists(ResourceLocation textureId) {
        return textureExists(Sheets.ARMOR_TRIMS_SHEET, textureId);
    }
}
