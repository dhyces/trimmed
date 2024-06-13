package dev.dhyces.testmod.data.trimmed;

import dev.dhyces.testmod.TrimmedTest;
import dev.dhyces.testmod.client.TestClientKeyResolvers;
import dev.dhyces.testmod.client.TestClientMapKeys;
import dev.dhyces.trimmed.api.client.map.ClientMapKeys;
import dev.dhyces.trimmed.api.data.map.ClientMapDataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class TestClientMapProvider extends ClientMapDataProvider<ResourceLocation> {
    public TestClientMapProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, TrimmedTest.MODID, existingFileHelper);
    }

    @Override
    protected void addMaps() {
        map(TestClientMapKeys.DATAGEN_TEST_MAP_2).put(TrimmedTest.id("some/kind/of/key"), "aValue");
        map(TestClientMapKeys.ADAMANTIUM_ARMOR)
                .put(TrimmedTest.id("item/adamantium_helmet"), ResourceLocation.withDefaultNamespace("trims/items/helmet_trim"))
                .put(TrimmedTest.id("item/adamantium_chestplate"), ResourceLocation.withDefaultNamespace("trims/items/chestplate_trim"))
                .put(TrimmedTest.id("item/adamantium_leggings"), ResourceLocation.withDefaultNamespace("trims/items/leggings_trim"))
                .put(TrimmedTest.id("item/adamantium_boots"), ResourceLocation.withDefaultNamespace("trims/items/boots_trim"));
        map(ClientMapKeys.TRIM_OVERLAYS)
                .append(TestClientMapKeys.ADAMANTIUM_ARMOR);
        map(TestClientMapKeys.ADAMANTIUM_MATERIAL_OVERRIDES)
                .put(TrimmedTest.id("trims/color_palettes/adamantium"), TrimmedTest.id("trims/color_palettes/adamantium_darker"));
        map(ClientMapKeys.TRIM_MATERIAL_OVERRIDES)
                .append(TestClientMapKeys.ADAMANTIUM_MATERIAL_OVERRIDES);
        map(ClientMapKeys.DARKER_MATERIAL_SUFFIXES)
                .put(TrimmedTest.id("trims/color_palettes/adamantium_darker"), "testmod_adamantium_darker");
    }
}
