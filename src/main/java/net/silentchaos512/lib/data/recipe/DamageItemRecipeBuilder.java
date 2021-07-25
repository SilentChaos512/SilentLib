package net.silentchaos512.lib.data.recipe;

import com.google.gson.JsonObject;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.silentchaos512.lib.crafting.recipe.DamageItemRecipe;

public class DamageItemRecipeBuilder extends ExtendedShapelessRecipeBuilder {
    protected int damage = 1;

    protected DamageItemRecipeBuilder(RecipeSerializer<?> serializer, ItemLike result, int count) {
        super(serializer, result, count);
    }

    public static DamageItemRecipeBuilder builder(ItemLike result) {
        return builder(result, 1);
    }

    public static DamageItemRecipeBuilder builder(ItemLike result, int count) {
        return builder(DamageItemRecipe.SERIALIZER, result, count);
    }

    public static DamageItemRecipeBuilder builder(RecipeSerializer<?> serializer, ItemLike result) {
        return builder(serializer, result, 1);
    }

    public static DamageItemRecipeBuilder builder(RecipeSerializer<?> serializer, ItemLike result, int count) {
        return new DamageItemRecipeBuilder(serializer, result, count);
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
