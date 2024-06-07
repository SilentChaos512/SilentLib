package net.silentchaos512.lib.crafting.recipe;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;

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
    public abstract RecipeSerializer<?> getSerializer();

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public CraftingBookCategory category() {
        return this.category;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public boolean matches(CraftingContainer pInv, Level pLevel) {
        StackedContents stackedcontents = new StackedContents();
        java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
        int i = 0;

        for (int j = 0; j < pInv.getContainerSize(); ++j) {
            ItemStack itemstack = pInv.getItem(j);
            if (!itemstack.isEmpty()) {
                ++i;
                if (isSimple)
                    stackedcontents.accountStack(itemstack, 1);
                else inputs.add(itemstack);
            }
        }

        return i == this.ingredients.size() && (isSimple ? stackedcontents.canCraft(this, null) : net.neoforged.neoforge.common.util.RecipeMatcher.findMatches(inputs, this.ingredients) != null);
    }

    @Override
    public ItemStack assemble(CraftingContainer pContainer, RegistryAccess pRegistryAccess) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= this.ingredients.size();
    }

    protected static <T extends ExtendedShapelessRecipe> Products.P4<RecordCodecBuilder.Mu<T>, String, CraftingBookCategory, ItemStack, NonNullList<Ingredient>> commonCodecFields(RecordCodecBuilder.Instance<T> pInstance) {
        var maxIngredients = 9; //ShapedRecipePattern.maxHeight * ShapedRecipePattern.maxWidth
        return pInstance.group(
                ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(p_301127_ -> p_301127_.group),
                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(p_301133_ -> p_301133_.category),
                ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter(p_301142_ -> p_301142_.result),
                Ingredient.CODEC_NONEMPTY
                        .listOf()
                        .fieldOf("ingredients")
                        .flatXmap(
                                list -> {
                                    Ingredient[] aingredient = list
                                            .toArray(Ingredient[]::new); //Forge skip the empty check and immediately create the array.
                                    if (aingredient.length == 0) {
                                        return DataResult.error(() -> "No ingredients for shapeless recipe");
                                    } else {
                                        return aingredient.length > maxIngredients
                                                ? DataResult.error(() -> "Too many ingredients for shapeless recipe. The maximum is: %s".formatted(maxIngredients))
                                                : DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                                    }
                                },
                                DataResult::success
                        )
                        .forGetter(r -> r.ingredients)
        );
    }

    public static class BasicSerializer<R extends ExtendedShapelessRecipe> implements RecipeSerializer<R> {
        private final Codec<R> codec;
        private final Function4<String, CraftingBookCategory, ItemStack, NonNullList<Ingredient>, R> factory;

        public BasicSerializer(Function4<String, CraftingBookCategory, ItemStack, NonNullList<Ingredient>, R> factory) {
            this.factory = factory;
            this.codec = RecordCodecBuilder.create(
                    builder -> commonCodecFields(builder)
                            .apply(builder, this.factory)
            );
        }

        @Override
        public Codec<R> codec() {
            return this.codec;
        }

        @Override
        public R fromNetwork(FriendlyByteBuf pBuffer) {
            String group = pBuffer.readUtf();
            CraftingBookCategory category = pBuffer.readEnum(CraftingBookCategory.class);
            int ingredientCount = pBuffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);

            for (int j = 0; j < ingredients.size(); ++j) {
                ingredients.set(j, Ingredient.fromNetwork(pBuffer));
            }

            ItemStack result = pBuffer.readItem();
            return this.factory.apply(group, category, result, ingredients);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, R pRecipe) {
            pBuffer.writeUtf(pRecipe.group);
            pBuffer.writeEnum(pRecipe.category);
            pBuffer.writeVarInt(pRecipe.ingredients.size());

            for (Ingredient ingredient : pRecipe.ingredients) {
                ingredient.toNetwork(pBuffer);
            }

            pBuffer.writeItem(pRecipe.result);
        }
    }
}
