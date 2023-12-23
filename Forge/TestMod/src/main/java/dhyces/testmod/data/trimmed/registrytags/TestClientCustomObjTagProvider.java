package dhyces.testmod.data.trimmed.registrytags;

import dhyces.testmod.TestClientTags;
import dhyces.testmod.TrimmedTest;
import dhyces.testmod.registry.CustomObj;
import dhyces.testmod.registry.CustomRegistration;
import dev.dhyces.trimmed.api.data.tags.ClientRegistryTagDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class TestClientCustomObjTagProvider extends ClientRegistryTagDataProvider<CustomObj> {
    public TestClientCustomObjTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProviderFuture, ExistingFileHelper existingFileHelper) {
        super(packOutput, TrimmedTest.MODID, lookupProviderFuture, CustomRegistration.CUSTOM_DEFERRED_REGISTRY.getRegistryKey(), existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        registryAwareTag(TestClientTags.TEST_CUSTOM_REG_KEY, lookupProvider).add(CustomRegistration.OBJ);
    }
}
