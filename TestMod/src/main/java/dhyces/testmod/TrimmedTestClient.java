package dhyces.testmod;

import dhyces.testmod.data.trimmed.TestClientCustomObjTagProvider;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.TrimmedApi;
import dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;
import dhyces.trimmed.impl.client.maps.ClientMapKey;
import dhyces.trimmed.impl.client.tags.ClientRegistryTagKey;
import dhyces.trimmed.impl.client.tags.ClientTagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class TrimmedTestClient {

    public static final ClientTagKey TEST_TAG = ClientTagKey.of(TrimmedTest.id("test_unchecked_client_tag"));
    public static final ClientRegistryTagKey<Item> TEST_ITEM_TAG = ClientRegistryTagKey.of(Registries.ITEM, TrimmedTest.id("test_client_tag"));
    public static final ClientRegistryTagKey<Biome> TEST_BIOME_TAG = ClientRegistryTagKey.of(Registries.BIOME, TrimmedTest.id("test_biome_tag"));

    public static final ClientMapKey TEST_MAP = ClientMapKey.of(TrimmedTest.id("test_map"));
    public static final ClientRegistryMapKey<Item> TEST_ITEM_MAP = ClientRegistryMapKey.of(Registries.ITEM, TrimmedTest.id("checked_item_map"));
    public static final ClientRegistryMapKey<Biome> TEST_BIOME_MAP = ClientRegistryMapKey.of(Registries.BIOME, TrimmedTest.id("checked_biome_map"));

    static void init(IEventBus modBus, IEventBus forgeBus) {
        forgeBus.addListener(TrimmedTestClient::loggedIn);
    }

    private static void loggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        TrimmedApi.TAG_API.getUncheckedTag(TEST_TAG).forEach(id -> TrimmedTest.LOGGER.info(id.toString()));
        TrimmedApi.TAG_API.getRegistryTag(TEST_ITEM_TAG).forEach(item -> TrimmedTest.LOGGER.info(item.toString()));
        TrimmedApi.TAG_API.getRegistryTag(TestClientCustomObjTagProvider.TEST_CUSTOM_REG_KEY).forEach(item -> TrimmedTest.LOGGER.info(item.toString()));
        TrimmedApi.TAG_API.getDatapackedTag(TEST_BIOME_TAG).forEach(biomeHolder -> TrimmedTest.LOGGER.info(biomeHolder.toString()));

        TrimmedTest.LOGGER.info(TrimmedApi.MAP_API.getUncheckedClientMap(TEST_MAP).get(new ResourceLocation(Trimmed.MODID, "not/a/real/place")));
        TrimmedTest.LOGGER.info("Map present! " + TrimmedApi.MAP_API.getRegistryClientMap(TEST_ITEM_MAP).get(Items.IRON_INGOT));
        TrimmedApi.MAP_API.getDatapackedClientMap(TEST_BIOME_MAP).forEach((biomeHolder, s) -> TrimmedTest.LOGGER.info("KEY: " + biomeHolder + " VALUE: " + s));
    }
}
