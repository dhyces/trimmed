package dev.dhyces.testmod.data;

import dev.dhyces.testmod.TrimmedTest;
import dev.dhyces.testmod.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class TestModelProvider extends ItemModelProvider {
    public TestModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TrimmedTest.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.SPIRAL_PATTERN.get());
        basicItem(ModItems.ADAMANTIUM.get());
    }
}
