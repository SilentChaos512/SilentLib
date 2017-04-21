package net.silentchaos512.lib.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RecipeHelper {

  /**
   * Adds two recipes, such as gems becoming blocks and vice versa, etc.
   * 
   * @param small
   *          The item being compressed (ie the gem/ingot/nugget)
   * @param big
   *          The compression result (ie the block)
   * @param count
   *          The number of "small" used to make "big". This should be either 4 or 9. Defaults to 9.
   */
  public static void addCompressionRecipe(ItemStack small, ItemStack big, int count) {

    if (count == 4) {
      GameRegistry.addShapelessRecipe(big, small, small, small, small);
    } else {
      GameRegistry.addShapelessRecipe(big, small, small, small, small, small, small, small, small,
          small);
    }
    ItemStack smallCopy = small.copy();
    smallCopy.setCount(count != 4 && count != 9 ? 9 : count);
    GameRegistry.addShapelessRecipe(smallCopy, big);
  }

  /**
   * Adds one recipe consisting of a center item with 1-4 different items (2-8 of each) surrounding it.
   * 
   * @param output
   *          The item being crafted.
   * @param middleStack
   *          The item in the middle of the crafting grid.
   * @param surrounding
   *          The item(s) surrounding the middle item. Order affects the recipe.
   */
  public static IRecipe addSurround(ItemStack output, ItemStack middleStack,
      Object... surrounding) {

    ItemStack[] stacks = new ItemStack[surrounding.length];

    int i = -1;
    for (Object obj : surrounding) {
      ++i;
      if (obj instanceof Block) {
        stacks[i] = new ItemStack((Block) obj);
      } else if (obj instanceof Item) {
        stacks[i] = new ItemStack((Item) obj);
      } else if (obj instanceof ItemStack) {
        stacks[i] = (ItemStack) obj;
      }
    }

    switch (surrounding.length) {
      case 0: {
        // No surrounding stacks?
        throw new IllegalArgumentException("No surrounding items!");
      }
      case 1: {
        return GameRegistry.addShapedRecipe(output, "xxx", "xcx", "xxx", 'c', middleStack, 'x',
            stacks[0]);
      }
      case 2: {
        return GameRegistry.addShapedRecipe(output, "xyx", "ycy", "xyx", 'c', middleStack, 'x',
            stacks[0], 'y', stacks[1]);
      }
      case 3: {
        return GameRegistry.addShapedRecipe(output, " xy", "zcz", "yx ", 'c', middleStack, 'x',
            stacks[0], 'y', stacks[1], 'z', stacks[2]);
      }
      case 4: {
        return GameRegistry.addShapedRecipe(output, "xyz", "dcd", "zyx", 'c', middleStack, 'x',
            stacks[0], 'y', stacks[1], 'z', stacks[2], 'd', stacks[3]);
      }
      default: {
        // Too many things!
        throw new IllegalArgumentException("Too many items!");
      }
    }
  }

  public static IRecipe addSurroundOre(ItemStack output, Object middle, Object... surrounding) {

    IRecipe recipe;
    switch (surrounding.length) {
      case 0:
        // No surrounding stacks?
        throw new IllegalArgumentException("No surrounding items!");
      case 1:
        recipe = new ShapedOreRecipe(output, "xxx", "xcx", "xxx", 'c', middle, 'x', surrounding[0]);
        break;
      case 2:
        recipe = new ShapedOreRecipe(output, "xyx", "ycy", "xyx", 'c', middle, 'x', surrounding[0],
            'y', surrounding[1]);
        break;
      case 3:
        recipe = new ShapedOreRecipe(output, " xy", "zcz", "yx ", 'c', middle, 'x', surrounding[0],
            'y', surrounding[1], 'z', surrounding[2]);
        break;
      case 4:
        recipe = new ShapedOreRecipe(output, "xyz", "dcd", "zyx", 'c', middle, 'x', surrounding[0],
            'y', surrounding[1], 'z', surrounding[2], 'd', surrounding[3]);
        break;
      default:
        // Too many things!
        throw new IllegalArgumentException("Too many items!");
    }

    if (recipe != null)
      GameRegistry.addRecipe(recipe);
    return recipe;
  }
}
