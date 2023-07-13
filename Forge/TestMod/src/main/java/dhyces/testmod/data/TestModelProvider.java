package dhyces.testmod.data;

import dhyces.testmod.ModItems;
import dhyces.testmod.TrimmedTest;
import dhyces.trimmed.api.TrimmedClientMapApi;
import dhyces.trimmed.api.client.UncheckedClientMaps;
import dhyces.trimmed.api.data.maps.BaseMapDataProvider;
import net.minecraft.Util;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

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
