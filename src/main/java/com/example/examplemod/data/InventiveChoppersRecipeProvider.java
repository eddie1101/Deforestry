package com.example.examplemod.data;

import com.example.examplemod.InventiveChoppers;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;

import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class InventiveChoppersRecipeProvider extends RecipeProvider {

    public InventiveChoppersRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, InventiveChoppers.FELLING_AXE)
                .pattern(" bi")
                .pattern(" si")
                .pattern("s  ")
                .define('b', Items.IRON_BLOCK)
                .define('i', Items.IRON_INGOT)
                .define('s', Items.STICK)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, InventiveChoppers.REMOTE_CHOPPER)
                .pattern("i a")
                .pattern("iii")
                .pattern("iri")
                .define('i', Items.IRON_INGOT)
                .define('a', Items.DIAMOND_AXE)
                .define('r', Items.REDSTONE)
                .unlockedBy(getHasName(Items.DIAMOND_AXE), has(Items.DIAMOND_AXE))
                .save(output);

    }

}
