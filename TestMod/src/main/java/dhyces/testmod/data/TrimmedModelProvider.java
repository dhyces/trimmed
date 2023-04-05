package dhyces.testmod.data;

import dhyces.testmod.TrimmedTest;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.HashSet;
import java.util.Set;

public class TrimmedModelProvider extends FabricModelProvider {
    static Set<ArmorMaterial> armorMaterials = Util.make(new HashSet<>(), set -> {
        set.add(ArmorMaterials.LEATHER);
        set.add(ArmorMaterials.IRON);
        set.add(ArmorMaterials.GOLD);
        set.add(ArmorMaterials.DIAMOND);
        set.add(ArmorMaterials.NETHERITE);
        set.add(ArmorMaterials.CHAIN);
        set.add(ArmorMaterials.TURTLE);
    });
    public TrimmedModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        createArmors(itemModelGenerator, TrimmedTest.id("blaze"), armorMaterials);
        createArmors(itemModelGenerator, TrimmedTest.id("echo"), armorMaterials);
        createArmors(itemModelGenerator, TrimmedTest.id("glow"), armorMaterials);
        createArmors(itemModelGenerator, TrimmedTest.id("prismarine"), armorMaterials);
        createArmors(itemModelGenerator, TrimmedTest.id("shell"), armorMaterials);
    }

    private void createArmors(ItemModelGenerator generator, Identifier trimMaterial, Set<ArmorMaterial> materials) {
        for (ArmorMaterial material : materials) {
            if (!(material == ArmorMaterials.LEATHER)) {
                createTwoLayerArmor(generator, material, trimMaterial);
            } else {
                createThreeLayerArmor(generator, material, trimMaterial);
            }
        }
    }

    private void createTwoLayerArmor(ItemModelGenerator generator, ArmorMaterial material, Identifier trimMaterial) {
        uploadTwoLayerArmor(generator, material, ArmorItem.Type.BOOTS, trimMaterial);
        uploadTwoLayerArmor(generator, material, ArmorItem.Type.LEGGINGS, trimMaterial);
        uploadTwoLayerArmor(generator, material, ArmorItem.Type.CHESTPLATE, trimMaterial);
        uploadTwoLayerArmor(generator, material, ArmorItem.Type.HELMET, trimMaterial);
    }


    private void uploadTwoLayerArmor(ItemModelGenerator generator, ArmorMaterial material, ArmorItem.Type type, Identifier trimMaterial) {
        String armorMaterialName = material == ArmorMaterials.GOLD ? "golden" : material.getName();
        Identifier id = new Identifier(trimMaterial.getNamespace(), "item/%s_%s_%s_trim".formatted(armorMaterialName, type.getName(), trimMaterial.getPath()));
        Identifier layer0Id = new Identifier("item/%s_%s".formatted(armorMaterialName, type.getName()));
        Identifier layer1Id = new Identifier("trims/items/%s_trim_%s".formatted(type.getName(), trimMaterial.getPath()));
        generator.uploadArmor(id, layer0Id, layer1Id);
    }

    private void createThreeLayerArmor(ItemModelGenerator generator, ArmorMaterial material, Identifier trimMaterial) {
        uploadThreeLayerArmor(generator, material, ArmorItem.Type.BOOTS, trimMaterial);
        uploadThreeLayerArmor(generator, material, ArmorItem.Type.LEGGINGS, trimMaterial);
        uploadThreeLayerArmor(generator, material, ArmorItem.Type.CHESTPLATE, trimMaterial);
        uploadThreeLayerArmor(generator, material, ArmorItem.Type.HELMET, trimMaterial);
    }


    private void uploadThreeLayerArmor(ItemModelGenerator generator, ArmorMaterial material, ArmorItem.Type type, Identifier trimMaterial) {
        Identifier id = new Identifier(trimMaterial.getNamespace(), "item/%s_%s_%s_trim".formatted(material.getName(), type.getName(), trimMaterial.getPath()));
        Identifier layer0Id = new Identifier("item/%s_%s".formatted(material.getName(), type.getName()));
        Identifier layer1Id = new Identifier("item/%s_%s_overlay".formatted(material.getName(), type.getName()));
        Identifier layer2Id = new Identifier("trims/items/%s_trim_%s".formatted(type.getName(), trimMaterial.getPath()));
        generator.uploadArmor(id, layer0Id, layer1Id, layer2Id);
    }
}
