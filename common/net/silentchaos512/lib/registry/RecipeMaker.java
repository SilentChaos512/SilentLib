package net.silentchaos512.lib.registry;

import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.silentchaos512.lib.util.StackHelper;
import scala.NotImplementedError;

/**
 * TODO
 * 
 * @author SilentChaos512
 * @since 2.2.0
 */
public class RecipeMaker {

  protected String modId;
  protected String resourcePrefix;
  protected int lastRecipeIndex = -1;

  public RecipeMaker(String modId) {

    this.modId = modId;
    resourcePrefix = modId + ":";
  }

  protected String getRecipeKey(String key) {

    if (key == null || key.isEmpty())
      key = "recipe" + (++lastRecipeIndex);
    return resourcePrefix + key;
  }

  /**********************************************************************************************************
   * Recipes adders and makers.
   * 
   * Adders will make and register a recipe. Makers just create a recipe (useful for guide book stuff, etc.)
   **********************************************************************************************************/

  public IRecipe addShapeless(String key, @Nonnull ItemStack result, ItemStack... inputs) {

    key = getRecipeKey(key);
    IRecipe recipe = makeShapeless(result, inputs);
    registerRecipe(new ResourceLocation(key), recipe);
    return recipe;
  }

  public IRecipe makeShapeless(@Nonnull ItemStack result, ItemStack... inputs) {

    return Utils.addShapelessRecipe(modId, result, inputs);
  }

  public IRecipe addShaped(String key, @Nonnull ItemStack result, Object... inputs) {

    key = getRecipeKey(key);
    IRecipe recipe = Utils.addRecipe(key, result, inputs);
    registerRecipe(new ResourceLocation(key), recipe);
    return recipe;
  }

  public IRecipe makeShaped(@Nonnull ItemStack result, Object... inputs) {

    return Utils.addRecipe(modId, result, inputs);
  }

  public IRecipe addShapelessOre(String key, @Nonnull ItemStack result, Object... inputs) {

    key = getRecipeKey(key);
    IRecipe recipe = makeShapelessOre(result, inputs);
    registerRecipe(new ResourceLocation(key), recipe);
    return recipe;
  }

  public IRecipe makeShapelessOre(@Nonnull ItemStack result, Object... inputs) {

    return new ShapedOreRecipe(new ResourceLocation(modId), result, inputs);
  }

  public IRecipe addShapedOre(String key, @Nonnull ItemStack result, Object... inputs) {

    key = getRecipeKey(key);
    IRecipe recipe = makeShapedOre(result, inputs);
    registerRecipe(new ResourceLocation(key), recipe);
    return recipe;
  }

  public IRecipe makeShapedOre(@Nonnull ItemStack result, Object... inputs) {

    return new ShapelessOreRecipe(new ResourceLocation(modId), result, inputs);
  }

  public IRecipe addRecipe(String key, IRecipe recipe) {

    key = getRecipeKey(key);
    registerRecipe(new ResourceLocation(key), recipe);
    return recipe;
  }

  /***************************************************************************************************************
   * Convenience recipe makers that simplify adding some common recipe types. These only have adders, not makers.
   ***************************************************************************************************************/

