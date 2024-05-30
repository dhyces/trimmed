package dev.dhyces.testmod.data;

import dev.dhyces.testmod.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class TestModelProvider extends ItemModelProvider {
    public TestModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.SPIRAL_PATTERN.get());
        basicItem(ModItems.ADAMANTIUM.get());
    }
}
