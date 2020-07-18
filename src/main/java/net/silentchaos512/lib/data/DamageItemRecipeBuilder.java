package net.silentchaos512.lib.data;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.IItemProvider;
import net.silentchaos512.lib.crafting.recipe.DamageItemRecipe;

public class DamageItemRecipeBuilder extends ExtendedShapelessRecipeBuilder {
    protected int damage = 1;

    protected DamageItemRecipeBuilder(IRecipeSerializer<?> serializer, IItemProvider result, int count) {
        super(serializer, result, count);
    }

    public static DamageItemRecipeBuilder builder(IItemProvider result) {
        return builder(result, 1);
    }

    public static DamageItemRecipeBuilder builder(IItemProvider result, int count) {
        return builder(DamageItemRecipe.SERIALIZER, result, count);
    }

    public static DamageItemRecipeBuilder builder(IRecipeSerializer<?> serializer, IItemProvider result) {
        return builder(serializer, result, 1);
    }

    public static DamageItemRecipeBuilder builder(IRecipeSerializer<?> serializer, IItemProvider result, int count) {
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
