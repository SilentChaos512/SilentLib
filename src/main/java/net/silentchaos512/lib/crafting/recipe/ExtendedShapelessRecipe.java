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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import org.checkerframework.checker.units.qual.N;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class ExtendedShapelessRecipe implements CraftingRecipeExtension {
    protected final String group;
    protected final CraftingBookCategory category;
    protected final ItemStack result;
    protected final List<Ingredient> ingredients;
    @Nullable
    private PlacementInfo placementInfo;
    protected final boolean isSimple;

    public ExtendedShapelessRecipe(String group, CraftingBookCategory category, ItemStack result, List<Ingredient> ingredient) {
        this.group = group;
        this.category = category;
        this.result = result;
        this.ingredients = ingredient;
        this.isSimple = ingredient.stream().allMatch(Ingredient::isSimple);
    }

    @Override
    public abstract RecipeSerializer<? extends ExtendedShapelessRecipe> getSerializer();

    @Override
    public String group() {
        return this.group;
    }

    @Override
    public CraftingBookCategory category() {
        return this.category;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.create(this.ingredients);
        }
        return this.placementInfo;
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
    public ItemStack assemble(CraftingInput pContainer, HolderLookup.Provider pRegistries) {
        return this.result.copy();
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
    public ItemStack getResultForDisplay() {
        return this.result.copy();
    }

    @Override
    public List<Ingredient> getIngredientsForDisplay() {
        return this.ingredients;
    }

    protected static <T extends ExtendedShapelessRecipe> Products.P4<RecordCodecBuilder.Mu<T>, String, CraftingBookCategory, ItemStack, List<Ingredient>> commonCodecFields(RecordCodecBuilder.Instance<T> pInstance) {
        return pInstance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(r -> r.group),
                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(r -> r.category),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.result),
                Codec.lazyInitialized(() -> Ingredient.CODEC.listOf(1, ShapedRecipePattern.getMaxHeight() * ShapedRecipePattern.getMaxWidth()))
                        .fieldOf("ingredients")
                        .forGetter(r -> r.ingredients)
        );
    }

    public static class BasicSerializer<R extends ExtendedShapelessRecipe> implements RecipeSerializer<R> {
        private final MapCodec<R> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;
        private final Function4<String, CraftingBookCategory, ItemStack, List<Ingredient>, R> factory;

        public BasicSerializer(Function4<String, CraftingBookCategory, ItemStack, List<Ingredient>, R> factory) {
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
            List<Ingredient> ingredients = new ArrayList<>();
            for (int i = 0; i < ingredientCount; ++i) {
                ingredients.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
            }

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