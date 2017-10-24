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
  public boolean isHidden() {

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
