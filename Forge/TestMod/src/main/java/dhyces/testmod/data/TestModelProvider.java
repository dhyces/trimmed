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
    static Set<ArmorMaterial> armorMaterials = Util.make(new HashSet<>(), set -> {
        set.add(ArmorMaterials.LEATHER);
        set.add(ArmorMaterials.IRON);
        set.add(ArmorMaterials.GOLD);
        set.add(ArmorMaterials.DIAMOND);
        set.add(ArmorMaterials.NETHERITE);
        set.add(ArmorMaterials.CHAIN);
        set.add(ArmorMaterials.TURTLE);
    });

    private final CompletableFuture<BaseMapDataProvider.MapLookup> lookup;

    public TestModelProvider(PackOutput output, CompletableFuture<BaseMapDataProvider.MapLookup> mapLookup, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
        this.lookup = mapLookup;
    }

    @Override
    protected void registerModels() {
        lookup.thenAccept(this::registerArmors);

        basicItem(ModItems.SPIRAL_PATTERN.get());
        basicItem(ModItems.ADAMANTIUM.get());
    }

    private void registerArmors(BaseMapDataProvider.MapLookup mapLookup) {
        createArmors(TrimmedTest.id("blaze"), armorMaterials, mapLookup);
        createArmors(TrimmedTest.id("echo"), armorMaterials, mapLookup);
        createArmors(TrimmedTest.id("glow"), armorMaterials, mapLookup);
        createArmors(TrimmedTest.id("prismarine"), armorMaterials, mapLookup);
        createArmors(TrimmedTest.id("shell"), armorMaterials, mapLookup);
        createArmors(TrimmedTest.id("adamantium"), armorMaterials, mapLookup);
    }

    private void createArmors(ResourceLocation trimMaterial, Set<ArmorMaterial> materials, BaseMapDataProvider.MapLookup mapLookup) {
        ResourceLocation key = trimMaterial.withPrefix("trims/color_palettes/");
        String suffix = mapLookup.apply(UncheckedClientMaps.CUSTOM_TRIM_PERMUTATIONS.getMapId()).get(key).value();
        for (ArmorMaterial material : materials) {
            if (!(material == ArmorMaterials.LEATHER)) {
                createTwoLayerArmor(material, trimMaterial, suffix);
            } else {
                createThreeLayerArmor(material, trimMaterial, suffix);
            }
        }
    }

    private void createTwoLayerArmor(ArmorMaterial material, ResourceLocation trimMaterial, String suffix) {
        uploadTwoLayerArmor(material, ArmorItem.Type.BOOTS, trimMaterial, suffix);
        uploadTwoLayerArmor(material, ArmorItem.Type.LEGGINGS, trimMaterial, suffix);
        uploadTwoLayerArmor(material, ArmorItem.Type.CHESTPLATE, trimMaterial, suffix);
        uploadTwoLayerArmor(material, ArmorItem.Type.HELMET, trimMaterial, suffix);
    }


    private void uploadTwoLayerArmor(ArmorMaterial material, ArmorItem.Type type, ResourceLocation trimMaterial, String suffix) {
        String armorMaterialName = material == ArmorMaterials.GOLD ? "golden" : material.getName();
        ResourceLocation id = new ResourceLocation(modid, "item/%s_%s_%s_trim".formatted(armorMaterialName, type.getName(), trimMaterial.getPath()));
        ResourceLocation layer0Id = trackGeneratedTexture(new ResourceLocation("item/%s_%s".formatted(armorMaterialName, type.getName())));
        ResourceLocation layer1Id = trackGeneratedTexture(new ResourceLocation("trims/items/%s_trim_%s".formatted(type.getName(), suffix)));
        withExistingParent(id.getPath(), "item/generated")
                .texture("layer0", layer0Id.toString())
                .texture("layer1", layer1Id.toString());

    }

    private void createThreeLayerArmor(ArmorMaterial material, ResourceLocation trimMaterial, String suffix) {
        uploadThreeLayerArmor(material, ArmorItem.Type.BOOTS, trimMaterial, suffix);
        uploadThreeLayerArmor(material, ArmorItem.Type.LEGGINGS, trimMaterial, suffix);
        uploadThreeLayerArmor(material, ArmorItem.Type.CHESTPLATE, trimMaterial, suffix);
        uploadThreeLayerArmor(material, ArmorItem.Type.HELMET, trimMaterial, suffix);
    }


    private void uploadThreeLayerArmor(ArmorMaterial material, ArmorItem.Type type, ResourceLocation trimMaterial, String suffix) {
        ResourceLocation id = new ResourceLocation(modid, "item/%s_%s_%s_trim".formatted(material.getName(), type.getName(), trimMaterial.getPath()));
        ResourceLocation layer0Id = trackGeneratedTexture(new ResourceLocation("item/%s_%s".formatted(material.getName(), type.getName())));
        ResourceLocation layer1Id = trackGeneratedTexture(new ResourceLocation("item/%s_%s_overlay".formatted(material.getName(), type.getName())));
        ResourceLocation layer2Id = trackGeneratedTexture(new ResourceLocation("trims/items/%s_trim_%s".formatted(type.getName(), suffix)));
        withExistingParent(id.getPath(), "item/generated")
                .texture("layer0", layer0Id.toString())
                .texture("layer1", layer1Id.toString())
                .texture("layer2", layer2Id.toString());
    }

    private ResourceLocation trackGeneratedTexture(ResourceLocation location) {
        existingFileHelper.trackGenerated(location, PackType.CLIENT_RESOURCES, ".png", "textures");
        return location;
    }
}
