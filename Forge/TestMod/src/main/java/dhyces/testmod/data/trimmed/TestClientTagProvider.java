package dhyces.testmod.data.trimmed;

import dhyces.testmod.TrimmedTest;
import dhyces.trimmed.api.data.tags.ClientTagDataProvider;
import dhyces.trimmed.impl.client.tags.ClientTagKey;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

public class TestClientTagProvider extends ClientTagDataProvider {

    public TestClientTagProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, "testmod", existingFileHelper);
    }

    public static final ClientTagKey TEST_TAG = ClientTagKey.of(TrimmedTest.id("test_client_tag"));

    @Override
    protected void addTags() {
        clientTag(TEST_TAG).add(TrimmedTest.id("unreallll")).add(TrimmedTest.id("nahhhh"));
    }
}
