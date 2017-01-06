package net.silentchaos512.lib.recipe;

import javax.annotation.Nonnull;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;


public class RecipeBase implements IRecipe {

  @Override
  public @Nonnull ItemStack getCraftingResult(InventoryCrafting inv) {

    return ItemStack.EMPTY;
  }

  @Override
  public boolean matches(InventoryCrafting inv, World worldIn) {

    return !getCraftingResult(inv).isEmpty();
  }

  @Override
  public int getRecipeSize() {

    return 10;
  }

  @Override
  public @Nonnull ItemStack getRecipeOutput() {

    return ItemStack.EMPTY;
  }

  @Override
  public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {

    for (int i = 0; i < inv.getSizeInventory(); ++i) {
      ItemStack stack = inv.getStackInSlot(i);
      if (!stack.isEmpty()) {
        stack.shrink(1);
        inv.setInventorySlotContents(i, stack);
      }
    }
    return NonNullList.create();
  }
}
