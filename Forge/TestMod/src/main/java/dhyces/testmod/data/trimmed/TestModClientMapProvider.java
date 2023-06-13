package dhyces.testmod.data.trimmed;

import dhyces.testmod.ModTrimMaterials;
import dhyces.testmod.TrimmedTest;
import dhyces.testmod.item.AdamantiumArmorItem;
import dhyces.trimmed.api.client.UncheckedClientMaps;
import dhyces.trimmed.api.data.maps.ClientMapDataProvider;
import dhyces.trimmed.impl.client.maps.ClientMapKey;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

public class TestModClientMapProvider extends ClientMapDataProvider {
    public TestModClientMapProvider(PackOutput packOutput, String modid, ExistingFileHelper existingFileHelper) {
        super(packOutput, modid, existingFileHelper);
    }

    public static final ClientMapKey ADAMANTIUM_MATERIAL_OVERRIDES = UncheckedClientMaps.armorMaterialOverride(ModTrimMaterials.ADAMANTIUM);

    @Override
    protected void addMaps() {
        map(UncheckedClientMaps.CUSTOM_TRIM_PERMUTATIONS)
                .put(TrimmedTest.id("trims/color_palettes/blaze"), "trimmed_testmod-blaze")
                .put(TrimmedTest.id("trims/color_palettes/echo"), "trimmed_testmod-echo")
                .put(TrimmedTest.id("trims/color_palettes/glow"), "trimmed_testmod-glow")
                .put(TrimmedTest.id("trims/color_palettes/prismarine"), "trimmed_testmod-prismarine")
                .put(TrimmedTest.id("trims/color_palettes/shell"), "trimmed_testmod-shell")
                .put(TrimmedTest.id("trims/color_palettes/adamantium"), "trimmed_testmod-adamantium")
                .put(TrimmedTest.id("trims/color_palettes/adamantium_darker"), "trimmed_testmod-adamantium_darker");
        map(ADAMANTIUM_MATERIAL_OVERRIDES)
                .put(new ResourceLocation(AdamantiumArmorItem.AdamantiumArmorMaterial.INSTANCE.getName()), "trimmed_testmod-adamantium_darker");
    }

    @Override
    public String getName() {
        return "nononono";
    }
}
