package dhyces.testmod.data.trimmed;

import dhyces.testmod.ModTrimMaterials;
import dhyces.testmod.TrimmedTest;
import dhyces.testmod.client.providers.BlockStateItemOverrideProvider;
import dhyces.trimmed.api.data.ItemOverrideDataProvider;
import net.minecraft.Util;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.LinkedList;
import java.util.List;

public class TestItemOverrideProvider extends ItemOverrideDataProvider {

    public TestItemOverrideProvider(PackOutput output) {
        super(output, TrimmedTest.MODID);
    }

    @Override
    protected void addItemOverrides() {
        final List<Item> armorItems = Util.make(new LinkedList<>(), list -> {
            list.add(Items.CHAINMAIL_BOOTS);
            list.add(Items.CHAINMAIL_LEGGINGS);
            list.add(Items.CHAINMAIL_CHESTPLATE);
            list.add(Items.CHAINMAIL_HELMET);
            list.add(Items.DIAMOND_BOOTS);
            list.add(Items.DIAMOND_LEGGINGS);
            list.add(Items.DIAMOND_CHESTPLATE);
            list.add(Items.DIAMOND_HELMET);
            list.add(Items.GOLDEN_BOOTS);
            list.add(Items.GOLDEN_LEGGINGS);
            list.add(Items.GOLDEN_CHESTPLATE);
            list.add(Items.GOLDEN_HELMET);
            list.add(Items.IRON_BOOTS);
            list.add(Items.IRON_LEGGINGS);
            list.add(Items.IRON_CHESTPLATE);
            list.add(Items.IRON_HELMET);
            list.add(Items.LEATHER_BOOTS);
            list.add(Items.LEATHER_LEGGINGS);
            list.add(Items.LEATHER_CHESTPLATE);
            list.add(Items.LEATHER_HELMET);
            list.add(Items.NETHERITE_BOOTS);
            list.add(Items.NETHERITE_LEGGINGS);
            list.add(Items.NETHERITE_CHESTPLATE);
            list.add(Items.NETHERITE_HELMET);
            list.add(Items.TURTLE_HELMET);
        });
        for (Item item : armorItems) {
            addTrimOverride(item, ModTrimMaterials.BLAZE);
            addTrimOverride(item, ModTrimMaterials.ECHO);
            addTrimOverride(item, ModTrimMaterials.GLOW);
            addTrimOverride(item, ModTrimMaterials.PRISMARINE);
            addTrimOverride(item, ModTrimMaterials.SHELL);
        }

        CompoundTag nbt = new CompoundTag();
        CompoundTag states = new CompoundTag();
        states.putString("snowy", "true");
        nbt.put(BlockItem.BLOCK_STATE_TAG, states);
        addNbtOverride(Blocks.GRASS_BLOCK, nbt, new ModelResourceLocation("minecraft", "grass_block", "snowy=true"));
        addItemOverrides(Blocks.BAMBOO_STAIRS, new BlockStateItemOverrideProvider());
    }
}
