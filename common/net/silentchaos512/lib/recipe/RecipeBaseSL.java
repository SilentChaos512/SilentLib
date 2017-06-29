package net.silentchaos512.lib.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
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
}
