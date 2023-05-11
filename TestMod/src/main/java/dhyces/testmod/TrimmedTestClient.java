package dhyces.testmod;

import dhyces.trimmed.api.TrimmedApi;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class TrimmedTestClient {

    static void init(IEventBus modBus, IEventBus forgeBus) {
        forgeBus.addListener(TrimmedTestClient::loggedIn);
    }

    private static void loggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        TrimmedApi.TAG_API.getUncheckedTag(TestClientTags.TEST_TAG).forEach(id -> TrimmedTest.LOGGER.info(id.toString()));
        TrimmedApi.TAG_API.getRegistryTag(TestClientTags.TEST_ITEM_TAG).forEach(item -> TrimmedTest.LOGGER.info(item.toString()));
        TrimmedApi.TAG_API.getRegistryTag(TestClientTags.TEST_CUSTOM_REG_KEY).forEach(item -> TrimmedTest.LOGGER.info(item.toString()));
        TrimmedApi.TAG_API.getDatapackedTag(TestClientTags.TEST_BIOME_TAG).forEach(biomeHolder -> TrimmedTest.LOGGER.info(biomeHolder.toString()));

        TrimmedApi.MAP_API.getUncheckedClientMap(TestClientMaps.MANUAL_TEST_MAP).forEach((optionalId, s) -> TrimmedTest.LOGGER.info("KEY: {}, VALUE: {}", optionalId, s));
        TrimmedTest.LOGGER.info("Map present! " + TrimmedApi.MAP_API.getRegistryClientMap(TestClientMaps.MANUAL_TEST_ITEM_MAP).get(Items.IRON_INGOT));
        TrimmedApi.MAP_API.getDatapackedClientMap(TestClientMaps.MANUAL_TEST_BIOME_MAP).forEach((biomeHolder, s) -> TrimmedTest.LOGGER.info("KEY: {}, VALUE: {}", biomeHolder, s));

        TrimmedTest.TEST_DELEGATE.forEach((damageTypeHolder, integer) -> {
            TrimmedTest.LOGGER.info("KEY: {}, VALUE: {}", damageTypeHolder, integer);
        });

        TrimmedTest.TEST_DELEGATE_2.forEach((entityType1, entityType2) -> {
            TrimmedTest.LOGGER.info("KEY: {}, VALUE: {}", entityType1, entityType2);
        });
    }
}
