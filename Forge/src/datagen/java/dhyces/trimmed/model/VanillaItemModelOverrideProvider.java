package dhyces.trimmed.model;

import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.data.ItemOverrideDataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public class VanillaItemModelOverrideProvider extends ItemOverrideDataProvider {
    public VanillaItemModelOverrideProvider(PackOutput output) {
        super(output, "minecraft");
    }

    @Override
    protected void addItemOverrides() {
        anyTrimBuilder(new ArmorSet(Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS))
                .threeLayer()
                .excludeVanillaDarker()
                .end();
        anyTrimBuilder(new ArmorSet(Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS))
                .twoLayer()
                .excludeVanillaDarker()
                .end();
        anyTrimBuilder(new ArmorSet(Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS))
                .twoLayer()
                .excludeVanillaDarker()
                .exclude(new ResourceLocation("trims/color_palettes/iron"))
                .include(new ResourceLocation("trims/color_palettes/iron_darker"))
                .end();
        anyTrimBuilder(new ArmorSet(Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS))
                .twoLayer()
                .excludeVanillaDarker()
                .exclude(new ResourceLocation("trims/color_palettes/gold"))
                .include(new ResourceLocation("trims/color_palettes/gold_darker"))
                .end();
        anyTrimBuilder(new ArmorSet(Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS))
                .twoLayer()
                .excludeVanillaDarker()
                .exclude(new ResourceLocation("trims/color_palettes/diamond"))
                .include(new ResourceLocation("trims/color_palettes/diamond_darker"))
                .end();
        anyTrimBuilder(new ArmorSet(Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS))
                .twoLayer()
                .excludeVanillaDarker()
                .exclude(new ResourceLocation("trims/color_palettes/netherite"))
                .include(new ResourceLocation("trims/color_palettes/netherite_darker"))
                .end();
        addAnyTrimOverride(Items.TURTLE_HELMET, Trimmed.id("item/two_layer_trim"), new ResourceLocation("trims/items/helmet_trim"), ArmorSetTrimBuilder.VANILLA_DARKER_COLORS.get());
    }
}
