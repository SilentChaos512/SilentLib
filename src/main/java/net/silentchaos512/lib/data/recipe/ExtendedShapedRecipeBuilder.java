package net.silentchaos512.lib.data.recipe;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

/**
 * Very similar to {@link net.minecraft.data.ShapedRecipeBuilder}, but with a couple of changes.
 * Intended to generate {@link net.silentchaos512.lib.crafting.recipe.ExtendedShapedRecipe}s. Extra
 * data can be quickly added to serialization by either calling {@link #addExtraData(Consumer)} or
 * extending this class and overriding {@link #serializeExtra(JsonObject)}.
 * <p>
 * If an advancement criterion is not added, no advancement is generated, instead of throwing an
 * exception.
 */
@SuppressWarnings("WeakerAccess")
public class ExtendedShapedRecipeBuilder {
    private final IRecipeSerializer<?> serializer;
    private final Collection<Consumer<JsonObject>> extraData = new ArrayList<>();
    private final Item result;
    private final int count;
    private final List<String> pattern = new ArrayList<>();
    private final Map<Character, Ingredient> key = new LinkedHashMap<>();
    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
    private boolean hasAdvancementCriterion = false;
    private String group = "";

    private ExtendedShapedRecipeBuilder(IRecipeSerializer<?> serializer, IItemProvider result, int count) {
        this.serializer = serializer;
        this.result = result.asItem();
        this.count = count;
    }

    public static ExtendedShapedRecipeBuilder builder(IRecipeSerializer<?> serializer, IItemProvider result) {
        return builder(serializer, result, 1);
    }

    public static ExtendedShapedRecipeBuilder builder(IRecipeSerializer<?> serializer, IItemProvider result, int count) {
        return new ExtendedShapedRecipeBuilder(serializer, result, count);
    }

    public static ExtendedShapedRecipeBuilder vanillaBuilder(IItemProvider result) {
        return vanillaBuilder(result, 1);
    }

    public static ExtendedShapedRecipeBuilder vanillaBuilder(IItemProvider result, int count) {
        return new ExtendedShapedRecipeBuilder(IRecipeSerializer.CRAFTING_SHAPED, result, count);
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

    public ExtendedShapedRecipeBuilder key(Character symbol, ITag<Item> tagIn) {
        return this.key(symbol, Ingredient.fromTag(tagIn));
    }

    public ExtendedShapedRecipeBuilder key(Character symbol, IItemProvider itemIn) {
        return this.key(symbol, Ingredient.fromItems(itemIn));
    }

    public ExtendedShapedRecipeBuilder key(Character symbol, Ingredient ingredientIn) {
        if (this.key.containsKey(symbol)) {
            throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
        } else if (symbol == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            this.key.put(symbol, ingredientIn);
            return this;
        }
    }

    public ExtendedShapedRecipeBuilder patternLine(String patternIn) {
        if (!this.pattern.isEmpty() && patternIn.length() != this.pattern.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            this.pattern.add(patternIn);
            return this;
        }
    }

    public ExtendedShapedRecipeBuilder addCriterion(String name, ICriterionInstance criterionIn) {
        this.advancementBuilder.withCriterion(name, criterionIn);
        this.hasAdvancementCriterion = true;
        return this;
    }

    public ExtendedShapedRecipeBuilder setGroup(String groupIn) {
        this.group = groupIn;
        return this;
    }

    public void build(Consumer<IFinishedRecipe> consumer) {
        build(consumer, NameUtils.from(this.result));
    }

    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
        this.validate(id);
        if (this.hasAdvancementCriterion && !this.advancementBuilder.getCriteria().isEmpty()) {
            this.advancementBuilder.withParentId(new ResourceLocation("recipes/root"))
                    .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, id))
                    .withRewards(AdvancementRewards.Builder.recipe(id))
                    .withRequirementsStrategy(IRequirementsStrategy.OR);
        }
        ResourceLocation advancementId = new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getGroup().getPath() + "/" + id.getPath());
        consumer.accept(new Result(id, this, advancementId));
    }

    private void validate(ResourceLocation id) {
        // Basically the same as ShapedRecipeBuilder, but doesn't fail if advancement is missing
        if (this.pattern.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + id + "!");
        } else {
            Set<Character> set = Sets.newHashSet(this.key.keySet());
            set.remove(' ');

            for (String s : this.pattern) {
                for (int i = 0; i < s.length(); ++i) {
                    char c0 = s.charAt(i);
                    if (!this.key.containsKey(c0) && c0 != ' ') {
                        throw new IllegalStateException("Pattern in recipe " + id + " uses undefined symbol '" + c0 + "'");
                    }

                    set.remove(c0);
                }
            }

            if (!set.isEmpty()) {
                throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + id);
            } else if (this.pattern.size() == 1 && this.pattern.get(0).length() == 1) {
                throw new IllegalStateException("Shaped recipe " + id + " only takes in a single item - should it be a shapeless recipe instead?");
            }
        }
    }

    public static class Result implements IFinishedRecipe {
        private final ResourceLocation id;
        private final ExtendedShapedRecipeBuilder builder;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, ExtendedShapedRecipeBuilder builder, ResourceLocation advancementId) {
            this.id = id;
            this.builder = builder;
            this.advancementId = advancementId;
        }

        @Override
        public void serialize(JsonObject json) {
            if (!builder.group.isEmpty()) {
                json.addProperty("group", builder.group);
            }

            JsonArray pattern = new JsonArray();
            builder.pattern.forEach(pattern::add);
            json.add("pattern", pattern);

            JsonObject key = new JsonObject();
            builder.key.forEach((c, ingredient) -> key.add(String.valueOf(c), ingredient.serialize()));
            json.add("key", key);

            JsonObject result = new JsonObject();
            result.addProperty("item", NameUtils.from(builder.result).toString());
            if (builder.count > 1) {
                result.addProperty("count", builder.count);
            }
            json.add("result", result);

            builder.serializeExtra(json);
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return builder.serializer;
        }

        @Override
        public ResourceLocation getID() {
            return id;
        }

        @Nullable
        @Override
        public JsonObject getAdvancementJson() {
            return builder.hasAdvancementCriterion ? builder.advancementBuilder.serialize() : null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementID() {
            return builder.hasAdvancementCriterion ? advancementId : null;
        }
    }
}
