package net.silentchaos512.lib.crafting.recipe;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingRecipe;

public interface CraftingRecipeExtension extends CraftingRecipe {
    ItemStackTemplate getResultForDisplay();
}
