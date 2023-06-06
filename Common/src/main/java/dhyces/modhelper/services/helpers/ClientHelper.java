package dhyces.modhelper.services.helpers;

import com.mojang.blaze3d.platform.NativeImage;
import dhyces.modhelper.network.SimpleChannelHandler;
import dhyces.modhelper.network.SimpleNetworkHandler;
import dhyces.modhelper.network.SimplePacket;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ClientHelper {
    SpriteContents createSpriteContents(ResourceLocation id, NativeImage image);

    ModelResourceLocation getModelLocation(ItemStack itemStack);

    <P extends Player> void registerClientPacket(SimpleChannelHandler channelHandler, SimplePacket.Type<?> packetType, SimpleNetworkHandler.Handler<?, P> packetHandler);
}
