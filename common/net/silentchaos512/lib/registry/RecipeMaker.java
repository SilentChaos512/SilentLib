package net.silentchaos512.lib.registry;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.silentchaos512.lib.recipe.IngredientSL;
import net.silentchaos512.lib.util.StackHelper;

/**
 * Used for wrapping recipe creation into a more convenient format.
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
    return resourcePrefix + key.toLowerCase();
  }

  /**********************************************************************************************************
   * Recipes adders and makers.
   * 
   * Adders will make and register a recipe. Makers just create a recipe (useful for guide book stuff, etc.)
   * 
   * Group will default to the mod ID if not specified.
   **********************************************************************************************************/

  // -------------------------------------------------- Shapeless --------------------------------------------------

  public IRecipe addShapeless(String key, @Nonnull ItemStack result, Object... inputs) {

    return addShapeless(modId, key, result, makeStackArray(inputs));
  }

  public IRecipe addShapeless(String group, String key, @Nonnull ItemStack result, Object... inputs) {

    return addShapeless(group, key, result, makeStackArray(inputs));
  }

  public IRecipe addShapeless(String key, @Nonnull ItemStack result, ItemStack... inputs) {

    return addShapeless(modId, key, result, inputs);
  }

  public IRecipe addShapeless(String group, String key, @Nonnull ItemStack result, ItemStack... inputs) {

    key = getRecipeKey(key);
    IRecipe recipe = makeShapeless(result, inputs);
    registerRecipe(new ResourceLocation(key), recipe);
    return recipe;
  }

  public IRecipe makeShapeless(@Nonnull ItemStack result, Object... inputs) {

    return makeShapeless(result, makeStackArray(inputs));
  }

  public IRecipe makeShapeless(String group, @Nonnull ItemStack result, Object... inputs) {

    return makeShapeless(result, makeStackArray(inputs));
  }

  public IRecipe makeShapeless(@Nonnull ItemStack result, ItemStack... inputs) {

    return makeShapeless(modId, result, inputs);
  }

  public IRecipe makeShapeless(String group, @Nonnull ItemStack result, ItemStack... inputs) {

    NonNullList<Ingredient> list = NonNullList.create();
    for (ItemStack stack : inputs)
      list.add(Ingredient.fromStacks(stack));
    return new ShapelessRecipes(group, result, list);
  }

  public IRecipe makeShapeless(@Nonnull ItemStack result, IngredientSL... inputs) {

    return makeShapeless(modId, result, inputs);
  }

  public IRecipe makeShapeless(String group, @Nonnull ItemStack result, IngredientSL... inputs) {

    NonNullList<Ingredient> list = NonNullList.create();
    for (IngredientSL ingredient : inputs)
      list.add(ingredient);
    return new ShapelessRecipes(group, result, list);
  }

  // -------------------------------------------------- Shaped --------------------------------------------------

  public IRecipe addShaped(String key, @Nonnull ItemStack result, Object... inputs) {

    return addShaped(modId, key, result, inputs);
  }

  public IRecipe addShaped(String group, String key, @Nonnull ItemStack result, Object... inputs) {

    key = getRecipeKey(key);
    IRecipe recipe = makeShaped(group, result, inputs);
    registerRecipe(new ResourceLocation(key), recipe);
    return recipe;
  }

  public IRecipe makeShaped(@Nonnull ItemStack result, Object... inputs) {

    return makeShaped(modId, result, inputs);
  }

  public IRecipe makeShaped(String group, @Nonnull ItemStack result, Object... inputs) {

    ShapedPrimer primer = CraftingHelper.parseShaped(inputs);
    return new ShapedRecipes(group, primer.width, primer.height, primer.input, result);
  }

  // -------------------------------------------------- Shapeless Ore --------------------------------------------------

  public IRecipe addShapelessOre(String key, @Nonnull ItemStack result, Object... inputs) {

    return addShapelessOre(modId, key, result, inputs);
  }

  public IRecipe addShapelessOre(String group, String key, @Nonnull ItemStack result, Object... inputs) {

    key = getRecipeKey(key);
    IRecipe recipe = makeShapelessOre(group, result, inputs);
    registerRecipe(new ResourceLocation(key), recipe);
    return recipe;
  }

  public IRecipe makeShapelessOre(@Nonnull ItemStack result, Object... inputs) {

    return makeShapelessOre(modId, result, inputs);
  }

  public IRecipe makeShapelessOre(String group, @Nonnull ItemStack result, Object... inputs) {

    return new ShapelessOreRecipe(new ResourceLocation(group), result, inputs);
  }

  // -------------------------------------------------- Shaped Ore --------------------------------------------------

  public IRecipe addShapedOre(String key, @Nonnull ItemStack result, Object... inputs) {

    return addShapedOre(modId, key, result, inputs);
  }

  public IRecipe addShapedOre(String group, String key, @Nonnull ItemStack result, Object... inputs) {

    key = getRecipeKey(key);
    IRecipe recipe = makeShapedOre(group, result, inputs);
    registerRecipe(new ResourceLocation(key), recipe);
    return recipe;
  }

  public IRecipe makeShapedOre(@Nonnull ItemStack result, Object... inputs) {

    return makeShapedOre(modId, result, inputs);
  }

  public IRecipe makeShapedOre(String group, @Nonnull ItemStack result, Object... inputs) {

    return new ShapedOreRecipe(new ResourceLocation(group), result, inputs);
  }

  // -------------------------------------------------- Smelting --------------------------------------------------

  public void addSmelting(Block input, @Nonnull ItemStack output, float xp) {

    GameRegistry.addSmelting(input, output, xp);
  }

  public void addSmelting(Item input, @Nonnull ItemStack output, float xp) {

    GameRegistry.addSmelting(input, output, xp);
  }

  public void addSmelting(@Nonnull ItemStack input, @Nonnull ItemStack output, float xp) {

    GameRegistry.addSmelting(input, output, xp);
  }

  // -------------------------------------------------- Generic --------------------------------------------------

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
   *          Registry name for the recipe. Appends "_compress" or "_decompress" for the appropriate recipes.
   * @param small
   *          The small stack (such as ingots).
   * @param big
   *          The big stack (such as blocks).
   * @param count
   *          The number of "small" needed to make a "big". Can be anything from 1 to 9.
   * @return Both created recipes in an array. First is compression, second is decompression. They are both
   *         ShapelessRecipes.
   */
  public IRecipe[] addCompression(String key, @Nonnull ItemStack small, @Nonnull ItemStack big, int count) {

    return addCompression(modId, key, small, big, count);
  }

  /**
   * Adds a compression recipe. For example, crafting ingots into blocks and vice versa. This will add both the
   * compression (small to big) and decompression (big to small) recipes.
   * 
   * @param group
   *          The recipe group.
   * @param key
   *          Registry name for the recipe. Appends "_compress" or "_decompress" for the appropriate recipes.
   * @param small
   *          The small stack (such as ingots).
   * @param big
   *          The big stack (such as blocks).
   * @param count
   *          The number of "small" needed to make a "big". Can be anything from 1 to 9.
   * @return Both created recipes in an array. First is compression, second is decompression. They are both
   *         ShapelessRecipes.
   */
  public IRecipe[] addCompression(String group, String key, @Nonnull ItemStack small, @Nonnull ItemStack big, int count) {

    IRecipe[] ret = new IRecipe[2];

    // Clamp to sane values.
    count = MathHelper.clamp(count, 1, 9);

    // small -> big
    ItemStack[] smallArray = new ItemStack[count];
    for (int i = 0; i < count; ++i) {
      smallArray[i] = small;
    }
    ret[0] = addShapeless(group, key + "_compress", big, smallArray);

    // big -> small
    ItemStack smallCopy = StackHelper.safeCopy(small);
    StackHelper.setCount(smallCopy, count);
    ret[1] = addShapeless(group, key + "_decompress", smallCopy, big);

    return ret;
  }

  /**
   * Adds one recipe consisting of a center item with 1-4 different items (2-8 of each) surrounding it.
   * 
   * @param key
   *          Registry name for the recipe.
   * @param output
   *          The item being crafted.
   * @param middleStack
   *          The item in the middle of the crafting grid.
   * @param surrounding
   *          The item(s) surrounding the middle item. Order affects the recipe.
   */
  public IRecipe addSurround(String key, ItemStack output, ItemStack middleStack, Object... surrounding) {

    return addSurround(modId, key, output, middleStack, surrounding);
  }

  /**
   * Adds one recipe consisting of a center item with 1-4 different items (2-8 of each) surrounding it.
   * 
   * @param group
   *          The recipe group.
   * @param key
   *          Registry name for the recipe.
   * @param output
   *          The item being crafted.
   * @param middleStack
   *          The item in the middle of the crafting grid.
   * @param surrounding
   *          The item(s) surrounding the middle item. Order affects the recipe.
   */
  public IRecipe addSurround(String group, String key, ItemStack output, ItemStack middleStack, Object... surrounding) {

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
        return addShaped(group, key, output, "xxx", "xcx", "xxx", 'c', middleStack, 'x', stacks[0]);
      case 2:
        return addShaped(group, key, output, "xyx", "ycy", "xyx", 'c', middleStack, 'x', stacks[0], 'y', stacks[1]);
      case 3:
        return addShaped(group, key, output, " xy", "zcz", "yx ", 'c', middleStack, 'x', stacks[0], 'y', stacks[1], 'z', stacks[2]);
      case 4:
        return addShaped(group, key, output, "xyz", "dcd", "zyx", 'c', middleStack, 'x', stacks[0], 'y', stacks[1], 'z', stacks[2], 'd', stacks[3]);
      default:
        // Too many things!
        throw new IllegalArgumentException("Too many items!");
    } //@formatter:on
  }

  /**
   * Adds one recipe consisting of a center item with 1-4 different items or oredict keys (2-8 of each) surrounding it.
   * 
   * @param key
   *          Registry name for the recipe.
   * @param output
   *          The item being crafted.
   * @param middleStack
   *          The item in the middle of the crafting grid.
   * @param surrounding
   *          The item(s) or oredict key(s) surrounding the middle item. Order affects the recipe.
   */
  public IRecipe addSurroundOre(String key, ItemStack output, Object middle, Object... surrounding) {

    return addSurroundOre(modId, key, output, middle, surrounding);
  }

  /**
   * Adds one recipe consisting of a center item with 1-4 different items or oredict keys (2-8 of each) surrounding it.
   * 
   * @param group
   *          The recipe group.
   * @param key
   *          Registry name for the recipe.
   * @param output
   *          The item being crafted.
   * @param middleStack
   *          The item in the middle of the crafting grid.
   * @param surrounding
   *          The item(s) or oredict key(s) surrounding the middle item. Order affects the recipe.
   */
  public IRecipe addSurroundOre(String group, String key, ItemStack output, Object middle, Object... surrounding) {

    switch (surrounding.length) { //@formatter:off
      case 0:
        // No surrounding stacks?
        throw new IllegalArgumentException("No surrounding items!");
      case 1:
        return addShapedOre(group, key, output, "xxx", "xcx", "xxx", 'c', middle, 'x', surrounding[0]);
      case 2:
        return addShapedOre(group, key, output, "xyx", "ycy", "xyx", 'c', middle, 'x', surrounding[0], 'y', surrounding[1]);
      case 3:
        return addShapedOre(group, key, output, " xy", "zcz", "yx ", 'c', middle, 'x', surrounding[0], 'y', surrounding[1], 'z', surrounding[2]);
      case 4:
        return addShapedOre(group, key, output, "xyz", "dcd", "zyx", 'c', middle, 'x', surrounding[0], 'y', surrounding[1], 'z', surrounding[2], 'd', surrounding[3]);
      default:
        // Too many things!
        throw new IllegalArgumentException("Too many items!");
    } //@formatter:on
  }

  /**
   * Registers a created recipe (called by adder methods, but not makers).
   * 
   * @param name
   *          Registry name for recipe.
   * @param recipe
   *          The recipe to register.
   */
  protected void registerRecipe(ResourceLocation name, IRecipe recipe) {

    if (recipe.getRegistryName() == null)
      recipe.setRegistryName(name);
    ForgeRegistries.RECIPES.register(recipe);
  }

  protected ItemStack[] makeStackArray(Object... params) {

    ItemStack[] result = new ItemStack[params.length];
    for (int i = 0; i < params.length; ++i) {
      Object obj = params[i];
      if (obj instanceof ItemStack)
        result[i] = (ItemStack) obj;
      else if (obj instanceof Item)
        result[i] = new ItemStack((Item) obj);
      else if (obj instanceof Block)
        result[i] = new ItemStack((Block) obj);
      else
        throw new IllegalArgumentException("Can't make object of type " + obj.getClass() + " into an ItemStack!");
    }
    return result;
  }
}
