package dev.dhyces.testmod.registry;

import dev.dhyces.testmod.TrimmedTest;
import dev.dhyces.testmod.item.AdamantiumArmorItem;
import dev.dhyces.testmod.item.ScannerItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TrimmedTest.MODID);

    public static final DeferredItem<Item> SPIRAL_PATTERN = ITEMS.register("spiral_armor_trim_smithing_template", () -> new Item(new Item.Properties()));
    
    public static final DeferredItem<Item> ADAMANTIUM_HELMET = ITEMS.register("adamantium_helmet", () -> new AdamantiumArmorItem(ArmorItem.Type.HELMET));
    public static final DeferredItem<Item> ADAMANTIUM_CHESTPLATE = ITEMS.register("adamantium_chestplate", () -> new AdamantiumArmorItem(ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<Item> ADAMANTIUM_LEGGINGS = ITEMS.register("adamantium_leggings", () -> new AdamantiumArmorItem(ArmorItem.Type.LEGGINGS));
    public static final DeferredItem<Item> ADAMANTIUM_BOOTS = ITEMS.register("adamantium_boots", () -> new AdamantiumArmorItem(ArmorItem.Type.BOOTS));
    
    public static final DeferredItem<Item> ADAMANTIUM = ITEMS.register("adamantium", () -> new Item(new Item.Properties().fireResistant()));
    public static final DeferredItem<Item> SCANNER = ITEMS.register("scanner", () -> new ScannerItem(new Item.Properties().stacksTo(1)));
}
