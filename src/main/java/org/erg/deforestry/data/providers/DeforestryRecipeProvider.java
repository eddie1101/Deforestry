package org.erg.deforestry.data.providers;

import org.erg.deforestry.Deforestry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import org.erg.deforestry.common.registries.DeforestryItems;

import java.util.concurrent.CompletableFuture;

public class DeforestryRecipeProvider extends RecipeProvider {

    public DeforestryRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, DeforestryItems.FELLING_AXE)
                .pattern(" bi")
                .pattern(" si")
                .pattern("s  ")
                .define('b', Items.IRON_BLOCK)
                .define('i', Items.IRON_INGOT)
                .define('s', Items.STICK)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, DeforestryItems.REMOTE_CHOPPER)
                .pattern("i a")
                .pattern("iii")
                .pattern("iri")
                .define('i', Items.IRON_INGOT)
                .define('a', Items.DIAMOND_AXE)
                .define('r', Items.REDSTONE)
                .unlockedBy(getHasName(Items.DIAMOND_AXE), has(Items.DIAMOND_AXE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, DeforestryItems.BOOMERANG)
                .pattern("sp ")
                .pattern("  p")
                .pattern("sp ")
                .define('s', ItemTags.WOODEN_SLABS)
                .define('p', ItemTags.PLANKS)
                .unlockedBy(getSimpleRecipeName(Items.OAK_LOG), has(ItemTags.WOODEN_SLABS))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, DeforestryItems.CHAINSAW)
                .pattern("i b")
                .pattern(" ig")
                .pattern(" gr")
                .define('b', ItemTags.STONE_BUTTONS)
                .define('g', Items.GOLD_INGOT)
                .define('i', Items.IRON_INGOT)
                .define('r', Items.REDSTONE)
                .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, DeforestryItems.BOOMERANG_CHOPPER)
                .requires(DeforestryItems.BOOMERANG)
                .requires(Items.DIAMOND_AXE)
                .unlockedBy(getHasName(Items.DIAMOND_AXE), has(Items.DIAMOND_AXE))
                .save(output);

    }

}
