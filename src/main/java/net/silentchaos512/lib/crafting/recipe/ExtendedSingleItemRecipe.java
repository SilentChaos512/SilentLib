package net.silentchaos512.lib.crafting.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public class ExtendedSingleItemRecipe extends SingleItemRecipe {
    public ExtendedSingleItemRecipe(RecipeType<?> type, RecipeSerializer<?> serializer, ResourceLocation id, String group, Ingredient ingredient, ItemStack result) {
        super(type, serializer, id, group, ingredient, result);
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
        return this.ingredient.test(inv.getItem(0));
    }

    @FunctionalInterface
    public interface IRecipeFactory<T extends SingleItemRecipe> {
        T create(ResourceLocation id, Ingredient ingredient, ItemStack result);
    }

    public static class Serializer<T extends ExtendedSingleItemRecipe> implements RecipeSerializer<T> {
        private final IRecipeFactory<T> recipeFactory;
        @Nullable private final BiConsumer<JsonObject, T> readJson;
        @Nullable private final BiConsumer<FriendlyByteBuf, T> readBuffer;
        @Nullable private final BiConsumer<FriendlyByteBuf, T> writeBuffer;

        public Serializer(IRecipeFactory<T> recipeFactory,
                          @Nullable BiConsumer<JsonObject, T> readJson,
                          @Nullable BiConsumer<FriendlyByteBuf, T> readBuffer,
                          @Nullable BiConsumer<FriendlyByteBuf, T> writeBuffer) {
            this.recipeFactory = recipeFactory;
            this.readJson = readJson;
            this.readBuffer = readBuffer;
            this.writeBuffer = writeBuffer;
        }

        public static <S extends ExtendedSingleItemRecipe> Serializer<S> basic(RecipeType<?> recipeType, IRecipeFactory<S> recipeFactory) {
            return new Serializer<>(recipeFactory, null, null, null);
        }

        @Override
        public T fromJson(ResourceLocation recipeId, JsonObject json) {
            Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));
            ResourceLocation itemId = new ResourceLocation(GsonHelper.getAsString(json, "result"));
            int count = GsonHelper.getAsInt(json, "count");
            ItemStack result = new ItemStack(ForgeRegistries.ITEMS.getValue(itemId), count);

            T ret = this.recipeFactory.create(recipeId, ingredient, result);

            if (this.readJson != null) {
                this.readJson.accept(json, ret);
            }

            return ret;
        }

        @Override
        public T fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ItemStack result = buffer.readItem();

            T ret = this.recipeFactory.create(recipeId, ingredient, result);

            if (this.readBuffer != null) {
                this.readBuffer.accept(buffer, ret);
            }

            return ret;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, T recipe) {
            recipe.ingredient.toNetwork(buffer);
            buffer.writeItem(recipe.result);

            if (this.writeBuffer != null) {
                this.writeBuffer.accept(buffer, recipe);
            }
        }
    }
}
