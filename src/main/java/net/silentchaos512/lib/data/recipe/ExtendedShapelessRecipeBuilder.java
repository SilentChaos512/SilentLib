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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

public abstract class ExtendedShapelessRecipeBuilder<R extends CraftingRecipe> implements RecipeBuilder {
    private final HolderGetter<Item> items;
    protected final RecipeCategory category;
    protected final Item result;
    protected final ItemStack resultStack;
    protected final NonNullList<Ingredient> ingredients = NonNullList.create();
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    protected String group = "";

    public ExtendedShapelessRecipeBuilder(HolderGetter<Item> items, RecipeCategory category, ItemStack result) {
        this.items = items;
        this.category = category;
        this.result = result.getItem();
        this.resultStack = result;
    }

    public ExtendedShapelessRecipeBuilder(HolderGetter<Item> items, RecipeCategory category, ItemLike result, int count) {
        this(items, category, new ItemStack(result, count));
    }

    public ExtendedShapelessRecipeBuilder(HolderGetter<Item> items, RecipeCategory category, ItemLike result) {
        this(items, category, result, 1);
    }

    public abstract R createRecipe(ResourceKey<Recipe<?>> id);

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
        for(int i = 0; i < count; ++i) {
            this.requires(Ingredient.of(item));
        }

        return this;
    }

    public ExtendedShapelessRecipeBuilder<R> requires(ICustomIngredient customIngredient) {
        return this.requires(customIngredient, 1);
    }

    public ExtendedShapelessRecipeBuilder<R> requires(ICustomIngredient customIngredient, int quantity) {
        for(int i = 0; i < quantity; ++i) {
            this.ingredients.add(new Ingredient(customIngredient));
        }

        return this;
    }

    public ExtendedShapelessRecipeBuilder<R> requires(Ingredient pIngredient) {
        return this.requires(pIngredient, 1);
    }

    public ExtendedShapelessRecipeBuilder<R> requires(Ingredient pIngredient, int pQuantity) {
        for(int i = 0; i < pQuantity; ++i) {
            this.ingredients.add(pIngredient);
        }

        return this;
    }

    public ExtendedShapelessRecipeBuilder<R> unlockedBy(String pName, Criterion<?> pCriterion) {
        this.criteria.put(pName, pCriterion);
        return this;
    }

    public ExtendedShapelessRecipeBuilder<R> group(String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result;
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

    public RecipeCategory category() {
        return category;
    }

    public ItemStack result() {
        return resultStack;
    }

    public NonNullList<Ingredient> ingredients() {
        return NonNullList.copyOf(ingredients);
    }

    public String group() {
        return group;
    }

    public static ShapelessRecipe vanillaFactory(Identifier id, ExtendedShapelessRecipeBuilder<ShapelessRecipe> builder) {
        // Basically the same as ShapelessRecipeBuilder, but doesn't fail if advancement is missing
        return new ShapelessRecipe(
                Objects.requireNonNullElse(builder.group, ""),
                RecipeBuilder.determineBookCategory(builder.category),
                builder.resultStack,
                builder.ingredients
        );
    }

    public static class Basic<R extends CraftingRecipe> extends ExtendedShapelessRecipeBuilder<R> {
        private final BiFunction<ResourceKey<Recipe<?>>, Basic<R>, R> factory;

        public Basic(HolderGetter<Item> items, RecipeCategory category, ItemStack result, BiFunction<ResourceKey<Recipe<?>>, Basic<R>, R> factory) {
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

        public Basic(HolderGetter<Item> items, RecipeCategory category, ItemStack result, Function4<String, CraftingBookCategory, ItemStack, List<Ingredient>, R> factory) {
            super(items, category, result);
            this.factory = convertConstructor(factory);
        }

        public Basic(HolderGetter<Item> items, RecipeCategory category, ItemLike result, int count, Function4<String, CraftingBookCategory, ItemStack, List<Ingredient>, R> factory) {
            super(items, category, result, count);
            this.factory = convertConstructor(factory);
        }

        public Basic(HolderGetter<Item> items, RecipeCategory category, ItemLike result, Function4<String, CraftingBookCategory, ItemStack, List<Ingredient>, R> factory) {
            super(items, category, result);
            this.factory = convertConstructor(factory);
        }

        private static <R extends CraftingRecipe> BiFunction<ResourceKey<Recipe<?>>, Basic<R>, R> convertConstructor(Function4<String, CraftingBookCategory, ItemStack, List<Ingredient>, R> factory) {
            return (id, builder) -> factory.apply(
                    builder.group,
                    RecipeBuilder.determineBookCategory(builder.category),
                    builder.resultStack,
                    builder.ingredients
            );
        }

        @Override
        public R createRecipe(ResourceKey<Recipe<?>> id) {
            return factory.apply(id, this);
        }
    }
}