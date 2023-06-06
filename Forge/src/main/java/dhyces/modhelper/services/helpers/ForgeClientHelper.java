package dhyces.modhelper.services.helpers;

import com.mojang.blaze3d.platform.NativeImage;
import dhyces.modhelper.network.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.ForgeItemModelShaper;
import net.minecraftforge.client.textures.ForgeTextureMetadata;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;

public class ForgeClientHelper implements ClientHelper {
    @Override
    public SpriteContents createSpriteContents(ResourceLocation id, NativeImage image) {
        return new SpriteContents(id, new FrameSize(image.getWidth(), image.getHeight()), image, AnimationMetadataSection.EMPTY, ForgeTextureMetadata.EMPTY);
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack itemStack) {
        return ((ForgeItemModelShaper) Minecraft.getInstance().getItemRenderer().getItemModelShaper()).getLocation(itemStack);
    }

    @Override
    public <P extends Player> void registerClientPacket(SimpleChannelHandler channelHandler, SimplePacket.Type<?> packetType, SimpleNetworkHandler.Handler<?, P> packetHandler) {
        ForgeChannelHandler forgeChannelHandler = ((ForgeChannelHandler)channelHandler);
        forgeChannelHandler.channel.messageBuilder(packetType.clazz(), forgeChannelHandler.packetId++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SimplePacket::writeTo)
                .decoder(friendlyByteBuf -> packetType.factory().apply(friendlyByteBuf).cast())
                .consumerMainThread((packet, contextSupplier) -> {
                    packetHandler.handlePacket(packet.cast(), (P) Minecraft.getInstance().player, SimpleNegotiator.simpleWrapper(contextSupplier.get().getPacketDispatcher()::sendPacket));
                }).add();
    }
}
