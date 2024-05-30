package dev.dhyces.testmod.client;

import dev.dhyces.testmod.TestKeyResolvers;
import dev.dhyces.testmod.TrimmedTest;
import dev.dhyces.testmod.registry.custom.CustomObj;
import dev.dhyces.trimmed.api.client.tag.ClientTagKey;
import dev.dhyces.trimmed.impl.client.maps.KeyResolvers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class TestClientTags {
    // MANUAL
    public static final ClientTagKey<ResourceLocation> TEST_TAG = ClientTagKey.of(TestClientKeyResolvers.TEST, TrimmedTest.id("test_unchecked_client_tag"));
    public static final ClientTagKey<Item> TEST_ITEM_TAG = ClientTagKey.of(KeyResolvers.ITEM, TrimmedTest.id("test_client_tag"));
//    public static final ClientTagKey<Biome> TEST_BIOME_TAG = ClientTagKey.of(KeyResolvers.BIOME, TrimmedTest.id("test_biome_tag"));

    // DATAGENNED
    public static final ClientTagKey<CustomObj> TEST_CUSTOM_REG_KEY = ClientTagKey.of(TestKeyResolvers.CUSTOM_OBJ, TrimmedTest.id("test"));
}
