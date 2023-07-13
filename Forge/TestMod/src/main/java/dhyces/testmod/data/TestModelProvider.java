package dhyces.testmod.data;

import dhyces.testmod.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

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
