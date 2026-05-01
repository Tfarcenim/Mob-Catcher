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
    protected ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }



    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.MISC, ModItems.net_item)
                .define('w', Tags.Items.INGOTS_IRON)
                .define('e',Items.ENDER_PEARL)
                .pattern(" w ")
                .pattern("wew")
                .pattern(" w ")
                .unlockedBy(getHasName(Items.ENDER_PEARL),has(Items.ENDER_PEARL))
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.net_launcher)
                .define('i', Tags.Items.INGOTS_IRON)
                .define('b',Items.ENDER_PEARL)
                .define('e',Items.BOW)
                .pattern("iii")
                .pattern(" eb")
                .pattern("iii")
                .unlockedBy(getHasName(Items.ENDER_PEARL),has(Items.ENDER_PEARL))
                .save(output);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new ModRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Mob Catcher Recipes";
        }
    }
}
