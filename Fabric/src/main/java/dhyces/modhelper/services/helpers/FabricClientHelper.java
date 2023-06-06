package dhyces.modhelper.services.helpers;

import com.mojang.blaze3d.platform.NativeImage;
import dhyces.modhelper.network.SimpleChannelHandler;
import dhyces.modhelper.network.SimpleNegotiator;
import dhyces.modhelper.network.SimpleNetworkHandler;
import dhyces.modhelper.network.SimplePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
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

    @Override
    public <P extends Player> void registerClientPacket(SimpleChannelHandler channelHandler, SimplePacket.Type<?> packetType, SimpleNetworkHandler.Handler<?, P> packetHandler) {
        ClientPlayNetworking.registerGlobalReceiver(packetType.id(), (client, handler, buf, responseSender) -> {
            SimplePacket<?> packet = packetType.factory().apply(buf);
            client.execute(() -> packetHandler.handlePacket(packet.cast(), (P) client.player, SimpleNegotiator.simpleWrapper(responseSender::sendPacket)));
        });
    }
}
