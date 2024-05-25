package dhyces.testmod;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTabs {
    private static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TrimmedTest.MODID);

    static void register(IEventBus modBus) {
        REGISTER.register(modBus);
    }

    public static final RegistryObject<CreativeModeTab> TAB = REGISTER.register("test_tab", () -> CreativeModeTab.builder()
            .title(Component.literal("My Tab"))
            .displayItems((displayParameters, output) -> {
                output.acceptAll(ModItems.ITEMS.getEntries().stream().map(reg -> reg.get().getDefaultInstance()).toList());
            })
            .icon(ModItems.SPIRAL_PATTERN.lazyMap(Item::getDefaultInstance))
            .withSlotColor(0xFFFFAABB)
            .withSearchBar()
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .build());
}
