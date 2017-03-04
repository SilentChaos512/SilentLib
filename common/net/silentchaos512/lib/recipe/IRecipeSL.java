package net.silentchaos512.lib.recipe;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
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
  public default NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {

//    for (int i = 0; i < inv.getSizeInventory(); ++i) {
//      ItemStack stack = inv.getStackInSlot(i);
//      if (StackHelper.isValid(stack)) {
//        stack = StackHelper.shrink(stack, 1);
//        inv.setInventorySlotContents(i, stack);
//      }
//    }
//    return NonNullList.create();
    return (NonNullList<ItemStack>) getRemainingItemsCompat(inv);
  }

  @Nonnull
  default List<ItemStack> getRemainingItemsCompat(InventoryCrafting inv) {

    return NonNullList.create();
  }
}
