package dhyces.testmod.data.trimmed;

import dhyces.testmod.TrimmedTest;
import dev.dhyces.trimmed.api.data.tags.ClientTagDataProvider;
import dev.dhyces.trimmed.impl.client.tags.ClientTagKey;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

public class TestClientTagProvider extends ClientTagDataProvider {

    public TestClientTagProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, TrimmedTest.MODID, existingFileHelper);
    }

    public static final ClientTagKey TEST_TAG = ClientTagKey.of(TrimmedTest.id("test_client_tag"));

    @Override
    protected void addTags() {
        clientTag(TEST_TAG).add(TrimmedTest.id("unreallll")).add(TrimmedTest.id("nahhhh"));
    }
}
