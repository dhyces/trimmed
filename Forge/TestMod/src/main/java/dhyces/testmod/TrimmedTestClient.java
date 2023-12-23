package dhyces.testmod;

import dev.dhyces.trimmed.api.TrimmedClientMapApi;
import dev.dhyces.trimmed.api.TrimmedClientTagApi;
import dev.dhyces.trimmed.api.maps.LimitedMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class TrimmedTestClient {

    static void init(IEventBus modBus, IEventBus forgeBus) {
        forgeBus.addListener(TrimmedTestClient::loggedIn);
    }

    private static void loggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        var t = "minecraft:textures/models/armor/test_layer_1/trimmed_test";
        TrimmedClientTagApi.INSTANCE.getUncheckedTag(TestClientTags.TEST_TAG).forEach(id -> TrimmedTest.LOGGER.info(id.toString()));
        TrimmedClientTagApi.INSTANCE.getRegistryTag(TestClientTags.TEST_ITEM_TAG).forEach(item -> TrimmedTest.LOGGER.info(item.toString()));
        TrimmedClientTagApi.INSTANCE.getRegistryTag(TestClientTags.TEST_CUSTOM_REG_KEY).forEach(item -> TrimmedTest.LOGGER.info(item.toString()));
        TrimmedClientTagApi.INSTANCE.getSafeDatapackedTag(TestClientTags.TEST_BIOME_TAG).ifPresent(holders -> holders.forEach(biomeHolder -> TrimmedTest.LOGGER.info(biomeHolder.toString())));

        TrimmedClientMapApi.INSTANCE.map(TestClientMaps.MANUAL_TEST_MAP).forEach((entry) -> TrimmedTest.LOGGER.info("KEY: {}, VALUE: {}", entry.getKey(), entry.getValue()));
        TrimmedTest.LOGGER.info("Map present! " + TrimmedClientMapApi.INSTANCE.map(TestClientMaps.MANUAL_TEST_ITEM_MAP).get(Items.IRON_INGOT));
        TrimmedClientMapApi.INSTANCE.map(TestClientMaps.MANUAL_TEST_BIOME_MAP).forEach((entry) -> TrimmedTest.LOGGER.info("KEY: {}, VALUE: {}", entry.getKey(), entry.getValue()));

        TrimmedTest.TEST_DELEGATE.forEach((entry) -> {
            TrimmedTest.LOGGER.info("KEY: {}, VALUE: {}", entry.getKey(), entry.getValue());
        });

        TrimmedTest.TEST_DELEGATE_2.forEach((entry) -> {
            TrimmedTest.LOGGER.info("KEY: {}, VALUE: {}", entry.getKey(), entry.getValue());
        });
    }

    public static final LimitedMap<Block, String> DESC_MAP = TrimmedClientMapApi.INSTANCE.map(TestClientMaps.MANUAL_SCANNER_DESCS);

    public static void printDescriptor(Player player, Block block) {
        DESC_MAP.getOptional(block).ifPresent(s -> player.sendSystemMessage(Component.literal(s)));
    }
}