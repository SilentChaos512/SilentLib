package net.silentchaos512.lib.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.silentchaos512.lib.util.StackHelper;

public class RecipeBaseSL implements IRecipeSL {

  @Override
  public ItemStack getCraftingResult(InventoryCrafting inv) {

    return StackHelper.empty();
  }

  //@Override // 1.12
  public boolean canFit(int width, int height) {

    return true;
  }

  //@Override // 1.12
  public boolean isHidden() {

    return true;
  }

  @Override
  public int getRecipeSize() {

    return 10;
  }
}
