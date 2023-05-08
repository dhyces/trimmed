package dhyces.testmod;

import dhyces.testmod.data.trimmed.TestClientCustomObjTagProvider;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.TrimmedApi;
import dhyces.trimmed.impl.client.maps.ClientMapManager;
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
        TrimmedApi.INSTANCE.getUncheckedTag(TEST_TAG).forEach(id -> TrimmedTest.LOGGER.info(id.toString()));
        TrimmedApi.INSTANCE.getRegistryTag(TEST_ITEM_TAG).forEach(item -> TrimmedTest.LOGGER.info(item.toString()));
        TrimmedApi.INSTANCE.getRegistryTag(TestClientCustomObjTagProvider.TEST_CUSTOM_REG_KEY).forEach(item -> TrimmedTest.LOGGER.info(item.toString()));
        TrimmedApi.INSTANCE.getDatapackedTag(TEST_BIOME_TAG).forEach(biomeHolder -> TrimmedTest.LOGGER.info(biomeHolder.toString()));

        ClientMapManager.getUnchecked(TEST_MAP).ifPresent(map -> TrimmedTest.LOGGER.info("Map present! " + map.get(new ResourceLocation(Trimmed.MODID, "not/a/real/place"))));
        ClientMapManager.getChecked(TEST_ITEM_MAP).ifPresent(itemStringMap -> TrimmedTest.LOGGER.info("Map present! " + itemStringMap.get(Items.IRON_INGOT)));
        ClientMapManager.getDatapacked(TEST_BIOME_MAP).ifPresent(biomeStringMap -> biomeStringMap.forEach((biomeReference, s) -> TrimmedTest.LOGGER.info(biomeReference.key().toString())));
    }
}
