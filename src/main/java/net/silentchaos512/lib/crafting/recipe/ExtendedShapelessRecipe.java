package net.silentchaos512.lib.crafting.recipe;

import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Allows quick extensions of vanilla shapeless crafting.
 */
public abstract class ExtendedShapelessRecipe extends ShapelessRecipe {
    private static final IRecipeSerializer<ShapelessRecipe> BASE_SERIALIZER = IRecipeSerializer.field_222158_b;

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
    public abstract boolean matches(CraftingInventory inv, World worldIn);

    @Override
    public abstract ItemStack getCraftingResult(CraftingInventory inv);

    public static class Serializer<T extends ExtendedShapelessRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
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
            ShapelessRecipe recipe = BASE_SERIALIZER.read(recipeId, json);
            T result = this.recipeFactory.apply(recipe);
            if (this.readJson != null) {
                this.readJson.accept(json, result);
            }
            return result;
        }

        @Override
        public T read(ResourceLocation recipeId, PacketBuffer buffer) {
            ShapelessRecipe recipe = BASE_SERIALIZER.read(recipeId, buffer);
            T result = this.recipeFactory.apply(recipe);
            if (this.readBuffer != null) {
                this.readBuffer.accept(buffer, result);
            }
            return result;
        }

        @Override
        public void write(PacketBuffer buffer, T recipe) {
            BASE_SERIALIZER.write(buffer, recipe.getBaseRecipe());
            if (this.writeBuffer != null) {
                this.writeBuffer.accept(buffer, recipe);
            }
        }
    }
}
