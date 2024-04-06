package net.silentchaos512.lib.crafting.recipe;

import com.mojang.datafixers.util.Function5;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.inventory.CraftingContainer;
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
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
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
    public boolean matches(CraftingContainer pInv, Level pLevel) {
        return this.pattern.matches(pInv);
    }

    @Override
    public ItemStack assemble(CraftingContainer pContainer, RegistryAccess pRegistryAccess) {
        return this.getResultItem(pRegistryAccess).copy();
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
        return nonnulllist.isEmpty() || nonnulllist.stream().filter(ingredient -> !ingredient.isEmpty()).anyMatch(net.neoforged.neoforge.common.CommonHooks::hasNoElements);
    }

    public static class BasicSerializer<R extends ExtendedShapedRecipe> implements RecipeSerializer<R> {
        private final Codec<R> codec;
        private final Function5<String, CraftingBookCategory, ShapedRecipePattern, ItemStack, Boolean, R> factory;

        public BasicSerializer(Function5<String, CraftingBookCategory, ShapedRecipePattern, ItemStack, Boolean, R> factory) {
            this.factory = factory;
            this.codec = RecordCodecBuilder.create(
                    builder -> builder.group(
                                    ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(r -> r.group),
                                    CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(r -> r.category),
                                    ShapedRecipePattern.MAP_CODEC.forGetter(r -> r.pattern),
                                    ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter(r -> r.result),
                                    ExtraCodecs.strictOptionalField(Codec.BOOL, "show_notification", true).forGetter(r -> r.showNotification)
                            )
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
            ShapedRecipePattern pattern = ShapedRecipePattern.fromNetwork(pBuffer);
            ItemStack result = pBuffer.readItem();
            boolean showNotification = pBuffer.readBoolean();
            return factory.apply(group, category, pattern, result, showNotification);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, R pRecipe) {
            pBuffer.writeUtf(pRecipe.group);
            pBuffer.writeEnum(pRecipe.category);
            pRecipe.pattern.toNetwork(pBuffer);
            pBuffer.writeItem(pRecipe.result);
            pBuffer.writeBoolean(pRecipe.showNotification);
        }
    }
}

