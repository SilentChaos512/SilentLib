package net.silentchaos512.lib.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

/**
 * @deprecated Use IRecipeSL instead.
 */
@Deprecated
public class RecipeBase implements IRecipe {

  @Override
  public ItemStack getCraftingResult(InventoryCrafting inv) {

    // Override this method, and others if needed.
    return null;
  }

  @Override
  public boolean matches(InventoryCrafting inv, World worldIn) {

    return getCraftingResult(inv) != null;
  }

  @Override
  public int getRecipeSize() {

    return 9;
  }

  @Override
  public ItemStack getRecipeOutput() {

    return null;
  }

  @Override
  public ItemStack[] getRemainingItems(InventoryCrafting inv) {

    for (int i = 0; i < inv.getSizeInventory(); ++i) {
      ItemStack stack = inv.getStackInSlot(i);
      if (stack != null) {
        --stack.stackSize;
        if (stack.stackSize <= 0) {
          stack = null;
        }
        inv.setInventorySlotContents(i, stack);
      }
    }
    return new ItemStack[] {};
  }
}
