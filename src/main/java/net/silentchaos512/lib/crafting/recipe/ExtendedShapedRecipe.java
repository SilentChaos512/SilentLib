package net.silentchaos512.lib.crafting.recipe;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public abstract class ExtendedShapedRecipe extends ShapedRecipe implements CraftingRecipeExtension {
    public ExtendedShapedRecipe(CommonInfo commonInfo, CraftingBookInfo bookInfo, ShapedRecipePattern pattern, ItemStackTemplate result) {
        super(commonInfo, bookInfo, pattern, result);
    }

    @Override
    public ItemStackTemplate getResultForDisplay() {
        return this.result;
    }

    public static <R extends ExtendedShapedRecipe> RecipeSerializer<R> basicSerializer(Function4<CommonInfo, CraftingBookInfo, ShapedRecipePattern, ItemStackTemplate, R> constructor) {
        return new RecipeSerializer<>(basicCodec(constructor), basicStreamCodec(constructor));
    }

    public static <R extends ExtendedShapedRecipe> MapCodec<R> basicCodec(Function4<CommonInfo, CraftingBookInfo, ShapedRecipePattern, ItemStackTemplate, R> constructor) {
        final int maxSize = 9; //ShapedRecipePattern.maxHeight * ShapedRecipePattern.maxWidth;
        return RecordCodecBuilder.mapCodec(
                i -> i.group(
                        Recipe.CommonInfo.MAP_CODEC.forGetter(o -> o.commonInfo),
                        CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(o -> o.bookInfo),
                        ShapedRecipePattern.MAP_CODEC.forGetter(o -> o.pattern),
                        ItemStackTemplate.CODEC.fieldOf("result").forGetter(o -> o.result)
                ).apply(i, constructor)
        );
    }

    public static <R extends ExtendedShapedRecipe> StreamCodec<RegistryFriendlyByteBuf, R> basicStreamCodec(Function4<CommonInfo, CraftingBookInfo, ShapedRecipePattern, ItemStackTemplate, R> constructor) {
        return StreamCodec.composite(
                Recipe.CommonInfo.STREAM_CODEC,
                o -> o.commonInfo,
                CraftingRecipe.CraftingBookInfo.STREAM_CODEC,
                o -> o.bookInfo,
                ShapedRecipePattern.STREAM_CODEC,
                o -> o.pattern,
                ItemStackTemplate.STREAM_CODEC,
                o -> o.result,
                constructor
        );
    }
}
