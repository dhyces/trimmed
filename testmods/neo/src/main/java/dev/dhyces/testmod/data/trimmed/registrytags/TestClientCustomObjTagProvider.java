package dev.dhyces.testmod.data.trimmed.registrytags;

import dev.dhyces.testmod.TestKeyResolvers;
import dev.dhyces.testmod.TrimmedTest;
import dev.dhyces.testmod.client.TestClientTags;
import dev.dhyces.testmod.registry.custom.CustomObj;
import dev.dhyces.testmod.registry.custom.CustomRegistration;
import dev.dhyces.trimmed.api.data.tag.ClientRegistryTagDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class TestClientCustomObjTagProvider extends ClientRegistryTagDataProvider<CustomObj> {
    public TestClientCustomObjTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProviderFuture, ExistingFileHelper existingFileHelper) {
        super(packOutput, TrimmedTest.MODID, CustomRegistration.KEY, lookupProviderFuture, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(TestClientTags.TEST_CUSTOM_REG_KEY, lookupProvider).add(CustomRegistration.OBJ);
    }
}
