package dhyces.testmod.data.chunked;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SmithingTrimRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

public class ChunkedRecipeProvider extends RecipeProvider {
    public ChunkedRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        SmithingTrimRecipeBuilder.smithingTrim(Ingredient.of(Items.GRASS_BLOCK), Ingredient.of(ItemTags.TRIMMABLE_ARMOR), Ingredient.of(ItemTags.TRIM_MATERIALS), RecipeCategory.COMBAT)
                .unlocks("has_smithing_trim_template", has(Items.GRASS_BLOCK))
                .save(pWriter, new ResourceLocation("chunkedpattern", "chunked_grass_block_smithing_trim"));
    }
}
