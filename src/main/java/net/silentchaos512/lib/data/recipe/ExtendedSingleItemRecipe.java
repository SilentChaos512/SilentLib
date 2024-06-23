package net.silentchaos512.lib.data.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class ExtendedSingleItemRecipe extends SingleItemRecipe {
    public ExtendedSingleItemRecipe(RecipeType<?> pType, RecipeSerializer<?> pSerializer, String pGroup, Ingredient pIngredient, ItemStack pResult) {
        super(pType, pSerializer, pGroup, pIngredient, pResult);
    }

    @Override
    public boolean matches(SingleRecipeInput pContainer, Level pLevel) {
        return this.ingredient.test(pContainer.getItem(0));
    }
}
