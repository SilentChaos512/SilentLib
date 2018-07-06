/*
 * SilentLib - IRecipeSL
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.lib.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.silentchaos512.lib.util.StackHelper;

import javax.annotation.Nonnull;
import java.util.List;

public interface IRecipeSL extends IRecipe {

  @Override
  default boolean matches(InventoryCrafting inv, World world) {

    return StackHelper.isValid(getCraftingResult(inv));
  }

//  @Override
//  public default int getRecipeSize() {
//
//    // Prioritize over all normal recipes.
//    return 10;
//  }

  @Override
  public default @Nonnull ItemStack getRecipeOutput() {

    return StackHelper.empty();
  }

  @Override
  public default NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {

    return (NonNullList<ItemStack>) getRemainingItemsCompat(inv);
  }

  @Nonnull
  default List<ItemStack> getRemainingItemsCompat(InventoryCrafting inv) {

    for (int i = 0; i < inv.getSizeInventory(); ++i) {
      ItemStack stack = inv.getStackInSlot(i);
      if (StackHelper.isValid(stack) && !stack.getItem().hasContainerItem(stack)) {
        stack = StackHelper.shrink(stack, 1);
        inv.setInventorySlotContents(i, stack);
      }
    }
    return NonNullList.create();
  }
}
