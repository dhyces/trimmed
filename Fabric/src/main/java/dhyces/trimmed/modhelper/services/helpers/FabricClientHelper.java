package dhyces.trimmed.modhelper.services.helpers;

import com.mojang.blaze3d.platform.NativeImage;
import dhyces.trimmed.modhelper.services.helpers.ClientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FabricClientHelper implements ClientHelper {
    @Override
    public SpriteContents createSpriteContents(ResourceLocation id, NativeImage image) {
        return new SpriteContents(id, new FrameSize(image.getWidth(), image.getHeight()), image, AnimationMetadataSection.EMPTY);
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack itemStack) {
        return Minecraft.getInstance().getItemRenderer().getItemModelShaper().shapes.get(Item.getId(itemStack.getItem()));
    }
}
