package net.silentchaos512.lib.data.recipe;

import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import net.minecraft.advancements.*;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.RecipeUnlockedTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
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
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class ExtendedShapedRecipeBuilder<R extends CraftingRecipe> implements RecipeBuilder {
    private final HolderGetter<Item> items;
    protected final RecipeCategory category;
    protected final ItemStackTemplate result;
    protected final List<String> rows = new ArrayList<>();
    protected final Map<Character, Ingredient> key = new LinkedHashMap<>();
    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    protected Recipe.CommonInfo commonInfo = new Recipe.CommonInfo(true);
    protected CraftingRecipe.CraftingBookInfo bookInfo;

    public ExtendedShapedRecipeBuilder(HolderGetter<Item> items, RecipeCategory category, ItemStackTemplate result) {
        this.items = items;
        this.category = category;
        this.result = result;
        this.bookInfo = new CraftingRecipe.CraftingBookInfo(RecipeBuilder.determineCraftingBookCategory(this.category), "");
    }

    public abstract R createRecipe(ResourceKey<Recipe<?>> id);

    public ExtendedShapedRecipeBuilder<R> commonInfo(Recipe.CommonInfo commonInfo) {
        this.commonInfo = commonInfo;
        return this;
    }

    public ExtendedShapedRecipeBuilder<R> showNotification(boolean showNotification) {
        this.commonInfo = new Recipe.CommonInfo(showNotification);
        return this;
    }

    public ExtendedShapedRecipeBuilder<R> bookInfo(CraftingRecipe.CraftingBookInfo bookInfo) {
        this.bookInfo = bookInfo;
        return this;
    }

    @Override
    public ExtendedShapedRecipeBuilder<R> group(@Nullable String group) {
        this.bookInfo = new CraftingRecipe.CraftingBookInfo(this.bookInfo.category(), group == null ? "" : group);
        return this;
    }

    public ExtendedShapedRecipeBuilder<R> define(Character symbol, TagKey<Item> tagIn) {
        return this.define(symbol, Ingredient.of(this.items.getOrThrow(tagIn)));
    }

    public ExtendedShapedRecipeBuilder<R> define(Character symbol, ItemLike itemIn) {
        return this.define(symbol, Ingredient.of(itemIn));
    }

    public ExtendedShapedRecipeBuilder<R> define(Character symbol, ICustomIngredient customIngredient) {
        return this.define(symbol, new Ingredient(customIngredient));
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
        if (!this.rows.isEmpty() && patternIn.length() != this.rows.getFirst().length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            this.rows.add(patternIn);
            return this;
        }
    }

    @Override
    public ExtendedShapedRecipeBuilder<R> unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public void save(RecipeOutput output) {
        save(output, ResourceKey.create(Registries.RECIPE, NameUtils.fromItem(this.result)));
    }

    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
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
        AdvancementHolder advancementHolder = advancementBuilder != null
                ? advancementBuilder.build(id.identifier().withPrefix("recipes/" + this.category.getFolderName() + "/"))
                : null;
        output.accept(id, recipe, advancementHolder);
    }

    public RecipeCategory category() {
        return category;
    }

    public ItemStackTemplate result() {
        return result;
    }

    public ShapedRecipePattern pattern() {
        return ShapedRecipePattern.of(this.key, this.rows);
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return RecipeBuilder.getDefaultRecipeId(this.result);
    }

    public static ShapedRecipe vanillaFactory(Identifier id, ExtendedShapedRecipeBuilder<ShapedRecipe> builder) {
        // Basically the same as ShapedRecipeBuilder, but doesn't fail if advancement is missing
        return new ShapedRecipe(
                builder.commonInfo,
                builder.bookInfo,
                ShapedRecipePattern.of(builder.key, builder.rows),
                builder.result
        );
    }

    public static class Basic<R extends CraftingRecipe> extends ExtendedShapedRecipeBuilder<R> {
        private final BiFunction<ResourceKey<Recipe<?>>, Basic<R>, R> factory;

        public Basic(HolderGetter<Item> items, RecipeCategory category, ItemStackTemplate result, BiFunction<ResourceKey<Recipe<?>>, Basic<R>, R> factory) {
            super(items, category, result);
            this.factory = factory;
        }

        public Basic(HolderGetter<Item> items, RecipeCategory category, ItemLike result, BiFunction<ResourceKey<Recipe<?>>, Basic<R>, R> factory) {
            super(items, category, new ItemStackTemplate(result.asItem()));
            this.factory = factory;
        }

        public Basic(HolderGetter<Item> items, RecipeCategory category, ItemLike result, int count, BiFunction<ResourceKey<Recipe<?>>, Basic<R>, R> factory) {
            super(items, category, new ItemStackTemplate(result.asItem(), count));
            this.factory = factory;
        }

        public Basic(HolderGetter<Item> items, RecipeCategory category, ItemStackTemplate result, Factory<R> factory) {
            this(items, category, result, convertConstructor(factory));
        }

        public Basic(HolderGetter<Item> items, RecipeCategory category, ItemLike result, Factory<R> factory) {
            this(items, category, result, convertConstructor(factory));
        }

        public Basic(HolderGetter<Item> items, RecipeCategory category, ItemLike result, int count, Factory<R> factory) {
            this(items, category, result, convertConstructor(factory));
        }

        private static <R extends CraftingRecipe> BiFunction<ResourceKey<Recipe<?>>, Basic<R>, R> convertConstructor(Factory<R> factory) {
            return (id, builder) -> factory.apply(
                    builder.commonInfo,
                    builder.bookInfo,
                    builder.pattern(),
                    builder.result
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

    public interface Factory<R extends CraftingRecipe> extends Function4<Recipe.CommonInfo, CraftingRecipe.CraftingBookInfo, ShapedRecipePattern, ItemStackTemplate, R> {}
}
