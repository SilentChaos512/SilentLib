package net.silentchaos512.lib.crafting.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Allows quick extensions of vanilla shapeless crafting.
 */
public abstract class ExtendedShapelessRecipe extends ShapelessRecipe {
    private static final RecipeSerializer<ShapelessRecipe> BASE_SERIALIZER = RecipeSerializer.SHAPELESS_RECIPE;

    private final ShapelessRecipe recipe;

    public ExtendedShapelessRecipe(ShapelessRecipe recipe) {
        super(recipe.getId(), recipe.getGroup(), recipe.getResultItem(), recipe.getIngredients());
        this.recipe = recipe;
    }

    public ShapelessRecipe getBaseRecipe() {
        return this.recipe;
    }

    @Override
    public abstract RecipeSerializer<?> getSerializer();

    @Override
    public abstract boolean matches(CraftingContainer inv, Level worldIn);

    @Override
    public abstract ItemStack assemble(CraftingContainer inv);

    public static class Serializer<T extends ExtendedShapelessRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {
        private final Function<ShapelessRecipe, T> recipeFactory;
        @Nullable private final BiConsumer<JsonObject, T> readJson;
        @Nullable private final BiConsumer<FriendlyByteBuf, T> readBuffer;
        @Nullable private final BiConsumer<FriendlyByteBuf, T> writeBuffer;

        public Serializer(Function<ShapelessRecipe, T> recipeFactory,
                          @Nullable BiConsumer<JsonObject, T> readJson,
                          @Nullable BiConsumer<FriendlyByteBuf, T> readBuffer,
                          @Nullable BiConsumer<FriendlyByteBuf, T> writeBuffer) {
            this.recipeFactory = recipeFactory;
            this.readJson = readJson;
            this.readBuffer = readBuffer;
            this.writeBuffer = writeBuffer;
        }

        public static <S extends ExtendedShapelessRecipe> Serializer<S> basic(Function<ShapelessRecipe, S> recipeFactory) {
            return new Serializer<>(recipeFactory, null, null, null);
        }

        @Deprecated
        public static <S extends ExtendedShapelessRecipe> Serializer<S> basic(ResourceLocation serializerId, Function<ShapelessRecipe, S> recipeFactory) {
            return new Serializer<>(recipeFactory, null, null, null);
        }

        @Override
        public T fromJson(ResourceLocation recipeId, JsonObject json) {
            ShapelessRecipe recipe = BASE_SERIALIZER.fromJson(recipeId, json);
            T result = this.recipeFactory.apply(recipe);
            if (this.readJson != null) {
                this.readJson.accept(json, result);
            }
            return result;
        }

        @Override
        public T fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            ShapelessRecipe recipe = BASE_SERIALIZER.fromNetwork(recipeId, buffer);
            T result = this.recipeFactory.apply(recipe);
            if (this.readBuffer != null) {
                this.readBuffer.accept(buffer, result);
            }
            return result;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, T recipe) {
            BASE_SERIALIZER.toNetwork(buffer, recipe.getBaseRecipe());
            if (this.writeBuffer != null) {
                this.writeBuffer.accept(buffer, recipe);
            }
        }
    }
}
