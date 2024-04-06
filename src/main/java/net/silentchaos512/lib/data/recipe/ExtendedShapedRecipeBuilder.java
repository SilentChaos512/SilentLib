package net.silentchaos512.lib.data.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

/**
 * Very similar to {@link net.minecraft.data.recipes.ShapedRecipeBuilder}, but with a couple of changes.
 * Intended to generate {@link net.silentchaos512.lib.crafting.recipe.ExtendedShapedRecipe}s. Extra
 * data can be quickly added to serialization by either calling {@link #addExtraData(Consumer)} or
 * extending this class and overriding {@link #serializeExtra(JsonObject)}.
 * <p>
 * If an advancement criterion is not added, no advancement is generated, instead of throwing an
 * exception.
 */
@SuppressWarnings("WeakerAccess")
public class ExtendedShapedRecipeBuilder {
    private final RecipeSerializer<?> serializer;
    private final RecipeCategory category;
    private final Collection<Consumer<JsonObject>> extraData = new ArrayList<>();
    private final ItemStack result;
    private final List<String> rows = new ArrayList<>();
    private final Map<Character, Ingredient> key = new LinkedHashMap<>();
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    private String group = "";

    private ExtendedShapedRecipeBuilder(RecipeSerializer<?> serializer, RecipeCategory category, ItemStack result) {
        this.serializer = serializer;
        this.category = category;
        this.result = result;
    }

    public static ExtendedShapedRecipeBuilder shaped(RecipeSerializer<?> serializer, RecipeCategory category, ItemLike result) {
        return shaped(serializer, category, result, 1);
    }

    public static ExtendedShapedRecipeBuilder shaped(RecipeSerializer<?> serializer, RecipeCategory category, ItemLike result, int count) {
        return shaped(serializer, category, new ItemStack(result, count));
    }

    public static ExtendedShapedRecipeBuilder shaped(RecipeSerializer<?> serializer, RecipeCategory category, ItemStack result) {
        return new ExtendedShapedRecipeBuilder(serializer, category, result);
    }

    public static ExtendedShapedRecipeBuilder vanillaShaped(RecipeCategory category, ItemLike result) {
        return vanillaShaped(category, result, 1);
    }

    public static ExtendedShapedRecipeBuilder vanillaShaped(RecipeCategory category, ItemLike result, int count) {
        return vanillaShaped(category, new ItemStack(result, count));
    }

    public static ExtendedShapedRecipeBuilder vanillaShaped(RecipeCategory category, ItemStack result) {
        return new ExtendedShapedRecipeBuilder(RecipeSerializer.SHAPED_RECIPE, category, result);
    }

    /**
     * Override to quickly add additional data to serialization
     *
     * @param json The recipe JSON
     */
    protected void serializeExtra(JsonObject json) {
        this.extraData.forEach(consumer -> consumer.accept(json));
    }

    /**
     * Allows extra data to be quickly appended for simple serializers. For more complex
     * serializers, consider extending this class and overriding {@link #serializeExtra(JsonObject)}
     * instead.
     *
     * @param extraDataIn Changes to make to the recipe JSON (called after base JSON is generated)
     * @return The recipe builder
     */
    public ExtendedShapedRecipeBuilder addExtraData(Consumer<JsonObject> extraDataIn) {
        this.extraData.add(extraDataIn);
        return this;
    }

    public ExtendedShapedRecipeBuilder define(Character symbol, TagKey<Item> tagIn) {
        return this.define(symbol, Ingredient.of(tagIn));
    }

    public ExtendedShapedRecipeBuilder define(Character symbol, ItemLike itemIn) {
        return this.define(symbol, Ingredient.of(itemIn));
    }

    public ExtendedShapedRecipeBuilder define(Character symbol, Ingredient ingredientIn) {
        if (this.key.containsKey(symbol)) {
            throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
        } else if (symbol == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            this.key.put(symbol, ingredientIn);
            return this;
        }
    }

    public ExtendedShapedRecipeBuilder pattern(String patternIn) {
        if (!this.rows.isEmpty() && patternIn.length() != this.rows.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            this.rows.add(patternIn);
            return this;
        }
    }

    public ExtendedShapedRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    public ExtendedShapedRecipeBuilder group(String groupIn) {
        this.group = groupIn;
        return this;
    }

    public void save(RecipeOutput output) {
        save(output, NameUtils.fromItem(this.result));
    }

    public void save(RecipeOutput output, ResourceLocation id) {
        ShapedRecipePattern pattern = this.ensureValid(id);
        if (!this.criteria.isEmpty()) {
            Advancement.Builder advancementBuilder = output.advancement()
                    .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                    .rewards(AdvancementRewards.Builder.recipe(id))
                    .requirements(AdvancementRequirements.Strategy.OR);
            this.criteria.forEach(advancementBuilder::addCriterion);
        }

        ResourceLocation advancementId = new ResourceLocation(id.getNamespace(), "recipes/" + this.category.getFolderName() + "/" + id.getPath());
        output.accept(new Result(id, this, advancementId));
    }

    private ShapedRecipePattern ensureValid(ResourceLocation id) {
        // Basically the same as ShapedRecipeBuilder, but doesn't fail if advancement is missing
        return ShapedRecipePattern.of(this.key, this.rows);
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final ExtendedShapedRecipeBuilder builder;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, ExtendedShapedRecipeBuilder builder, ResourceLocation advancementId) {
            this.id = id;
            this.builder = builder;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            if (!builder.group.isEmpty()) {
                json.addProperty("group", builder.group);
            }

            JsonArray pattern = new JsonArray();
            builder.rows.forEach(pattern::add);
            json.add("pattern", pattern);

            JsonObject key = new JsonObject();
            builder.key.forEach((c, ingredient) -> key.add(String.valueOf(c), ingredient.toJson()));
            json.add("key", key);

            JsonObject result = new JsonObject();
            result.addProperty("item", NameUtils.fromItem(builder.result).toString());
            if (builder.count > 1) {
                result.addProperty("count", builder.count);
            }
            json.add("result", result);

            builder.serializeExtra(json);
        }

        @Override
        public RecipeSerializer<?> getType() {
            return builder.serializer;
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return builder.hasAdvancementCriterion ? builder.advancementBuilder.serializeToJson() : null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return builder.hasAdvancementCriterion ? advancementId : null;
        }
    }
}
