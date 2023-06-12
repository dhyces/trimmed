package dhyces.testmod.item;

import dhyces.testmod.TrimmedTest;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public class TestArmorItem extends ArmorItem {
    public TestArmorItem(Type type) {
        super(TestArmorMaterial.INSTANCE, type, new Properties().stacksTo(1));
    }

    public enum TestArmorMaterial implements ArmorMaterial {
        INSTANCE;

        @Override
        public int getDurabilityForType(Type p_266807_) {
            return 9999;
        }

        @Override
        public int getDefenseForType(Type p_267168_) {
            return 4;
        }

        @Override
        public int getEnchantmentValue() {
            return 2;
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ALLAY_ITEM_GIVEN;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(ItemTags.TRIM_MATERIALS);
        }

        @Override
        public String getName() {
            return TrimmedTest.MODID + ":test";
        }

        @Override
        public float getToughness() {
            return 3;
        }

        @Override
        public float getKnockbackResistance() {
            return 5;
        }
    }
}
