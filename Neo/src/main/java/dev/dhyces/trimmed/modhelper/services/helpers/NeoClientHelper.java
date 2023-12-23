package dev.dhyces.trimmed.modhelper.services.helpers;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.RegistryAwareItemModelShaper;

public class NeoClientHelper implements ClientHelper {
    @Override
    public SpriteContents createSpriteContents(ResourceLocation id, NativeImage image) {
        return new SpriteContents(id, new FrameSize(image.getWidth(), image.getHeight()), image, ResourceMetadata.EMPTY);
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack itemStack) {
        return ((RegistryAwareItemModelShaper) Minecraft.getInstance().getItemRenderer().getItemModelShaper()).getLocation(itemStack);
    }
}
