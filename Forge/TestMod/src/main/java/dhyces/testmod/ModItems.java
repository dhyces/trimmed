package dhyces.testmod;

import dhyces.testmod.item.AdamantiumArmorItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, TrimmedTest.MODID);

    static void init(IEventBus modBus) {
        ITEMS.register(modBus);
    }

    public static final RegistryObject<Item> SPIRAL_PATTERN = ITEMS.register("spiral_armor_trim_smithing_template", () -> new Item(new Item.Properties()));
    
    public static final RegistryObject<Item> ADAMANTIUM_HELMET = ITEMS.register("adamantium_helmet", () -> new AdamantiumArmorItem(ArmorItem.Type.HELMET));
    public static final RegistryObject<Item> ADAMANTIUM_CHESTPLATE = ITEMS.register("adamantium_chestplate", () -> new AdamantiumArmorItem(ArmorItem.Type.CHESTPLATE));
    public static final RegistryObject<Item> ADAMANTIUM_LEGGINGS = ITEMS.register("adamantium_leggings", () -> new AdamantiumArmorItem(ArmorItem.Type.LEGGINGS));
    public static final RegistryObject<Item> ADAMANTIUM_BOOTS = ITEMS.register("adamantium_boots", () -> new AdamantiumArmorItem(ArmorItem.Type.BOOTS));
    
    public static final RegistryObject<Item> ADAMANTIUM = ITEMS.register("adamantium", () -> new Item(new Item.Properties().fireResistant()));
}
