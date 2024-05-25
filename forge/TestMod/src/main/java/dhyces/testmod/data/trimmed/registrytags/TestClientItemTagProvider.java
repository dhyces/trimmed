package dhyces.testmod.data.trimmed.registrytags;

import dhyces.testmod.TrimmedTest;
import dev.dhyces.trimmed.api.data.tags.ClientRegistryTagDataProvider;
import dev.dhyces.trimmed.impl.client.tags.ClientRegistryTagKey;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class TestClientItemTagProvider extends ClientRegistryTagDataProvider<Item> {

    public TestClientItemTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProviderFuture, ExistingFileHelper existingFileHelper) {
        super(packOutput, TrimmedTest.MODID, lookupProviderFuture, Registries.ITEM, existingFileHelper);
    }

    public static final ClientRegistryTagKey<Item> TEST_ITEM_TAG = ClientRegistryTagKey.of(Registries.ITEM, TrimmedTest.id("test_item_client_tag"));

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(TEST_ITEM_TAG).add(new ResourceLocation("iron_ingot"));
        registryAwareTag(TEST_ITEM_TAG, lookupProvider).add(Items.ACACIA_BOAT);
    }
}
