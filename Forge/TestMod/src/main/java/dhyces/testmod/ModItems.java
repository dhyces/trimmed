package dhyces.testmod;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, TrimmedTest.MODID);

    static void init(IEventBus modBus) {
        ITEMS.register(modBus);
    }

    public static final RegistryObject<Item> SPIRAL_PATTERN = ITEMS.register("spiral_armor_trim_smithing_template", () -> new Item(new Item.Properties().requiredFeatures(FeatureFlags.UPDATE_1_20)));
}
