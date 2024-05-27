package dev.dhyces.trimmed.model;

import dev.dhyces.trimmed.api.data.ItemOverrideDataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

public class VanillaItemModelOverrideProvider extends ItemOverrideDataProvider {
    public VanillaItemModelOverrideProvider(PackOutput output) {
        super(output, "minecraft");
    }

    @Override
    protected void addItemOverrides() {
        anyTrimBuilder(new ArmorSet(Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS))
                .defaultTemplate()
                .end();
        anyTrimBuilder(new ArmorSet(Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS))
                .defaultTemplate()
                .end();
        anyTrimBuilder(new ArmorSet(Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS))
                .defaultTemplate()
                .end();
        anyTrimBuilder(new ArmorSet(Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS))
                .defaultTemplate()
                .end();
        anyTrimBuilder(new ArmorSet(Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS))
                .defaultTemplate()
                .end();
        anyTrimBuilder(new ArmorSet(Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS))
                .defaultTemplate()
                .end();
        addAnyTrimOverride(Items.TURTLE_HELMET);
    }
}
