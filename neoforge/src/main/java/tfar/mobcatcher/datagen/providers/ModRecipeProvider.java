package tfar.mobcatcher.datagen.providers;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import tfar.mobcatcher.ModItems;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput pGenerator, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(pGenerator,lookupProvider);
    }


    @Override
    protected void buildRecipes(RecipeOutput pRecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.net_item)
                .define('w', Tags.Items.INGOTS_IRON)
                .define('e',Items.ENDER_PEARL)
                .pattern(" w ")
                .pattern("wew")
                .pattern(" w ")
                .unlockedBy(getHasName(Items.ENDER_PEARL),has(Items.ENDER_PEARL))
                .save(pRecipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.net_launcher)
                .define('i', Tags.Items.INGOTS_IRON)
                .define('b',Items.ENDER_PEARL)
                .define('e',Items.BOW)
                .pattern("iii")
                .pattern(" eb")
                .pattern("iii")
                .unlockedBy(getHasName(Items.ENDER_PEARL),has(Items.ENDER_PEARL))
                .save(pRecipeOutput);
    }
}
