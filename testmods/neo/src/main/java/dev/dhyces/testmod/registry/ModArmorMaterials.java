package dev.dhyces.testmod.registry;

import dev.dhyces.testmod.TrimmedTest;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> REGISTER = DeferredRegister.create(Registries.ARMOR_MATERIAL, TrimmedTest.MODID);

    public static final Holder<ArmorMaterial> ADAMANTIUM = REGISTER.register("adamantium", () -> new ArmorMaterial(
            Util.make(new Object2IntOpenHashMap<>(), map -> map.defaultReturnValue(4)),
            2, SoundEvents.ARMOR_EQUIP_DIAMOND, () -> Ingredient.of(ModItems.ADAMANTIUM.get()), List.of(new ArmorMaterial.Layer(TrimmedTest.id("adamantium"))), 3, 5
    ));
}
