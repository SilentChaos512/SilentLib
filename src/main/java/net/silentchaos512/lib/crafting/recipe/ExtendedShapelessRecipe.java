package net.silentchaos512.lib.crafting.recipe;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

public abstract class ExtendedShapelessRecipe extends ShapelessRecipe {
    protected final String group;
    protected final CraftingBookCategory category;
    protected final ItemStack result;
    protected final NonNullList<Ingredient> ingredients;
    protected final boolean isSimple;

    public ExtendedShapelessRecipe(String pGroup, CraftingBookCategory pCategory, ItemStack pResult, NonNullList<Ingredient> pIngredients) {
        super(pGroup, pCategory, pResult, pIngredients);
        this.group = pGroup;
        this.category = pCategory;
        this.result = pResult;
        this.ingredients = pIngredients;
        this.isSimple = pIngredients.stream().allMatch(Ingredient::isSimple);
    }

    @Override
    public abstract RecipeSerializer<ShapelessRecipe> getSerializer();

    @Override
    public String group() {
        return this.group;
    }

    @Override
    public CraftingBookCategory category() {
        return this.category;
    }

    @Override
    public ItemStack assemble(CraftingInput pContainer, HolderLookup.Provider pRegistries) {
        return this.result.copy();
    }

    protected static <T extends ExtendedShapelessRecipe> Products.P4<RecordCodecBuilder.Mu<T>, String, CraftingBookCategory, ItemStack, NonNullList<Ingredient>> commonCodecFields(RecordCodecBuilder.Instance<T> pInstance) {
        var maxIngredients = 9; //ShapedRecipePattern.maxHeight * ShapedRecipePattern.maxWidth
        return pInstance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(r -> r.group),
                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(r -> r.category),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.result),
                Ingredient.CODEC
                        .listOf()
                        .fieldOf("ingredients")
                        .flatXmap(
                                list -> {
                                    Ingredient[] aingredient = list.toArray(Ingredient[]::new);
                                    if (aingredient.length == 0) {
                                        return DataResult.error(() -> "No ingredients for shapeless recipe");
                                    } else {
                                        return aingredient.length > maxIngredients
                                                ? DataResult.error(() -> "Too many ingredients for shapeless recipe. The maximum is: %s".formatted(maxIngredients))
                                                : DataResult.success(NonNullList.of(Ingredient.of(), aingredient));
                                    }
                                },
                                DataResult::success
                        )
                        .forGetter(r -> r.ingredients)
        );
    }

    public static class BasicSerializer<R extends ExtendedShapelessRecipe> implements RecipeSerializer<R> {
        private final MapCodec<R> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;
        private final Function4<String, CraftingBookCategory, ItemStack, NonNullList<Ingredient>, R> factory;

        public BasicSerializer(Function4<String, CraftingBookCategory, ItemStack, NonNullList<Ingredient>, R> factory) {
            this.factory = factory;
            this.codec = RecordCodecBuilder.mapCodec(
                    builder -> commonCodecFields(builder)
                            .apply(builder, this.factory)
            );
            this.streamCodec = StreamCodec.of(this::toNetwork, this::fromNetwork);
        }

        @Override
        public MapCodec<R> codec() {
            return this.codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
            return this.streamCodec;
        }

        public R fromNetwork(RegistryFriendlyByteBuf buf) {
            String group = buf.readUtf();
            CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
            int ingredientCount = buf.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.of());

            ingredients.replaceAll(ignored -> Ingredient.CONTENTS_STREAM_CODEC.decode(buf));

            ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
            return this.factory.apply(group, category, result, ingredients);
        }

        public void toNetwork(RegistryFriendlyByteBuf buf, R recipe) {
            buf.writeUtf(recipe.group);
            buf.writeEnum(recipe.category);
            buf.writeVarInt(recipe.ingredients.size());

            for (Ingredient ingredient : recipe.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
            }

            ItemStack.STREAM_CODEC.encode(buf, recipe.result);
        }
    }
}