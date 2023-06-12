package dhyces.testmod;

import dhyces.testmod.item.TestArmorItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
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
    public static final RegistryObject<Item> TEST_HELMET = ITEMS.register("test_helmet", () -> new TestArmorItem(ArmorItem.Type.HELMET));
    public static final RegistryObject<Item> TEST_CHESTPLATE = ITEMS.register("test_chestplate", () -> new TestArmorItem(ArmorItem.Type.CHESTPLATE));
    public static final RegistryObject<Item> TEST_LEGGINGS = ITEMS.register("test_leggings", () -> new TestArmorItem(ArmorItem.Type.LEGGINGS));
    public static final RegistryObject<Item> TEST_BOOTS = ITEMS.register("test_boots", () -> new TestArmorItem(ArmorItem.Type.BOOTS));
}
