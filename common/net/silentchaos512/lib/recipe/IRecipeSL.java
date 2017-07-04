package net.silentchaos512.lib.recipe;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.silentchaos512.lib.util.StackHelper;

public interface IRecipeSL extends IRecipe {

  @Override
  default boolean matches(InventoryCrafting inv, World world) {

    return StackHelper.isValid(getCraftingResult(inv));
  }

  @Override
  public default int getRecipeSize() {

    // Prioritize over all normal recipes.
    return 10;
  }

  @Override
  public default @Nonnull ItemStack getRecipeOutput() {

    return StackHelper.empty();
  }

  @Override
  public default ItemStack[] getRemainingItems(InventoryCrafting inv) {

    List<ItemStack> list = getRemainingItemsCompat(inv);
    return list.toArray(new ItemStack[list.size()]);
  }

  @Nonnull
  default List<ItemStack> getRemainingItemsCompat(InventoryCrafting inv) {

    for (int i = 0; i < inv.getSizeInventory(); ++i) {
      ItemStack stack = inv.getStackInSlot(i);
      if (StackHelper.isValid(stack)) {
        stack = StackHelper.shrink(stack, 1);
        inv.setInventorySlotContents(i, stack);
      }
    }
    return new ArrayList<>();
  }
}
