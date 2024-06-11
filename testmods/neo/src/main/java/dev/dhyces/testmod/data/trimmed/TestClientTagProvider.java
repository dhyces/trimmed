package dev.dhyces.testmod.data.trimmed;

import dev.dhyces.testmod.client.TestClientKeyResolvers;
import dev.dhyces.testmod.TrimmedTest;
import dev.dhyces.trimmed.api.client.tag.ClientTagKey;
import dev.dhyces.trimmed.api.data.tag.ClientTagDataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class TestClientTagProvider extends ClientTagDataProvider<ResourceLocation> {

    public TestClientTagProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, TrimmedTest.MODID, TestClientKeyResolvers.TEST, existingFileHelper);
    }

    public static final ClientTagKey<ResourceLocation> TEST_TAG = ClientTagKey.of(TestClientKeyResolvers.TEST, TrimmedTest.id("test_client_tag"));

    @Override
    protected void addTags() {
        tag(TEST_TAG).add(TrimmedTest.id("unreallll")).add(TrimmedTest.id("nahhhh"));
    }
}
