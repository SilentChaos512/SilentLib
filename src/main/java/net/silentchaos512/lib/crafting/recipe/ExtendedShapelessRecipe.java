package net.silentchaos512.lib.crafting.recipe;

import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;

import java.util.List;

public abstract class ExtendedShapelessRecipe extends ShapelessRecipe implements CraftingRecipeExtension {
    public ExtendedShapelessRecipe(CommonInfo commonInfo, CraftingBookInfo bookInfo, ItemStackTemplate result, List<Ingredient> ingredients) {
        super(commonInfo, bookInfo, result, ingredients);
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