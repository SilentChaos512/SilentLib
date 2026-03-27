package net.silentchaos512.lib.crafting.recipe;

import com.google.common.annotations.VisibleForTesting;
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
import java.util.Optional;

public abstract class ExtendedShapedRecipe extends NormalCraftingRecipe implements CraftingRecipeExtension {
    public final ShapedRecipePattern pattern;
    protected final ItemStackTemplate result;

    public ExtendedShapedRecipe(CommonInfo commonInfo, CraftingBookInfo bookInfo, ShapedRecipePattern pattern, ItemStackTemplate result) {
        super(commonInfo, bookInfo);
        this.pattern = pattern;
        this.result = result;
    }

    @Override
    public abstract RecipeSerializer<? extends ExtendedShapedRecipe> getSerializer();

    @VisibleForTesting
    public List<Optional<Ingredient>> getIngredients() {
        return this.pattern.ingredients();
    }

    @Override
    protected PlacementInfo createPlacementInfo() {
        return PlacementInfo.createFromOptionals(this.pattern.ingredients());
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return this.pattern.matches(input);
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        return this.result.create();
    }

    public int getWidth() {
        return this.pattern.width();
    }

    public int getHeight() {
        return this.pattern.height();
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(
                new ShapedCraftingRecipeDisplay(
                        this.pattern.width(),
                        this.pattern.height(),
                        this.pattern.ingredients().stream().map(e -> e.map(Ingredient::display).orElse(SlotDisplay.Empty.INSTANCE)).toList(),
                        new SlotDisplay.ItemStackSlotDisplay(this.result),
                        new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
                )
        );
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
