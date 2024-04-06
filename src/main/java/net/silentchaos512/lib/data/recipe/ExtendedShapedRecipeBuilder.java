package net.silentchaos512.lib.data.recipe;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import net.silentchaos512.lib.util.NameUtils;

import java.util.*;
import java.util.function.BiFunction;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class ExtendedShapedRecipeBuilder<R extends ShapedRecipe> {
    protected final RecipeCategory category;
    protected final ItemStack result;
    protected final List<String> rows = new ArrayList<>();
    protected final Map<Character, Ingredient> key = new LinkedHashMap<>();
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    protected String group = "";
    protected boolean showNotification = true;

    public ExtendedShapedRecipeBuilder(RecipeCategory category, ItemStack result) {
        this.category = category;
        this.result = result;
    }

    public abstract R createRecipe(ResourceLocation id);

    public ExtendedShapedRecipeBuilder<R> define(Character symbol, TagKey<Item> tagIn) {
        return this.define(symbol, Ingredient.of(tagIn));
    }

    public ExtendedShapedRecipeBuilder<R> define(Character symbol, ItemLike itemIn) {
        return this.define(symbol, Ingredient.of(itemIn));
    }

    public ExtendedShapedRecipeBuilder<R> define(Character symbol, Ingredient ingredientIn) {
        if (this.key.containsKey(symbol)) {
            throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
        } else if (symbol == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            this.key.put(symbol, ingredientIn);
            return this;
        }
    }

    public ExtendedShapedRecipeBuilder<R> pattern(String patternIn) {
        if (!this.rows.isEmpty() && patternIn.length() != this.rows.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            this.rows.add(patternIn);
            return this;
        }
    }

    public ExtendedShapedRecipeBuilder<R> unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    public ExtendedShapedRecipeBuilder<R> group(String groupIn) {
        this.group = groupIn;
        return this;
    }

    public ExtendedShapedRecipeBuilder<R> showNotification(boolean showNotification) {
        this.showNotification = showNotification;
        return this;
    }

    public void save(RecipeOutput output) {
        save(output, NameUtils.fromItem(this.result));
    }

    public void save(RecipeOutput output, ResourceLocation id) {
        ShapedRecipePattern pattern = ShapedRecipePattern.of(this.key, this.rows);
        Advancement.Builder advancementBuilder = null;
        if (!this.criteria.isEmpty()) {
            advancementBuilder = output.advancement()
                    .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                    .rewards(AdvancementRewards.Builder.recipe(id))
                    .requirements(AdvancementRequirements.Strategy.OR);
            this.criteria.forEach(advancementBuilder::addCriterion);
        }

        R recipe = createRecipe(id);
        AdvancementHolder advancementHolder = advancementBuilder != null ? advancementBuilder.build(id.withPrefix("recipes/" + this.category.getFolderName() + "/")) : null;
        output.accept(id, recipe, advancementHolder);
    }

    public static ShapedRecipe vanillaFactory(ResourceLocation id, ExtendedShapedRecipeBuilder<ShapedRecipe> builder) {
        // Basically the same as ShapedRecipeBuilder, but doesn't fail if advancement is missing
        ShapedRecipePattern pattern = ShapedRecipePattern.of(builder.key, builder.rows);
        return new ShapedRecipe(
                Objects.requireNonNullElse(builder.group, ""),
                RecipeBuilder.determineBookCategory(builder.category),
                pattern,
                builder.result,
                builder.showNotification
        );
    }

    public static class Basic<R extends ShapedRecipe> extends ExtendedShapedRecipeBuilder<R> {
        private final BiFunction<ResourceLocation, Basic<R>, R> factory;

        public Basic(RecipeCategory category, ItemStack result, BiFunction<ResourceLocation, Basic<R>, R> factory) {
            super(category, result);
            this.factory = factory;
        }

        @Override
        public R createRecipe(ResourceLocation id) {
            return factory.apply(id, this);
        }
    }
}
