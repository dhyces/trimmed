package dhyces.testmod.data;

import dhyces.testmod.ModItems;
import dhyces.testmod.TrimmedTest;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SmithingTrimRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

public class TestRecipeProvider extends RecipeProvider {
    public TestRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        SmithingTrimRecipeBuilder.smithingTrim(Ingredient.of(ModItems.SPIRAL_PATTERN.get()), Ingredient.of(ItemTags.TRIMMABLE_ARMOR), Ingredient.of(ItemTags.TRIM_MATERIALS), RecipeCategory.COMBAT).unlocks("test_advancement", has(ModItems.SPIRAL_PATTERN.get())).save(pWriter, TrimmedTest.id("spiral_armor_trim_smithing_template_smithing_trim"));
    }
}
