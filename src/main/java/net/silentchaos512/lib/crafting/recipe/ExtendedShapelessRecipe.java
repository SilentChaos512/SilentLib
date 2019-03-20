package net.silentchaos512.lib.crafting.recipe;

import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Allows quick extensions of vanilla shapeless crafting.
 */
public abstract class ExtendedShapelessRecipe extends ShapelessRecipe {
    private final ShapelessRecipe recipe;

    public ExtendedShapelessRecipe(ShapelessRecipe recipe) {
        super(recipe.getId(), recipe.getGroup(), recipe.getRecipeOutput(), recipe.getIngredients());
        this.recipe = recipe;
    }

    public ShapelessRecipe getBaseRecipe() {
        return this.recipe;
    }

    @Override
    public abstract IRecipeSerializer<?> getSerializer();

    @Override
    public abstract boolean matches(IInventory inv, World worldIn);

    @Override
    public abstract ItemStack getCraftingResult(IInventory inv);

    public static class Serializer<T extends ExtendedShapelessRecipe> implements IRecipeSerializer<T> {
        private final ResourceLocation serializerId;
        private final Function<ShapelessRecipe, T> recipeFactory;
        @Nullable private final BiConsumer<JsonObject, T> readJson;
        @Nullable private final BiConsumer<PacketBuffer, T> readBuffer;
        @Nullable private final BiConsumer<PacketBuffer, T> writeBuffer;

        public Serializer(ResourceLocation serializerId,
                          Function<ShapelessRecipe, T> recipeFactory,
                          @Nullable BiConsumer<JsonObject, T> readJson,
                          @Nullable BiConsumer<PacketBuffer, T> readBuffer,
                          @Nullable BiConsumer<PacketBuffer, T> writeBuffer) {
            this.serializerId = serializerId;
            this.recipeFactory = recipeFactory;
            this.readJson = readJson;
            this.readBuffer = readBuffer;
            this.writeBuffer = writeBuffer;
        }

        public static <S extends ExtendedShapelessRecipe> Serializer<S> basic(ResourceLocation serializerId, Function<ShapelessRecipe, S> recipeFactory) {
            return new Serializer<>(serializerId, recipeFactory, null, null, null);
        }

        @Override
        public T read(ResourceLocation recipeId, JsonObject json) {
            ShapelessRecipe recipe = RecipeSerializers.CRAFTING_SHAPELESS.read(recipeId, json);
            T result = this.recipeFactory.apply(recipe);
            if (this.readJson != null) {
                this.readJson.accept(json, result);
            }
            return result;
        }

        @Override
        public T read(ResourceLocation recipeId, PacketBuffer buffer) {
            ShapelessRecipe recipe = RecipeSerializers.CRAFTING_SHAPELESS.read(recipeId, buffer);
            T result = this.recipeFactory.apply(recipe);
            if (this.readBuffer != null) {
                this.readBuffer.accept(buffer, result);
            }
            return result;
        }

        @Override
        public void write(PacketBuffer buffer, T recipe) {
            RecipeSerializers.CRAFTING_SHAPELESS.write(buffer, recipe.getBaseRecipe());
            if (this.writeBuffer != null) {
                this.writeBuffer.accept(buffer, recipe);
            }
        }

        @Override
        public ResourceLocation getName() {
            return this.serializerId;
        }
    }
}
