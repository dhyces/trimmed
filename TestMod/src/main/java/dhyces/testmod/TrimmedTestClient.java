package dhyces.testmod;

import dhyces.trimmed.Trimmed;
import dhyces.trimmed.impl.client.maps.ClientMapManager;
import dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;
import dhyces.trimmed.impl.client.maps.ClientMapKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class TrimmedTestClient {

    public static final ClientMapKey TEST_MAP = ClientMapKey.of(TrimmedTest.id("test_map"));
    public static final ClientRegistryMapKey<Item> TEST_ITEM_MAP = ClientRegistryMapKey.of(Registries.ITEM, TrimmedTest.id("checked_item_map"));

    static void init(IEventBus forgeBus, IEventBus modBus) {
        forgeBus.addListener(TrimmedTestClient::loggedIn);
    }

    private static void loggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ClientMapManager.getUnchecked(TEST_MAP).ifPresent(map -> TrimmedTest.LOGGER.info("Map present! " + map.get(new ResourceLocation(Trimmed.MODID, "not/a/real/place"))));
        ClientMapManager.getChecked(TEST_ITEM_MAP).ifPresent(itemStringMap -> TrimmedTest.LOGGER.info("Map present! " + itemStringMap.get(Items.IRON_INGOT)));
    }
}
