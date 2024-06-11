package dev.dhyces.testmod.data.trimmed.registrytags;

import dev.dhyces.testmod.TrimmedTest;
import dev.dhyces.trimmed.api.client.tag.ClientTagKey;
import dev.dhyces.trimmed.api.data.tag.ClientIntrinsicRegistryTagDataProvider;
import dev.dhyces.trimmed.api.data.tag.ClientRegistryTagDataProvider;
import dev.dhyces.trimmed.impl.client.maps.KeyResolvers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class TestClientItemTagProvider extends ClientIntrinsicRegistryTagDataProvider<Item> {

    public TestClientItemTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProviderFuture, ExistingFileHelper existingFileHelper) {
        super(packOutput, TrimmedTest.MODID, Registries.ITEM, BuiltInRegistries.ITEM::getKey, lookupProviderFuture, existingFileHelper);
    }

    public static final ClientTagKey<Item> TEST_ITEM_TAG = ClientTagKey.of(KeyResolvers.ITEM, TrimmedTest.id("test_item_client_tag"));

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(TEST_ITEM_TAG, lookupProvider).add(Items.IRON_INGOT);
        tag(TEST_ITEM_TAG, lookupProvider).add(Items.ACACIA_BOAT);
    }
}
