package dev.dhyces.trimmed.modhelper.services.helpers;

import com.mojang.blaze3d.platform.NativeImage;
import dev.dhyces.trimmed.api.client.TrimmedClientApiEntrypoint;
import dev.dhyces.trimmed.impl.ModApiConsumer;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface ClientHelper {
    List<ModApiConsumer<TrimmedClientApiEntrypoint>> getClientApiConsumers();

    SpriteContents createSpriteContents(ResourceLocation id, NativeImage image);

    ModelResourceLocation getModelLocation(ItemStack itemStack);
}
