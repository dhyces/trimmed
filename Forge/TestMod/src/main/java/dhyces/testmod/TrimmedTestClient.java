package dhyces.testmod;

import dhyces.trimmed.api.TrimmedClientApi;
import dhyces.trimmed.api.TrimmedClientMapApi;
import dhyces.trimmed.api.TrimmedClientTagApi;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class TrimmedTestClient {

    static void init(IEventBus modBus, IEventBus forgeBus) {
        forgeBus.addListener(TrimmedTestClient::loggedIn);
        modBus.addListener(TrimmedTestClient::buildContents);
    }

    private static void loggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        var t = "minecraft:textures/models/armor/test_layer_1/trimmed_test";
        TrimmedClientTagApi.INSTANCE.getUncheckedTag(TestClientTags.TEST_TAG).forEach(id -> TrimmedTest.LOGGER.info(id.toString()));
        TrimmedClientTagApi.INSTANCE.getRegistryTag(TestClientTags.TEST_ITEM_TAG).forEach(item -> TrimmedTest.LOGGER.info(item.toString()));
        TrimmedClientTagApi.INSTANCE.getRegistryTag(TestClientTags.TEST_CUSTOM_REG_KEY).forEach(item -> TrimmedTest.LOGGER.info(item.toString()));
        TrimmedClientTagApi.INSTANCE.getSafeDatapackedTag(TestClientTags.TEST_BIOME_TAG).ifPresent(holders -> holders.forEach(biomeHolder -> TrimmedTest.LOGGER.info(biomeHolder.toString())));

        TrimmedClientMapApi.INSTANCE.getUncheckedClientMap(TestClientMaps.MANUAL_TEST_MAP).forEach((entry) -> TrimmedTest.LOGGER.info("KEY: {}, VALUE: {}", entry.getKey(), entry.getValue()));
        TrimmedTest.LOGGER.info("Map present! " + TrimmedClientMapApi.INSTANCE.getRegistryClientMap(TestClientMaps.MANUAL_TEST_ITEM_MAP).get(Items.IRON_INGOT));
        TrimmedClientMapApi.INSTANCE.getSafeRegistryClientMap(TestClientMaps.MANUAL_TEST_BIOME_MAP).ifPresent(map -> map.forEach((entry) -> TrimmedTest.LOGGER.info("KEY: {}, VALUE: {}", entry.getKey(), entry.getValue())));

        TrimmedTest.TEST_DELEGATE.forEach((entry) -> {
            TrimmedTest.LOGGER.info("KEY: {}, VALUE: {}", entry.getKey(), entry.getValue());
        });

        TrimmedTest.TEST_DELEGATE_2.forEach((entry) -> {
            TrimmedTest.LOGGER.info("KEY: {}, VALUE: {}", entry.getKey(), entry.getValue());
        });
    }

    private static void buildContents(final BuildCreativeModeTabContentsEvent event) {
//        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
//            event.accept(ModItems.SPIRAL_PATTERN);
//            event.accept(ModItems.TEST_HELMET);
//            event.accept(ModItems.TEST_CHESTPLATE);
//            event.accept(ModItems.TEST_LEGGINGS);
//            event.accept(ModItems.TEST_BOOTS);
//        }
    }
}
