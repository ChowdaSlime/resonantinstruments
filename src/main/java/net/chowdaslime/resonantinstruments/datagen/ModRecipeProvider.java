package net.chowdaslime.resonantinstruments.datagen;

import net.chowdaslime.resonantinstruments.block.ModBlocks;
import net.chowdaslime.resonantinstruments.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
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
            return "Resonant Instruments Recipes";
        }
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.TOOLS, ModItems.UNATTUNED_FORK.get())
                .pattern(" A ")
                .pattern("B A")
                .pattern("CB ")
                .define('A', Items.IRON_BLOCK)
                .define('B', Items.IRON_INGOT)
                .define('C', Items.STICK)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(output);

        shaped(RecipeCategory.TOOLS, ModItems.CARVING_KNIFE.get())
                .pattern("  A")
                .pattern(" A ")
                .pattern("B  ")
                .define('A', Items.IRON_INGOT)
                .define('B', Items.STICK)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(output);

        shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.MARBLE.get())
                .requires(Items.STONE)
                .requires(Items.QUARTZ)
                .unlockedBy("has_stone", has(Items.STONE))
                .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.RUNED_MARBLE.get())
                .pattern("ABA")
                .define('A', Items.AMETHYST_SHARD)
                .define('B', ModBlocks.MARBLE.get())
                .unlockedBy("has_marble", has(ModBlocks.MARBLE.get()))
                .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.ENGRAVED_MARBLE.get())
                .pattern(" A ")
                .pattern("ABA")
                .pattern(" A ")
                .define('A', Items.AMETHYST_SHARD)
                .define('B', ModBlocks.MARBLE.get())
                .unlockedBy("has_marble", has(ModBlocks.MARBLE.get()))
                .save(output);
    }
}