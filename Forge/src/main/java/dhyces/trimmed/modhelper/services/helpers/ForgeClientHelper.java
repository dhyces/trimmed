package dhyces.trimmed.modhelper.services.helpers;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.ForgeItemModelShaper;
import net.minecraftforge.client.textures.ForgeTextureMetadata;

public class ForgeClientHelper implements ClientHelper {
    @Override
    public SpriteContents createSpriteContents(ResourceLocation id, NativeImage image) {
        return new SpriteContents(id, new FrameSize(image.getWidth(), image.getHeight()), image, AnimationMetadataSection.EMPTY, ForgeTextureMetadata.EMPTY);
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack itemStack) {
        return ((ForgeItemModelShaper) Minecraft.getInstance().getItemRenderer().getItemModelShaper()).getLocation(itemStack);
    }
}
