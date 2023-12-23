package dhyces.testmod.data.trimmed;

import dhyces.testmod.TestClientMaps;
import dhyces.testmod.TrimmedTest;
import dev.dhyces.trimmed.api.data.maps.ClientMapDataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

public class TestClientMapProvider extends ClientMapDataProvider {
    public TestClientMapProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, TrimmedTest.MODID, existingFileHelper);
    }

    @Override
    protected void addMaps() {
        map(TestClientMaps.DATAGEN_TEST_MAP_2).put(TrimmedTest.id("some/kind/of/key"), "aValue");
    }
}
