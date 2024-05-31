package dev.dhyces.testmod.data.trimmed;

import dev.dhyces.testmod.TrimmedTest;
import dev.dhyces.testmod.client.TestClientKeyResolvers;
import dev.dhyces.testmod.client.TestClientMapKeys;
import dev.dhyces.trimmed.api.data.map.ClientMapDataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class TestClientMapProvider extends ClientMapDataProvider<ResourceLocation> {
    public TestClientMapProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, TrimmedTest.MODID, TestClientKeyResolvers.TEST, existingFileHelper);
    }

    @Override
    protected void addMaps() {
        map(TestClientMapKeys.DATAGEN_TEST_MAP_2).put(TrimmedTest.id("some/kind/of/key"), "aValue");
    }
}
