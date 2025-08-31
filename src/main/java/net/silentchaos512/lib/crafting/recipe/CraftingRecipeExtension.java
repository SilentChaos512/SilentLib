package net.silentchaos512.lib.crafting.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public interface CraftingRecipeExtension extends CraftingRecipe {
    ItemStack getResultForDisplay();

    List<Ingredient> getIngredientsForDisplay();
}
