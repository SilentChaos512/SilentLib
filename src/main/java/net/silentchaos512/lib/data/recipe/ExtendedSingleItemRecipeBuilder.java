package net.silentchaos512.lib.data.recipe;

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
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class ExtendedSingleItemRecipeBuilder {
    private final IRecipeSerializer<?> serializer;
    private final Collection<Consumer<JsonObject>> extraData = new ArrayList<>();
    private final Ingredient ingredients;
    private final Item result;
    private final int count;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
    private boolean hasAdvancementCriterion = false;
    private String group = "";

    protected ExtendedSingleItemRecipeBuilder(IRecipeSerializer<?> serializer, Ingredient ingredients, IItemProvider result, int count) {
        this.serializer = serializer;
        this.ingredients = ingredients;
        this.result = result.asItem();
        this.count = count;
    }

    public static ExtendedSingleItemRecipeBuilder builder(IRecipeSerializer<?> serializer, Ingredient ingredient, IItemProvider result) {
        return builder(serializer, ingredient, result, 1);
    }

    public static ExtendedSingleItemRecipeBuilder builder(IRecipeSerializer<?> serializer, Ingredient ingredient, IItemProvider result, int count) {
        return new ExtendedSingleItemRecipeBuilder(serializer, ingredient, result, count);
    }

    public static ExtendedSingleItemRecipeBuilder stonecuttingBuilder(Ingredient ingredient, IItemProvider result) {
        return stonecuttingBuilder(ingredient, result, 1);
    }

    public static ExtendedSingleItemRecipeBuilder stonecuttingBuilder(Ingredient ingredient, IItemProvider result, int count) {
        return new ExtendedSingleItemRecipeBuilder(IRecipeSerializer.STONECUTTING, ingredient, result, count);
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

    public ExtendedSingleItemRecipeBuilder addCriterion(String name, ICriterionInstance criterion) {
        this.advancementBuilder.withCriterion(name, criterion);
        this.hasAdvancementCriterion = true;
        return this;
    }

    public ExtendedSingleItemRecipeBuilder setGroup(String group) {
        this.group = group;
        return this;
    }

    public void build(Consumer<IFinishedRecipe> consumer) {
        ResourceLocation itemId = NameUtils.from(this.result);
        ResourceLocation serializerId = NameUtils.from(this.serializer);
        build(consumer, new ResourceLocation(itemId.getNamespace(), serializerId.getPath() + "/" + itemId.getPath()));
    }

    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
        if (this.hasAdvancementCriterion && !this.advancementBuilder.getCriteria().isEmpty()) {
            this.advancementBuilder.withParentId(new ResourceLocation("recipes/root"))
                    .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, id))
                    .withRewards(AdvancementRewards.Builder.recipe(id))
                    .withRequirementsStrategy(IRequirementsStrategy.OR);
        }
        ResourceLocation advancementId = new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getGroup().getPath() + "/" + id.getPath());
        consumer.accept(new ExtendedSingleItemRecipeBuilder.Result(id, this, advancementId));
    }

    public static class Result implements IFinishedRecipe {
        private final ResourceLocation id;
        private final ExtendedSingleItemRecipeBuilder builder;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, ExtendedSingleItemRecipeBuilder builder, ResourceLocation advancementId) {
            this.id = id;
            this.builder = builder;
            this.advancementId = advancementId;
        }

        @Override
        public void serialize(JsonObject json) {
            if (!builder.group.isEmpty()) {
                json.addProperty("group", builder.group);
            }

            json.add("ingredient", builder.ingredients.serialize());
            json.addProperty("result", NameUtils.from(builder.result).toString());
            json.addProperty("count", builder.count);

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
