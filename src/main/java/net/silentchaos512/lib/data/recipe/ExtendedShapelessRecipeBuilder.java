package net.silentchaos512.lib.data.recipe;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * Very similar to {@link net.minecraft.data.recipes.ShapelessRecipeBuilder}, but with a couple of changes.
 * Intended to generate {@link net.silentchaos512.lib.crafting.recipe.ExtendedShapelessRecipe}s.
 * Extra data can be quickly added to serialization by either calling {@link
 * #addExtraData(Consumer)} or extending this class and overriding {@link
 * #serializeExtra(JsonObject)}.
 * <p>
 * If an advancement criterion is not added, no advancement is generated, instead of throwing an
 * exception.
 */
@SuppressWarnings("WeakerAccess")
public class ExtendedShapelessRecipeBuilder {
    private final RecipeSerializer<?> serializer;
    private final RecipeCategory category;
    private final Collection<Consumer<JsonObject>> extraData = new ArrayList<>();
    private final Item result;
    private final int count;
    private final List<Ingredient> ingredients = Lists.newArrayList();
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
    private boolean hasAdvancementCriterion = false;
    private String group = "";

    protected ExtendedShapelessRecipeBuilder(RecipeSerializer<?> serializer, RecipeCategory category, ItemLike result, int count) {
        this.serializer = serializer;
        this.category = category;
        this.result = result.asItem();
        this.count = count;
    }

    public static ExtendedShapelessRecipeBuilder builder(RecipeSerializer<?> serializer, RecipeCategory category, ItemLike result) {
        return builder(serializer,category , result, 1);
    }

    public static ExtendedShapelessRecipeBuilder builder(RecipeSerializer<?> serializer, RecipeCategory category, ItemLike result, int count) {
        return new ExtendedShapelessRecipeBuilder(serializer, category, result, count);
    }

    public static ExtendedShapelessRecipeBuilder vanillaBuilder(RecipeCategory category, ItemLike result) {
        return vanillaBuilder(category, result, 1);
    }

    public static ExtendedShapelessRecipeBuilder vanillaBuilder(RecipeCategory category, ItemLike result, int count) {
        return new ExtendedShapelessRecipeBuilder(RecipeSerializer.SHAPELESS_RECIPE, category, result, count);
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

    public ExtendedShapelessRecipeBuilder requires(TagKey<Item> tag) {
        return requires(tag, 1);
    }

    public ExtendedShapelessRecipeBuilder requires(TagKey<Item> tag, int quantity) {
        return requires(Ingredient.of(tag), quantity);
    }

    public ExtendedShapelessRecipeBuilder requires(ItemLike item) {
        return requires(item, 1);
    }

    public ExtendedShapelessRecipeBuilder requires(ItemLike item, int quantity) {
        return requires(Ingredient.of(item), quantity);
    }

    public ExtendedShapelessRecipeBuilder requires(Ingredient ingredient) {
        return requires(ingredient, 1);
    }

    public ExtendedShapelessRecipeBuilder requires(Ingredient ingredient, int quantity) {
        for (int i = 0; i < quantity; ++i) {
            this.ingredients.add(ingredient);
        }
        return this;
    }

    public ExtendedShapelessRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterion) {
        this.advancementBuilder.addCriterion(name, criterion);
        this.hasAdvancementCriterion = true;
        return this;
    }

    public ExtendedShapelessRecipeBuilder group(String group) {
        this.group = group;
        return this;
    }

    public void save(Consumer<FinishedRecipe> consumer) {
        save(consumer, NameUtils.fromItem(this.result));
    }

    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        if (this.hasAdvancementCriterion && !this.advancementBuilder.getCriteria().isEmpty()) {
            this.advancementBuilder.parent(new ResourceLocation("recipes/root"))
                    .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                    .rewards(AdvancementRewards.Builder.recipe(id))
                    .requirements(RequirementsStrategy.OR);
        }
        ResourceLocation advancementId = new ResourceLocation(id.getNamespace(), "recipes/" + this.category.getFolderName() + "/" + id.getPath());
        consumer.accept(new Result(id, this, advancementId));
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final ExtendedShapelessRecipeBuilder builder;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, ExtendedShapelessRecipeBuilder builder, ResourceLocation advancementId) {
            this.id = id;
            this.builder = builder;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            if (!builder.group.isEmpty()) {
                json.addProperty("group", builder.group);
            }

            JsonArray ingredients = new JsonArray();
            for (Ingredient ingredient : builder.ingredients) {
                ingredients.add(ingredient.toJson());
            }
            json.add("ingredients", ingredients);

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
