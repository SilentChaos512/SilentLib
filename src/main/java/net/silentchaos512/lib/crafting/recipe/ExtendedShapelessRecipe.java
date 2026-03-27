package net.silentchaos512.lib.crafting.recipe;

import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

import java.util.List;

public abstract class ExtendedShapelessRecipe extends NormalCraftingRecipe implements CraftingRecipeExtension {
    protected final ItemStackTemplate result;
    protected final List<Ingredient> ingredients;
    private final boolean isSimple;

    public ExtendedShapelessRecipe(CommonInfo commonInfo, CraftingBookInfo bookInfo, ItemStackTemplate result, List<Ingredient> ingredients) {
        super(commonInfo, bookInfo);
        this.result = result;
        this.ingredients = ingredients;
        this.isSimple = ingredients.stream().allMatch(Ingredient::isSimple);
    }

    @Override
    public abstract RecipeSerializer<? extends ExtendedShapelessRecipe> getSerializer();

    @Override
    protected PlacementInfo createPlacementInfo() {
        return PlacementInfo.create(this.ingredients);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.ingredientCount() != this.ingredients.size()) {
            return false;
        } else if (!isSimple) {
            var nonEmptyItems = new java.util.ArrayList<ItemStack>(input.ingredientCount());
            for (var item : input.items())
                if (!item.isEmpty())
                    nonEmptyItems.add(item);
            return net.neoforged.neoforge.common.util.RecipeMatcher.findMatches(nonEmptyItems, this.ingredients) != null;
        } else {
            return input.size() == 1 && this.ingredients.size() == 1
                    ? this.ingredients.getFirst().test(input.getItem(0))
                    : input.stackedContents().canCraft(this, null);
        }
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        return this.result.create();
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(
                new ShapelessCraftingRecipeDisplay(
                        this.ingredients.stream().map(Ingredient::display).toList(),
                        new SlotDisplay.ItemStackSlotDisplay(this.result),
                        new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
                )
        );
    }

    @Override
    public ItemStackTemplate getResultForDisplay() {
        return this.result;
    }

    public static <R extends ExtendedShapelessRecipe> RecipeSerializer<R> basicSerializer(Function4<CommonInfo, CraftingBookInfo, ItemStackTemplate, List<Ingredient>, R> constructor) {
        return new RecipeSerializer<>(basicCodec(constructor), basicStreamCodec(constructor));
    }

    public static <R extends ExtendedShapelessRecipe> MapCodec<R> basicCodec(Function4<CommonInfo, CraftingBookInfo, ItemStackTemplate, List<Ingredient>, R> constructor) {
        final int maxSize = 9; //ShapedRecipePattern.maxHeight * ShapedRecipePattern.maxWidth;
        return RecordCodecBuilder.mapCodec(
                i -> i.group(
                        Recipe.CommonInfo.MAP_CODEC.forGetter(o -> o.commonInfo),
                        CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(o -> o.bookInfo),
                        ItemStackTemplate.CODEC.fieldOf("result").forGetter(o -> o.result),
                        Codec.lazyInitialized(() -> Ingredient.CODEC.listOf(1, maxSize)).fieldOf("ingredients").forGetter(o -> o.ingredients)
                ).apply(i, constructor)
        );
    }

    public static <R extends ExtendedShapelessRecipe> StreamCodec<RegistryFriendlyByteBuf, R> basicStreamCodec(Function4<CommonInfo, CraftingBookInfo, ItemStackTemplate, List<Ingredient>, R> constructor) {
        return StreamCodec.composite(
                Recipe.CommonInfo.STREAM_CODEC,
                o -> o.commonInfo,
                CraftingRecipe.CraftingBookInfo.STREAM_CODEC,
                o -> o.bookInfo,
                ItemStackTemplate.STREAM_CODEC,
                o -> o.result,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                o -> o.ingredients,
                constructor
        );
    }
}