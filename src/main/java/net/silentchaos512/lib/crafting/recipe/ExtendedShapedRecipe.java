package net.silentchaos512.lib.crafting.recipe;

import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Allows quick extensions of vanilla shaped crafting.
 */
public abstract class ExtendedShapedRecipe extends ShapedRecipe {
    private static final IRecipeSerializer<ShapedRecipe> BASE_SERIALIZER = IRecipeSerializer.SHAPED_RECIPE;

    private final ShapedRecipe recipe;

    public ExtendedShapedRecipe(ShapedRecipe recipe) {
        super(recipe.getId(), recipe.getGroup(), recipe.getRecipeWidth(), recipe.getRecipeHeight(), recipe.getIngredients(), recipe.getResultItem());
        this.recipe = recipe;
    }

    public ShapedRecipe getBaseRecipe() {
        return this.recipe;
    }

    @Override
    public abstract IRecipeSerializer<?> getSerializer();

    @Override
    public abstract boolean matches(CraftingInventory inv, World worldIn);

    @Override
    public abstract ItemStack assemble(CraftingInventory inv);

    public static class Serializer<T extends ExtendedShapedRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
        private final Function<ShapedRecipe, T> recipeFactory;
        @Nullable private final BiConsumer<JsonObject, T> readJson;
        @Nullable private final BiConsumer<PacketBuffer, T> readBuffer;
        @Nullable private final BiConsumer<PacketBuffer, T> writeBuffer;

        public Serializer(Function<ShapedRecipe, T> recipeFactory,
                          @Nullable BiConsumer<JsonObject, T> readJson,
                          @Nullable BiConsumer<PacketBuffer, T> readBuffer,
                          @Nullable BiConsumer<PacketBuffer, T> writeBuffer) {
            this.recipeFactory = recipeFactory;
            this.readJson = readJson;
            this.readBuffer = readBuffer;
            this.writeBuffer = writeBuffer;
        }

        public static <S extends ExtendedShapedRecipe> Serializer<S> basic(Function<ShapedRecipe, S> recipeFactory) {
            return new Serializer<>(recipeFactory, null, null, null);
        }

        @Deprecated
        public static <S extends ExtendedShapedRecipe> Serializer<S> basic(ResourceLocation serializerId, Function<ShapedRecipe, S> recipeFactory) {
            return new Serializer<>(recipeFactory, null, null, null);
        }

        @Override
        public T fromJson(ResourceLocation recipeId, JsonObject json) {
            ShapedRecipe recipe = BASE_SERIALIZER.fromJson(recipeId, json);
            T result = this.recipeFactory.apply(recipe);
            if (this.readJson != null) {
                this.readJson.accept(json, result);
            }
            return result;
        }

        @Override
        public T fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
            ShapedRecipe recipe = BASE_SERIALIZER.fromNetwork(recipeId, buffer);
            T result = this.recipeFactory.apply(recipe);
            if (this.readBuffer != null) {
                this.readBuffer.accept(buffer, result);
            }
            return result;
        }

        @Override
        public void toNetwork(PacketBuffer buffer, T recipe) {
            BASE_SERIALIZER.toNetwork(buffer, recipe.getBaseRecipe());
            if (this.writeBuffer != null) {
                this.writeBuffer.accept(buffer, recipe);
            }
        }
    }
}
