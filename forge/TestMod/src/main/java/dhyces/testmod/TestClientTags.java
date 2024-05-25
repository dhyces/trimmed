package dhyces.testmod;

import dhyces.testmod.registry.CustomObj;
import dhyces.testmod.registry.CustomRegistration;
import dev.dhyces.trimmed.impl.client.tags.ClientRegistryTagKey;
import dev.dhyces.trimmed.impl.client.tags.ClientTagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;

public class TestClientTags {
    // MANUAL
    public static final ClientTagKey TEST_TAG = ClientTagKey.of(TrimmedTest.id("test_unchecked_client_tag"));
    public static final ClientRegistryTagKey<Item> TEST_ITEM_TAG = ClientRegistryTagKey.of(Registries.ITEM, TrimmedTest.id("test_client_tag"));
    public static final ClientRegistryTagKey<Biome> TEST_BIOME_TAG = ClientRegistryTagKey.of(Registries.BIOME, TrimmedTest.id("test_biome_tag"));

    // DATAGENNED
    public static final ClientRegistryTagKey<CustomObj> TEST_CUSTOM_REG_KEY = ClientRegistryTagKey.of(CustomRegistration.CUSTOM_DEFERRED_REGISTRY.getRegistryKey(), TrimmedTest.id("test"));
}
