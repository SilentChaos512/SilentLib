/*
 * SilentLib - IngredientSL
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

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.silentchaos512.lib.util.StackHelper;

import javax.annotation.Nullable;

/**
 * An Ingredient wrapper for cross compatibility. By passing instances of IngredientSL into the RecipeMaker, you can add
 * ingredient-based recipes without writing version-specific code. Note that only the first stack is considered in
 * pre-1.12 versions, so it's not a perfect solution.
 *
 * @author SilentChaos512
 * @since 2.2.5
 */
@Deprecated
public class IngredientSL extends Ingredient {

  public static final IngredientSL EMPTY = new IngredientSL(new ItemStack[0]) {

    public boolean apply(@Nullable ItemStack p_apply_1_) {

      return p_apply_1_.isEmpty();
    }
  };

  protected IngredientSL(ItemStack... stacks) {

    super(stacks);
  }

  public static IngredientSL from(Item... items) {

    ItemStack[] aitemstack = new ItemStack[items.length];

    for (int i = 0; i < items.length; ++i) {
      aitemstack[i] = new ItemStack(items[i]);
    }

    return from(aitemstack);
  }

  public static IngredientSL from(ItemStack... stacks) {

    if (stacks.length > 0) {
      for (ItemStack stack : stacks) {
        if (StackHelper.isValid(stack)) {
          return new IngredientSL(stacks);
        }
      }
    }

    return EMPTY;
  }
}
