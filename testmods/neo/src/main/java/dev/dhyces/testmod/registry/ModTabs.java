package dev.dhyces.testmod.registry;

import dev.dhyces.testmod.TrimmedTest;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TrimmedTest.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = REGISTER.register("test_tab", () -> CreativeModeTab.builder()
            .title(Component.literal("My Tab"))
            .displayItems((displayParameters, output) -> {
                output.acceptAll(ModItems.ITEMS.getEntries().stream().map(reg -> reg.get().getDefaultInstance()).toList());
            })
            .icon(ModItems.SPIRAL_PATTERN::toStack)
            .withSlotColor(0xFFFFAABB)
            .withSearchBar()
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .build());
}
