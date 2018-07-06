/*
 * SilentLib - RecipeBaseSL
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
import net.silentchaos512.lib.collection.ItemStackList;
import net.silentchaos512.lib.util.StackHelper;

public class RecipeBaseSL extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipeSL {

  @Override
  public ItemStack getCraftingResult(InventoryCrafting inv) {

    return StackHelper.empty();
  }

  @Override
  public boolean canFit(int width, int height) {

    return true;
  }

  @Override
  public boolean isDynamic() {

    return true;
  }

  // pre-1.12
  public int getRecipeSize() {

    return 10;
  }

  /**
   * Convenience method to make iterating a bit cleaner.
   * @param inv
   * @return
   */
  public static ItemStackList getNonEmptyStacks(InventoryCrafting inv) {

    ItemStackList list = ItemStackList.create();

    for (int i = 0; i < inv.getSizeInventory(); ++i) {
      ItemStack stack = inv.getStackInSlot(i);
      if (StackHelper.isValid(stack)) {
        list.add(stack);
      }
    }

    return list;
  }
}
