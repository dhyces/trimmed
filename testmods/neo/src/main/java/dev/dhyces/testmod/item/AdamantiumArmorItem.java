package dev.dhyces.testmod.item;

import dev.dhyces.testmod.registry.ModArmorMaterials;
import net.minecraft.world.item.ArmorItem;

public class AdamantiumArmorItem extends ArmorItem {
    public AdamantiumArmorItem(Type type) {
        super(ModArmorMaterials.ADAMANTIUM, type, new Properties().stacksTo(1).durability(9999));
    }
}
