package net.silentchaos512.lib.data.recipe;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.silentchaos512.lib.crafting.recipe.DamageItemRecipe;

public class DamageItemRecipeBuilder extends ExtendedShapelessRecipeBuilder {
    protected int damage = 1;

    protected DamageItemRecipeBuilder(RecipeSerializer<?> serializer, RecipeCategory category, ItemLike result, int count) {
        super(serializer, category, result, count);
    }

    public static DamageItemRecipeBuilder builder(RecipeCategory category, ItemLike result) {
        return builder(category, result, 1);
    }

    public static DamageItemRecipeBuilder builder(RecipeCategory category, ItemLike result, int count) {
        return builder(DamageItemRecipe.SERIALIZER, category, result, count);
    }

    public static DamageItemRecipeBuilder builder(RecipeSerializer<?> serializer, RecipeCategory category, ItemLike result) {
        return builder(serializer, category, result, 1);
    }

    public static DamageItemRecipeBuilder builder(RecipeSerializer<?> serializer, RecipeCategory category, ItemLike result, int count) {
        return new DamageItemRecipeBuilder(serializer, category, result, count);
    }

    @Override
    protected void serializeExtra(JsonObject json) {
        json.addProperty("damage", this.damage);
        super.serializeExtra(json);
    }

    public DamageItemRecipeBuilder damageToItems(int damage) {
        this.damage = damage;
        return this;
    }
}
