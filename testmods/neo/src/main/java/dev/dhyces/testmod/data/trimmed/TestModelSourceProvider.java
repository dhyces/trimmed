package dev.dhyces.testmod.data.trimmed;

import dev.dhyces.testmod.TrimmedTest;
import dev.dhyces.testmod.registry.ModArmorMaterials;
import dev.dhyces.trimmed.api.data.model.source.ModelSourceDataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class TestModelSourceProvider extends ModelSourceDataProvider {
    public TestModelSourceProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, TrimmedTest.MODID, existingFileHelper);
    }

    @Override
    protected void addModelSources() {
        addTwoLayerTrimsSource(ModArmorMaterials.ADAMANTIUM.value());
    }
}
