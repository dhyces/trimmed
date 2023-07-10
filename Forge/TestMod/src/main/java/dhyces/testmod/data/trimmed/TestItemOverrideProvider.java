package dhyces.testmod.data.trimmed;

import dhyces.testmod.TrimmedTest;
import dhyces.testmod.client.providers.BlockStateItemOverrideProvider;
import dhyces.trimmed.api.data.ItemOverrideDataProvider;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;

public class TestItemOverrideProvider extends ItemOverrideDataProvider {

    public TestItemOverrideProvider(PackOutput output) {
        super(output, TrimmedTest.MODID);
    }

    @Override
    protected void addItemOverrides() {
        CompoundTag nbt = new CompoundTag();
        CompoundTag states = new CompoundTag();
        states.putString("snowy", "true");
        nbt.put(BlockItem.BLOCK_STATE_TAG, states);
        addNbtOverride(Blocks.GRASS_BLOCK, nbt, new ModelResourceLocation("minecraft", "grass_block", "snowy=true"));
        addItemOverrides(Blocks.BAMBOO_STAIRS, new BlockStateItemOverrideProvider());
    }
}
