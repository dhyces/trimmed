package dhyces.trimmed.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Util {
    public static ModelIdentifier getItemModelId(ItemStack stack) {
        return MinecraftClient.getInstance().getItemRenderer().getModels().modelIds.get(Item.getRawId(stack.getItem()));
    }
}
