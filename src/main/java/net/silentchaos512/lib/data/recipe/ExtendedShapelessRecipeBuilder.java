package net.silentchaos512.lib.data.recipe;

import com.mojang.datafixers.util.Function4;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/** @noinspection unused*/
public abstract class ExtendedShapelessRecipeBuilder<R extends CraftingRecipe> implements RecipeBuilder {
    private final HolderGetter<Item> items;
    protected final RecipeCategory category;
    protected final ItemStackTemplate result;
    protected final NonNullList<Ingredient> ingredients = NonNullList.create();
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    protected Recipe.CommonInfo commonInfo = new Recipe.CommonInfo(true);
    protected CraftingRecipe.CraftingBookInfo bookInfo;

    public ExtendedShapelessRecipeBuilder(HolderGetter<Item> items, RecipeCategory category, ItemStackTemplate result) {
        this.items = items;
        this.category = category;
        this.result = result;
        this.bookInfo = new CraftingRecipe.CraftingBookInfo(RecipeBuilder.determineCraftingBookCategory(this.category), "");
    }

    public ExtendedShapelessRecipeBuilder(HolderGetter<Item> items, RecipeCategory category, ItemLike result, int count) {
        this(items, category, new ItemStackTemplate(result.asItem(), count));
    }

    public ExtendedShapelessRecipeBuilder(HolderGetter<Item> items, RecipeCategory category, ItemLike result) {
        this(items, category, result, 1);
    }

    public abstract R createRecipe(ResourceKey<Recipe<?>> id);

    public ExtendedShapelessRecipeBuilder<R> commonInfo(Recipe.CommonInfo commonInfo) {
        this.commonInfo = commonInfo;
        return this;
    }

    public ExtendedShapelessRecipeBuilder<R> showNotification(boolean showNotification) {
        this.commonInfo = new Recipe.CommonInfo(showNotification);
        return this;
    }

    public ExtendedShapelessRecipeBuilder<R> bookInfo(CraftingRecipe.CraftingBookInfo bookInfo) {
        this.bookInfo = bookInfo;
        return this;
    }

    @Override
    public ExtendedShapelessRecipeBuilder<R> group(@Nullable String group) {
        this.bookInfo = new CraftingRecipe.CraftingBookInfo(this.bookInfo.category(), group == null ? "" : group);
        return this;
    }

    public ExtendedShapelessRecipeBuilder<R> requires(TagKey<Item> tag) {
        return this.requires(tag, 1);
    }

    public ExtendedShapelessRecipeBuilder<R> requires(TagKey<Item> tag, int count) {
        return this.requires(Ingredient.of(this.items.getOrThrow(tag)), count);
    }

    public ExtendedShapelessRecipeBuilder<R> requires(ItemLike item) {
        return this.requires(item, 1);
    }

    public ExtendedShapelessRecipeBuilder<R> requires(ItemLike item, int count) {
        for (int i = 0; i < count; ++i) {
            this.requires(Ingredient.of(item));
        }

        return this;
    }

    public ExtendedShapelessRecipeBuilder<R> requires(ICustomIngredient customIngredient) {
        return this.requires(customIngredient, 1);
    }

    public ExtendedShapelessRecipeBuilder<R> requires(ICustomIngredient customIngredient, int quantity) {
        for (int i = 0; i < quantity; ++i) {
            this.ingredients.add(new Ingredient(customIngredient));
        }

        return this;
    }

    public ExtendedShapelessRecipeBuilder<R> requires(Ingredient pIngredient) {
        return this.requires(pIngredient, 1);
    }

    public ExtendedShapelessRecipeBuilder<R> requires(Ingredient pIngredient, int pQuantity) {
        for (int i = 0; i < pQuantity; ++i) {
            this.ingredients.add(pIngredient);
        }

        return this;
    }

    @Override
    public ExtendedShapelessRecipeBuilder<R> unlockedBy(String pName, Criterion<?> pCriterion) {
        this.criteria.put(pName, pCriterion);
        return this;
    }

