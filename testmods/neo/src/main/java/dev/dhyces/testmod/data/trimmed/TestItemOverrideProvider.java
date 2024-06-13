package dev.dhyces.testmod.data.trimmed;

import dev.dhyces.testmod.TrimmedTest;
import dev.dhyces.testmod.client.providers.BlockStateItemOverrideProvider;
import dev.dhyces.testmod.registry.ModItems;
import dev.dhyces.trimmed.api.data.ItemOverrideDataProvider;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;

public class TestItemOverrideProvider extends ItemOverrideDataProvider {

    public TestItemOverrideProvider(PackOutput output) {
        super(output, TrimmedTest.MODID);
    }

    @Override
    protected void addItemOverrides() {
        addComponentOverride(Blocks.GRASS_BLOCK, builder -> builder.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(GrassBlock.SNOWY, true)), new ModelResourceLocation(ResourceLocation.withDefaultNamespace("grass_block"), "snowy=true"));
        addItemOverrides(Blocks.BAMBOO_STAIRS, new BlockStateItemOverrideProvider());
        anyTrimBuilder(new ArmorSet(ModItems.ADAMANTIUM_HELMET, ModItems.ADAMANTIUM_CHESTPLATE, ModItems.ADAMANTIUM_LEGGINGS, ModItems.ADAMANTIUM_BOOTS))
                .defaultTemplate()
                .end();
    }
}
