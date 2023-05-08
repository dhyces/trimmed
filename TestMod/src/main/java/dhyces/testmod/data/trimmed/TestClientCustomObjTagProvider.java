package dhyces.testmod.data.trimmed;

import dhyces.testmod.TrimmedTest;
import dhyces.testmod.registry.CustomObj;
import dhyces.testmod.registry.CustomRegistration;
import dhyces.trimmed.api.data.tags.ClientRegistryTagDataProvider;
import dhyces.trimmed.impl.client.tags.ClientRegistryTagKey;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class TestClientCustomObjTagProvider extends ClientRegistryTagDataProvider<CustomObj> {
    public TestClientCustomObjTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProviderFuture, ExistingFileHelper existingFileHelper) {
        super(packOutput, TrimmedTest.MODID, lookupProviderFuture, CustomRegistration.CUSTOM_DEFERRED_REGISTRY.getRegistryKey(), existingFileHelper);
    }

    public static final ClientRegistryTagKey<CustomObj> TEST_CUSTOM_REG_KEY = ClientRegistryTagKey.of(CustomRegistration.CUSTOM_DEFERRED_REGISTRY.getRegistryKey(), TrimmedTest.id("test"));

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        clientRegistryTag(TEST_CUSTOM_REG_KEY, lookupProvider).add(CustomRegistration.OBJ);
    }
}