    @Override
    public void save(RecipeOutput pRecipeOutput, ResourceKey<Recipe<?>> pId) {
        Advancement.Builder advancement$builder = null;
        if (!this.criteria.isEmpty()) {
            advancement$builder = pRecipeOutput.advancement()
                    .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pId))
                    .rewards(AdvancementRewards.Builder.recipe(pId))
                    .requirements(AdvancementRequirements.Strategy.OR);
            this.criteria.forEach(advancement$builder::addCriterion);
        }

        R recipe = createRecipe(pId);
        var advancementHolder = advancement$builder != null
                ? advancement$builder.build(pId.identifier().withPrefix("recipes/" + this.category.getFolderName() + "/"))
                : null;
        pRecipeOutput.accept(pId, recipe, advancementHolder);
    }

    public Recipe.CommonInfo commonInfo() {
        return this.commonInfo;
    }

    public CraftingRecipe.CraftingBookInfo bookInfo() {
        return this.bookInfo;
    }

    public RecipeCategory category() {
        return category;
    }

    public ItemStackTemplate result() {
        return result;
    }

    public NonNullList<Ingredient> ingredients() {
        return NonNullList.copyOf(ingredients);
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return RecipeBuilder.getDefaultRecipeId(this.result);
    }

    public static ShapelessRecipe vanillaFactory(Identifier id, ExtendedShapelessRecipeBuilder<ShapelessRecipe> builder) {
        // Basically the same as ShapelessRecipeBuilder, but doesn't fail if advancement is missing
        return new ShapelessRecipe(
                builder.commonInfo,
                builder.bookInfo,
                builder.result,
                builder.ingredients
        );
    }

    public static class Basic<R extends CraftingRecipe> extends ExtendedShapelessRecipeBuilder<R> {
        private final BiFunction<ResourceKey<Recipe<?>>, Basic<R>, R> factory;

        public Basic(HolderGetter<Item> items, RecipeCategory category, ItemStackTemplate result, BiFunction<ResourceKey<Recipe<?>>, Basic<R>, R> factory) {
            super(items, category, result);
            this.factory = factory;
        }

        public Basic(HolderGetter<Item> items, RecipeCategory category, ItemLike result, int count, BiFunction<ResourceKey<Recipe<?>>, Basic<R>, R> factory) {
            super(items, category, result, count);
            this.factory = factory;
        }

        public Basic(HolderGetter<Item> items, RecipeCategory category, ItemLike result, BiFunction<ResourceKey<Recipe<?>>, Basic<R>, R> factory) {
            super(items, category, result);
            this.factory = factory;
        }

        public Basic(HolderGetter<Item> items, RecipeCategory category, ItemStackTemplate result, Factory<R> factory) {
            super(items, category, result);
            this.factory = convertConstructor(factory);
        }

        public Basic(HolderGetter<Item> items, RecipeCategory category, ItemLike result, int count, Factory<R> factory) {
            super(items, category, result, count);
            this.factory = convertConstructor(factory);
        }

        public Basic(HolderGetter<Item> items, RecipeCategory category, ItemLike result, Factory<R> factory) {
            super(items, category, result);
            this.factory = convertConstructor(factory);
        }

        private static <R extends CraftingRecipe> BiFunction<ResourceKey<Recipe<?>>, Basic<R>, R> convertConstructor(Factory<R> factory) {
            return (_, builder) -> factory.apply(
                    builder.commonInfo,
                    builder.bookInfo,
                    builder.result,
                    builder.ingredients
            );
        }

        @Override
        public R createRecipe(ResourceKey<Recipe<?>> id) {
            return factory.apply(id, this);
        }

        @Override
        public ResourceKey<Recipe<?>> defaultId() {
            return RecipeBuilder.getDefaultRecipeId(this.result);
        }
    }

    public interface Factory<R extends CraftingRecipe> extends Function4<Recipe.CommonInfo, CraftingRecipe.CraftingBookInfo, ItemStackTemplate, List<Ingredient>, R> {}
}