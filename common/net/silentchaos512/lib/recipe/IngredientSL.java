package net.silentchaos512.lib.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.silentchaos512.lib.util.StackHelper;

public class IngredientSL extends Ingredient {

  public static final IngredientSL EMPTY = new IngredientSL(new ItemStack[0]);

  public IngredientSL(ItemStack... stacks) {

    super(stacks);
  }

  public static IngredientSL from(Item... items) {

    ItemStack[] aitemstack = new ItemStack[items.length];

    for (int i = 0; i < items.length; ++i)
    {
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
