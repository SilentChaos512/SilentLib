package net.silentchaos512.lib.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.lib.util.StackHelper;

public class RecipeBaseSL implements IRecipeSL {

  @Override
  public ItemStack getCraftingResult(InventoryCrafting inv) {

    return StackHelper.empty();
  }

  @Override
  public boolean canFit(int width, int height) {

    return true;
  }

  /*
   * I assume these will be removed in later Forge versions?
   */

  ResourceLocation registryName;

  @Override
  public IRecipe setRegistryName(ResourceLocation name) {

    registryName = name;
    return this;
  }

  @Override
  public ResourceLocation getRegistryName() {

    return registryName;
  }

  @Override
  public Class<IRecipe> getRegistryType() {

    return (Class<IRecipe>) getClass();
  }

}
