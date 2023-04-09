package dhyces.trimmed.api.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.ForgeItemModelShaper;

public class ClientUtil {
    public static ModelResourceLocation getItemModelId(ItemStack stack) {
        return ((ForgeItemModelShaper) Minecraft.getInstance().getItemRenderer().getItemModelShaper()).getLocation(stack);
    }
}
