package net.silentchaos512.lib.crafting.recipe;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.util.Function5;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public abstract class ExtendedShapedRecipe extends ShapedRecipe {
    protected final ShapedRecipePattern pattern;
    protected final ItemStack result;
    protected final String group;
    protected final CraftingBookCategory category;
    protected final boolean showNotification;

    public ExtendedShapedRecipe(String pGroup, CraftingBookCategory pCategory, ShapedRecipePattern pPattern, ItemStack pResult, boolean pShowNotification) {
        super(pGroup, pCategory, pPattern, pResult, pShowNotification);
        this.group = pGroup;
        this.category = pCategory;
        this.pattern = pPattern;
        this.result = pResult;
        this.showNotification = pShowNotification;
    }

    public ExtendedShapedRecipe(String pGroup, CraftingBookCategory pCategory, ShapedRecipePattern pPattern, ItemStack pResult) {
        this(pGroup, pCategory, pPattern, pResult, true);
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
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.pattern.ingredients();
    }

    @Override
    public boolean showNotification() {
        return this.showNotification;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth >= this.pattern.width() && pHeight >= this.pattern.height();
    }

    @Override
    public boolean matches(CraftingInput pInv, Level pLevel) {
        return this.pattern.matches(pInv);
    }

    @Override
    public ItemStack assemble(CraftingInput pContainer, HolderLookup.Provider pRegistries) {
        return this.getResultItem(pRegistries).copy();
    }

    @Override
    public int getWidth() {
        return this.pattern.width();
    }

    @Override
    public int getHeight() {
        return this.pattern.height();
    }

    @Override
    public boolean isIncomplete() {
        NonNullList<Ingredient> nonnulllist = this.getIngredients();
        return nonnulllist.isEmpty() || nonnulllist.stream()
                .filter(ingredient -> !ingredient.isEmpty())
                .anyMatch(Ingredient::hasNoItems);
    }

    protected static <R extends ExtendedShapedRecipe> Products.P5<RecordCodecBuilder.Mu<R>, String, CraftingBookCategory, ShapedRecipePattern, ItemStack, Boolean> basicCodecFields(RecordCodecBuilder.Instance<R> builder) {
        return builder.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(r -> r.group),
                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(r -> r.category),
                ShapedRecipePattern.MAP_CODEC.forGetter(r -> r.pattern),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.result),
                Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(r -> r.showNotification)
        );
    }

    public static class BasicSerializer<R extends ExtendedShapedRecipe> implements RecipeSerializer<R> {
        private final MapCodec<R> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;
        private final Function5<String, CraftingBookCategory, ShapedRecipePattern, ItemStack, Boolean, R> factory;

        public BasicSerializer(Function5<String, CraftingBookCategory, ShapedRecipePattern, ItemStack, Boolean, R> factory) {
            this.factory = factory;
            this.codec = RecordCodecBuilder.mapCodec(
                    builder -> basicCodecFields(builder)
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
            ShapedRecipePattern pattern = ShapedRecipePattern.STREAM_CODEC.decode(buf);
            ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
            boolean showNotification = buf.readBoolean();
            return factory.apply(group, category, pattern, result, showNotification);
        }

        public void toNetwork(RegistryFriendlyByteBuf buf, R recipe) {
            buf.writeUtf(recipe.group);
            buf.writeEnum(recipe.category);
            ShapedRecipePattern.STREAM_CODEC.encode(buf, recipe.pattern);
            ItemStack.STREAM_CODEC.encode(buf, recipe.result);
            buf.writeBoolean(recipe.showNotification);
        }
    }
}

