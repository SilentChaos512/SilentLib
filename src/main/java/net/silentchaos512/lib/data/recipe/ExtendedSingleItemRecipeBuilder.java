package net.silentchaos512.lib.data.recipe;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class ExtendedSingleItemRecipeBuilder {
    private final RecipeSerializer<?> serializer;
    private final RecipeCategory category;
    private final Collection<Consumer<JsonObject>> extraData = new ArrayList<>();
    private final Ingredient ingredients;
    private final Item result;
    private final int count;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
    private boolean hasAdvancementCriterion = false;
    private String group = "";

    protected ExtendedSingleItemRecipeBuilder(RecipeSerializer<?> serializer, RecipeCategory category, Ingredient ingredients, ItemLike result, int count) {
        this.serializer = serializer;
        this.category = category;
        this.ingredients = ingredients;
        this.result = result.asItem();
        this.count = count;
    }

    public static ExtendedSingleItemRecipeBuilder builder(RecipeSerializer<?> serializer, RecipeCategory category, Ingredient ingredient, ItemLike result) {
        return builder(serializer, category, ingredient, result, 1);
    }

    public static ExtendedSingleItemRecipeBuilder builder(RecipeSerializer<?> serializer, RecipeCategory category, Ingredient ingredient, ItemLike result, int count) {
        return new ExtendedSingleItemRecipeBuilder(serializer, category, ingredient, result, count);
    }

    public static ExtendedSingleItemRecipeBuilder stonecuttingBuilder(RecipeCategory category, Ingredient ingredient, ItemLike result) {
        return stonecuttingBuilder(category, ingredient, result, 1);
    }

    public static ExtendedSingleItemRecipeBuilder stonecuttingBuilder(RecipeCategory category, Ingredient ingredient, ItemLike result, int count) {
        return new ExtendedSingleItemRecipeBuilder(RecipeSerializer.STONECUTTER, category, ingredient, result, count);
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
    public ExtendedSingleItemRecipeBuilder addExtraData(Consumer<JsonObject> extraDataIn) {
        this.extraData.add(extraDataIn);
        return this;
    }

    public ExtendedSingleItemRecipeBuilder addCriterion(String name, CriterionTriggerInstance criterion) {
        this.advancementBuilder.addCriterion(name, criterion);
        this.hasAdvancementCriterion = true;
        return this;
    }

    public ExtendedSingleItemRecipeBuilder setGroup(String group) {
        this.group = group;
        return this;
    }

    public void build(Consumer<FinishedRecipe> consumer) {
        ResourceLocation itemId = NameUtils.fromItem(this.result);
        ResourceLocation serializerId = NameUtils.fromRecipeSerializer(this.serializer);
        build(consumer, new ResourceLocation(itemId.getNamespace(), serializerId.getPath() + "/" + itemId.getPath()));
    }

    public void build(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        if (this.hasAdvancementCriterion && !this.advancementBuilder.getCriteria().isEmpty()) {
            this.advancementBuilder.parent(new ResourceLocation("recipes/root"))
                    .addCriterion("has_the_recipe", new RecipeUnlockedTrigger.TriggerInstance(ContextAwarePredicate.ANY, id))
                    .rewards(AdvancementRewards.Builder.recipe(id))
                    .requirements(RequirementsStrategy.OR);
        }
        ResourceLocation advancementId = new ResourceLocation(id.getNamespace(), "recipes/" + this.category.getFolderName() + "/" + id.getPath());
        consumer.accept(new ExtendedSingleItemRecipeBuilder.Result(id, this, advancementId));
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final ExtendedSingleItemRecipeBuilder builder;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, ExtendedSingleItemRecipeBuilder builder, ResourceLocation advancementId) {
            this.id = id;
            this.builder = builder;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            if (!builder.group.isEmpty()) {
                json.addProperty("group", builder.group);
            }

            json.add("ingredient", builder.ingredients.toJson());
            json.addProperty("result", NameUtils.fromItem(builder.result).toString());
            json.addProperty("count", builder.count);

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
