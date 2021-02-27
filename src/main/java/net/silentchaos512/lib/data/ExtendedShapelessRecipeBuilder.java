package net.silentchaos512.lib.data;

import com.google.common.collect.Lists;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * Very similar to {@link net.minecraft.data.ShapelessRecipeBuilder}, but with a couple of changes.
 * Intended to generate {@link net.silentchaos512.lib.crafting.recipe.ExtendedShapelessRecipe}s.
 * Extra data can be quickly added to serialization by either calling {@link
 * #addExtraData(Consumer)} or extending this class and overriding {@link
 * #serializeExtra(JsonObject)}.
 * <p>
 * If an advancement criterion is not added, no advancement is generated, instead of throwing an
 * exception.
 * @deprecated Use {@link net.silentchaos512.lib.data.recipe.ExtendedShapelessRecipeBuilder} instead
 */
@SuppressWarnings("WeakerAccess")
@Deprecated
public class ExtendedShapelessRecipeBuilder {
    private final IRecipeSerializer<?> serializer;
    private final Collection<Consumer<JsonObject>> extraData = new ArrayList<>();
    private final Item result;
    private final int count;
    private final List<Ingredient> ingredients = Lists.newArrayList();
    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
    private boolean hasAdvancementCriterion = false;
    private String group = "";

    protected ExtendedShapelessRecipeBuilder(IRecipeSerializer<?> serializer, IItemProvider result, int count) {
        this.serializer = serializer;
        this.result = result.asItem();
        this.count = count;
    }

    public static ExtendedShapelessRecipeBuilder builder(IRecipeSerializer<?> serializer, IItemProvider result) {
        return builder(serializer, result, 1);
    }

    public static ExtendedShapelessRecipeBuilder builder(IRecipeSerializer<?> serializer, IItemProvider result, int count) {
        return new ExtendedShapelessRecipeBuilder(serializer, result, count);
    }

    public static ExtendedShapelessRecipeBuilder vanillaBuilder(IItemProvider result) {
        return vanillaBuilder(result, 1);
    }

    public static ExtendedShapelessRecipeBuilder vanillaBuilder(IItemProvider result, int count) {
        return new ExtendedShapelessRecipeBuilder(IRecipeSerializer.CRAFTING_SHAPELESS, result, count);
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
    public ExtendedShapelessRecipeBuilder addExtraData(Consumer<JsonObject> extraDataIn) {
        this.extraData.add(extraDataIn);
        return this;
    }

    public ExtendedShapelessRecipeBuilder addIngredient(ITag<Item> tag) {
        return addIngredient(tag, 1);
    }

    public ExtendedShapelessRecipeBuilder addIngredient(ITag<Item> tag, int quantity) {
        return addIngredient(Ingredient.fromTag(tag), quantity);
    }

    public ExtendedShapelessRecipeBuilder addIngredient(IItemProvider item) {
        return addIngredient(item, 1);
    }

    public ExtendedShapelessRecipeBuilder addIngredient(IItemProvider item, int quantity) {
        return addIngredient(Ingredient.fromItems(item), quantity);
    }

    public ExtendedShapelessRecipeBuilder addIngredient(Ingredient ingredient) {
        return addIngredient(ingredient, 1);
    }

    public ExtendedShapelessRecipeBuilder addIngredient(Ingredient ingredient, int quantity) {
        for (int i = 0; i < quantity; ++i) {
            this.ingredients.add(ingredient);
        }
        return this;
    }

    public ExtendedShapelessRecipeBuilder addCriterion(String name, ICriterionInstance criterion) {
        this.advancementBuilder.withCriterion(name, criterion);
        this.hasAdvancementCriterion = true;
        return this;
    }

    public ExtendedShapelessRecipeBuilder setGroup(String group) {
        this.group = group;
        return this;
    }

    public void build(Consumer<IFinishedRecipe> consumer) {
        build(consumer, NameUtils.from(this.result));
    }

    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
        if (this.hasAdvancementCriterion && !this.advancementBuilder.getCriteria().isEmpty()) {
            this.advancementBuilder.withParentId(new ResourceLocation("recipes/root"))
                    .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, id))
                    .withRewards(AdvancementRewards.Builder.recipe(id))
                    .withRequirementsStrategy(IRequirementsStrategy.OR);
        }
        ResourceLocation advancementId = new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getGroup().getPath() + "/" + id.getPath());
        consumer.accept(new Result(id, this, advancementId));
    }

    public static class Result implements IFinishedRecipe {
        private final ResourceLocation id;
        private final ExtendedShapelessRecipeBuilder builder;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, ExtendedShapelessRecipeBuilder builder, ResourceLocation advancementId) {
            this.id = id;
            this.builder = builder;
            this.advancementId = advancementId;
        }

        @Override
        public void serialize(JsonObject json) {
            if (!builder.group.isEmpty()) {
                json.addProperty("group", builder.group);
            }

            JsonArray ingredients = new JsonArray();
            for (Ingredient ingredient : builder.ingredients) {
                ingredients.add(ingredient.serialize());
            }
            json.add("ingredients", ingredients);

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
