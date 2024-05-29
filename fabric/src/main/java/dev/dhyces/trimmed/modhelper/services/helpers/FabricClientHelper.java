package dev.dhyces.trimmed.modhelper.services.helpers;

import com.mojang.blaze3d.platform.NativeImage;
import dev.dhyces.trimmed.api.client.TrimmedClientApiEntrypoint;
import dev.dhyces.trimmed.impl.ModApiConsumer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class FabricClientHelper implements ClientHelper {
    @Override
    public List<ModApiConsumer<TrimmedClientApiEntrypoint>> getClientApiConsumers() {
        return FabricLoader.getInstance().getEntrypointContainers("trimmed:client_api_entrypoint", TrimmedClientApiEntrypoint.class)
                .stream().map(container -> new ModApiConsumer<>(container.getProvider().getMetadata().getId(), container.getEntrypoint())).toList();
    }

    @Override
    public SpriteContents createSpriteContents(ResourceLocation id, NativeImage image) {
        return new SpriteContents(id, new FrameSize(image.getWidth(), image.getHeight()), image, ResourceMetadata.EMPTY);
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack itemStack) {
        return Minecraft.getInstance().getItemRenderer().getItemModelShaper().shapes.get(Item.getId(itemStack.getItem()));
    }
}