  /**
   * Adds a compression recipe. For example, crafting ingots into blocks and vice versa. This will add both the
   * compression (small to big) and decompression (big to small) recipes.
   * 
   * @param key
   *          Recipe key(?). Appends "_compress" or "_decompress" for the appropriate recipes.
   * @param small
   *          The small stack (such as ingots).
   * @param big
   *          The big stack (such as blocks).
   * @param count
   *          The number of "small" needed to make a "big". Can be anything from 1 to 9.
   * @return Both created recipes in an array. First is compression, second is decompression. They are both
   *         ShapelessRecipes.
   */
  public IRecipe[] addCompression(String key, @Nonnull ItemStack small, @Nonnull ItemStack big,
      int count) {

    IRecipe[] ret = new IRecipe[2];

    // Clamp to sane values.
    count = MathHelper.clamp(count, 1, 9);

    // small -> big
    ItemStack[] smallArray = new ItemStack[count];
    for (int i = 0; i < count; ++i) {
      smallArray[i] = small;
    }
    ret[0] = addShapeless(key + "_compress", big, smallArray);

    // big -> small
    ItemStack smallCopy = StackHelper.safeCopy(small);
    StackHelper.setCount(smallCopy, count);
    ret[1] = addShapeless(key + "_decompress", smallCopy, big);

    return ret;
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
  public IRecipe addSurround(String key, ItemStack output, ItemStack middleStack,
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

    switch (surrounding.length) { //@formatter:off
      case 0:
        // No surrounding stacks?
        throw new IllegalArgumentException("No surrounding items!");
      case 1:
        return addShaped(key, output, "xxx", "xcx", "xxx", 'c', middleStack, 'x', stacks[0]);
      case 2:
        return addShaped(key, output, "xyx", "ycy", "xyx", 'c', middleStack, 'x', stacks[0], 'y', stacks[1]);
      case 3:
        return addShaped(key, output, " xy", "zcz", "yx ", 'c', middleStack, 'x', stacks[0], 'y', stacks[1], 'z', stacks[2]);
      case 4:
        return addShaped(key, output, "xyz", "dcd", "zyx", 'c', middleStack, 'x', stacks[0], 'y', stacks[1], 'z', stacks[2], 'd', stacks[3]);
      default:
        // Too many things!
        throw new IllegalArgumentException("Too many items!");
    } //@formatter:on
  }

  /**
   * Adds one recipe consisting of a center item with 1-4 different items or oredict keys (2-8 of each) surrounding it.
   * 
   * @param output
   *          The item being crafted.
   * @param middleStack
   *          The item in the middle of the crafting grid.
   * @param surrounding
   *          The item(s) or oredict key(s) surrounding the middle item. Order affects the recipe.
   */
  public IRecipe addSurroundOre(String key, ItemStack output, Object middle,
      Object... surrounding) {

    switch (surrounding.length) { //@formatter:off
      case 0:
        // No surrounding stacks?
        throw new IllegalArgumentException("No surrounding items!");
      case 1:
        return addShapedOre(key, output, "xxx", "xcx", "xxx", 'c', middle, 'x', surrounding[0]);
      case 2:
        return addShapedOre(key, output, "xyx", "ycy", "xyx", 'c', middle, 'x', surrounding[0], 'y', surrounding[1]);
      case 3:
        return addShapedOre(key, output, " xy", "zcz", "yx ", 'c', middle, 'x', surrounding[0], 'y', surrounding[1], 'z', surrounding[2]);
      case 4:
        return addShapedOre(key, output, "xyz", "dcd", "zyx", 'c', middle, 'x', surrounding[0], 'y', surrounding[1], 'z', surrounding[2], 'd', surrounding[3]);
      default:
        // Too many things!
        throw new IllegalArgumentException("Too many items!");
    } //@formatter:on
  }

  /**
   * Registers a created recipe (called by adder methods). This method may be removed, as it is a workaround for early
   * Forge 1.12 versions.
   * 
   * @param res Registry name for recipe?
   * @param recipe The recipe to register.
   */
  protected void registerRecipe(ResourceLocation res, IRecipe recipe) {

    recipe.setRegistryName(res);
    GameData.getRecipeRegistry().register(recipe);
  }

  /**
   * Holds code for adding recipes. Currently, this is a workaround for Forge 1.12 being incomplete. May be removed in a
   * later version.
   * 
   * @author SilentChaos512
   * @since 2.2.0
   */
  public static class Utils {

    public static ShapelessRecipes addShapelessRecipe(String group, ItemStack output,
        ItemStack... params) {

      NonNullList<Ingredient> list = NonNullList.create();
      for (ItemStack stack : params) {
        list.add(Ingredient.fromStacks(stack));
      }
      return new ShapelessRecipes(group, output, list);
    }

    public static ShapedRecipes addRecipe(String group, ItemStack stack,
        Object... recipeComponents) {

      String s = "";
      int i = 0;
      int j = 0;
      int k = 0;

      if (recipeComponents[i] instanceof String[]) {
        String[] astring = (String[]) ((String[]) recipeComponents[i++]);

        for (String s2 : astring) {
          ++k;
          j = s2.length();
          s = s + s2;
        }
      } else {
        while (recipeComponents[i] instanceof String) {
          String s1 = (String) recipeComponents[i++];
          ++k;
          j = s1.length();
          s = s + s1;
        }
      }

      Map<Character, ItemStack> map;

      for (map = Maps.<Character, ItemStack> newHashMap(); i < recipeComponents.length; i += 2) {
        Character character = (Character) recipeComponents[i];
        ItemStack itemstack = ItemStack.EMPTY;

        if (recipeComponents[i + 1] instanceof Item) {
          itemstack = new ItemStack((Item) recipeComponents[i + 1]);
        } else if (recipeComponents[i + 1] instanceof Block) {
          itemstack = new ItemStack((Block) recipeComponents[i + 1], 1, 32767);
        } else if (recipeComponents[i + 1] instanceof ItemStack) {
          itemstack = (ItemStack) recipeComponents[i + 1];
        }

        map.put(character, itemstack);
      }

      ItemStack[] aitemstack = new ItemStack[j * k];

      for (int l = 0; l < j * k; ++l) {
        char c0 = s.charAt(l);

        if (map.containsKey(Character.valueOf(c0))) {
          aitemstack[l] = ((ItemStack) map.get(Character.valueOf(c0))).copy();
        } else {
          aitemstack[l] = ItemStack.EMPTY;
        }
      }

      NonNullList<Ingredient> ingredients = NonNullList.create();
      for (ItemStack itemstack : aitemstack) {
        if (StackHelper.isValid(itemstack)) {
          ingredients.add(Ingredient.fromStacks(itemstack));
        }
      }

      return new ShapedRecipes(group, j, k, ingredients, stack);
    }
  }
}
