package dhyces.testmod.data;

import dhyces.testmod.ModItems;
import dhyces.testmod.TrimmedTest;
import net.minecraft.Util;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.HashSet;
import java.util.Set;

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

    public TestModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        createArmors(TrimmedTest.id("blaze"), armorMaterials);
        createArmors(TrimmedTest.id("echo"), armorMaterials);
        createArmors(TrimmedTest.id("glow"), armorMaterials);
        createArmors(TrimmedTest.id("prismarine"), armorMaterials);
        createArmors(TrimmedTest.id("shell"), armorMaterials);

        basicItem(ModItems.SPIRAL_PATTERN.get());
    }

    private void createArmors(ResourceLocation trimMaterial, Set<ArmorMaterial> materials) {
        for (ArmorMaterial material : materials) {
            if (!(material == ArmorMaterials.LEATHER)) {
                createTwoLayerArmor(material, trimMaterial);
            } else {
                createThreeLayerArmor(material, trimMaterial);
            }
        }
    }

    private void createTwoLayerArmor(ArmorMaterial material, ResourceLocation trimMaterial) {
        uploadTwoLayerArmor(material, ArmorItem.Type.BOOTS, trimMaterial);
        uploadTwoLayerArmor(material, ArmorItem.Type.LEGGINGS, trimMaterial);
        uploadTwoLayerArmor(material, ArmorItem.Type.CHESTPLATE, trimMaterial);
        uploadTwoLayerArmor(material, ArmorItem.Type.HELMET, trimMaterial);
    }


    private void uploadTwoLayerArmor(ArmorMaterial material, ArmorItem.Type type, ResourceLocation trimMaterial) {
        String armorMaterialName = material == ArmorMaterials.GOLD ? "golden" : material.getName();
        ResourceLocation id = new ResourceLocation(trimMaterial.getNamespace(), "item/%s_%s_%s_trim".formatted(armorMaterialName, type.getName(), trimMaterial.getPath()));
        ResourceLocation layer0Id = new ResourceLocation("item/%s_%s".formatted(armorMaterialName, type.getName()));
        ResourceLocation layer1Id = new ResourceLocation("trims/items/%s_trim_%s".formatted(type.getName(), trimMaterial.getPath()));
        withExistingParent(id.getPath(), "item/generated")
                .texture("layer0", layer0Id.toString())
                .texture("layer1", layer1Id.toString());

    }

    private void createThreeLayerArmor(ArmorMaterial material, ResourceLocation trimMaterial) {
        uploadThreeLayerArmor(material, ArmorItem.Type.BOOTS, trimMaterial);
        uploadThreeLayerArmor(material, ArmorItem.Type.LEGGINGS, trimMaterial);
        uploadThreeLayerArmor(material, ArmorItem.Type.CHESTPLATE, trimMaterial);
        uploadThreeLayerArmor(material, ArmorItem.Type.HELMET, trimMaterial);
    }


    private void uploadThreeLayerArmor(ArmorMaterial material, ArmorItem.Type type, ResourceLocation trimMaterial) {
        ResourceLocation id = new ResourceLocation(trimMaterial.getNamespace(), "item/%s_%s_%s_trim".formatted(material.getName(), type.getName(), trimMaterial.getPath()));
        ResourceLocation layer0Id = new ResourceLocation("item/%s_%s".formatted(material.getName(), type.getName()));
        ResourceLocation layer1Id = new ResourceLocation("item/%s_%s_overlay".formatted(material.getName(), type.getName()));
        ResourceLocation layer2Id = new ResourceLocation("trims/items/%s_trim_%s".formatted(type.getName(), trimMaterial.getPath()));
        withExistingParent(id.getPath(), "item/generated")
                .texture("layer0", layer0Id.toString())
                .texture("layer1", layer1Id.toString())
                .texture("layer2", layer2Id.toString());
    }
}
