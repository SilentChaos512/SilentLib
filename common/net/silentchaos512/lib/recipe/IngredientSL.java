package net.silentchaos512.lib.recipe;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.util.StackHelper;

public class IngredientSL implements Predicate<ItemStack> {

  public static final IngredientSL EMPTY = new IngredientSL(new ItemStack[0]) {

    public boolean apply(@Nullable ItemStack p_apply_1_) {

      return StackHelper.isEmpty(p_apply_1_);
    }
  };

  private final ItemStack[] matchingStacks;

  protected IngredientSL(ItemStack... stacks) {

    this.matchingStacks = stacks;
  }

  public ItemStack[] getMatchingStacks()
  {
      return this.matchingStacks;
  }


  @Override
  public boolean apply(@Nullable ItemStack p_apply_1_) {

    if (p_apply_1_ == null) {
      return false;
    } else {
      for (ItemStack itemstack : this.matchingStacks) {
        if (itemstack.getItem() == p_apply_1_.getItem()) {
          int i = itemstack.getMetadata();

          if (i == 32767 || i == p_apply_1_.getMetadata()) {
            return true;
          }
        }
      }

      return false;
    }
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
