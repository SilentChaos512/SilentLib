package net.silentchaos512.lib.crafting.recipe;

import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SingleItemRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public class ExtendedSingleItemRecipe extends SingleItemRecipe {
    public ExtendedSingleItemRecipe(IRecipeType<?> type, IRecipeSerializer<?> serializer, ResourceLocation id, String group, Ingredient ingredient, ItemStack result) {
        super(type, serializer, id, group, ingredient, result);
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return this.ingredient.test(inv.getItem(0));
    }

    @FunctionalInterface
    public interface IRecipeFactory<T extends SingleItemRecipe> {
        T create(ResourceLocation id, Ingredient ingredient, ItemStack result);
    }

    public static class Serializer<T extends ExtendedSingleItemRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
        private final IRecipeFactory<T> recipeFactory;
        @Nullable private final BiConsumer<JsonObject, T> readJson;
        @Nullable private final BiConsumer<PacketBuffer, T> readBuffer;
        @Nullable private final BiConsumer<PacketBuffer, T> writeBuffer;

        public Serializer(IRecipeFactory<T> recipeFactory,
                          @Nullable BiConsumer<JsonObject, T> readJson,
                          @Nullable BiConsumer<PacketBuffer, T> readBuffer,
                          @Nullable BiConsumer<PacketBuffer, T> writeBuffer) {
            this.recipeFactory = recipeFactory;
            this.readJson = readJson;
            this.readBuffer = readBuffer;
            this.writeBuffer = writeBuffer;
        }

        public static <S extends ExtendedSingleItemRecipe> Serializer<S> basic(IRecipeType<?> recipeType, IRecipeFactory<S> recipeFactory) {
            return new Serializer<>(recipeFactory, null, null, null);
        }

        @Override
        public T fromJson(ResourceLocation recipeId, JsonObject json) {
            Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));
            ResourceLocation itemId = new ResourceLocation(JSONUtils.getAsString(json, "result"));
            int count = JSONUtils.getAsInt(json, "count");
            ItemStack result = new ItemStack(ForgeRegistries.ITEMS.getValue(itemId), count);

            T ret = this.recipeFactory.create(recipeId, ingredient, result);

            if (this.readJson != null) {
                this.readJson.accept(json, ret);
            }

            return ret;
        }

        @Override
        public T fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ItemStack result = buffer.readItem();

            T ret = this.recipeFactory.create(recipeId, ingredient, result);

            if (this.readBuffer != null) {
                this.readBuffer.accept(buffer, ret);
            }

            return ret;
        }

        @Override
        public void toNetwork(PacketBuffer buffer, T recipe) {
            recipe.ingredient.toNetwork(buffer);
            buffer.writeItem(recipe.result);

            if (this.writeBuffer != null) {
                this.writeBuffer.accept(buffer, recipe);
            }
        }
    }
}
