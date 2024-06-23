package net.silentchaos512.lib.data.recipe;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@SuppressWarnings({"SameParameterValue", "MethodMayBeStatic", "WeakerAccess", "unused"})
public abstract class LibRecipeProvider extends RecipeProvider {
    private final String modId;

    public LibRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, String modId) {
        super(packOutput, registries);
        this.modId = modId;
    }

    @Override
    protected abstract void buildRecipes(RecipeOutput consumer);

    /**
     * Gets a {@link ResourceLocation} with {@link #modId} as the namespace. This is used
     * internally, but may be used by the extending class as well.
     *
     * @param path The path to use
     * @return A {@link ResourceLocation} with {@link #modId} as the namespace and the given path
     */
    protected ResourceLocation modId(String path) {
        return ResourceLocation.fromNamespaceAndPath(this.modId, path);
    }

    protected void registerCustomRecipe(RecipeOutput consumer, Function<CraftingBookCategory, Recipe<?>> serializer, ResourceLocation recipeId) {
        SpecialRecipeBuilder.special(serializer).save(consumer, recipeId.toString());
    }

    /**
     * Adds standard recipes for smelting something in both the furnace and blast furnace. The
     * furnace recipe will take 200 ticks and the blast furnace 100 ticks (same as vanilla
     * recipes).
     * <p>
     * The recipes will be saved to {@code mod_id:blasting/id} and {@code mod_id:smelting/id}, where
     * {@code id} is the String parameter you called the method with.
     *
     * @param consumer     RecipeOutput
     * @param id           Recipe path ending
     * @param ingredientIn The ingredient (ore, etc.)
     * @param result       The result (ingot, gem, etc.)
     * @param experienceIn The experience (XP) the recipe yields
     */
    protected void smeltingAndBlastingRecipes(RecipeOutput consumer, String id, ItemLike ingredientIn, ItemLike result, float experienceIn) {
        smeltingAndBlastingRecipes(consumer, id, Ingredient.of(ingredientIn), result, experienceIn);
    }

    /**
     * Adds standard recipes for smelting something in both the furnace and blast furnace. The
     * furnace recipe will take 200 ticks and the blast furnace 100 ticks (same as vanilla
     * recipes).
     * <p>
     * The recipes will be saved to {@code mod_id:blasting/id} and {@code mod_id:smelting/id}, where
     * {@code id} is the String parameter you called the method with.
     *
     * @param consumer     RecipeOutput
     * @param id           Recipe path ending
     * @param ingredientIn The ingredient (ore, etc.)
     * @param result       The result (ingot, gem, etc.)
     * @param experienceIn The experience (XP) the recipe yields
     */
    protected void smeltingAndBlastingRecipes(RecipeOutput consumer, String id, TagKey<Item> ingredientIn, ItemLike result, float experienceIn) {
        smeltingAndBlastingRecipes(consumer, id, Ingredient.of(ingredientIn), result, experienceIn);
    }

    /**
     * Adds standard recipes for smelting something in both the furnace and blast furnace. The
     * furnace recipe will take 200 ticks and the blast furnace 100 ticks (same as vanilla
     * recipes).
     * <p>
     * The recipes will be saved to {@code mod_id:blasting/id} and {@code mod_id:smelting/id}, where
     * {@code id} is the String parameter you called the method with.
     *
     * @param consumer     RecipeOutput
     * @param id           Recipe path ending
     * @param ingredientIn The ingredient (ore, etc.)
     * @param result       The result (ingot, gem, etc.)
     * @param experienceIn The experience (XP) the recipe yields
     */
    protected void smeltingAndBlastingRecipes(RecipeOutput consumer, String id, Ingredient ingredientIn, ItemLike result, float experienceIn) {
        SimpleCookingRecipeBuilder.blasting(ingredientIn, RecipeCategory.MISC, result, experienceIn, 100)
                .unlockedBy("impossible", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(consumer, modId("blasting/" + id));
        SimpleCookingRecipeBuilder.smelting(ingredientIn, RecipeCategory.MISC, result, experienceIn, 200)
                .unlockedBy("impossible", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(consumer, modId("smelting/" + id));
    }

    /**
     * Adds recipes that convert between items, where nine of one is used to craft the bigger
     * version. These items can be anything, but this is typically used for things like metal
     * ingots/blocks/nuggets.
     * <p>
     * If the {@code nugget} parameter is null, block/item recipes will still generate, but the
     * nugget recipes will not.
     *
     * @param consumer RecipeOutput
     * @param block    The block item (mandatory). Does not need to be a block, but is assumed to be
     *                 one.
     * @param item     The normal item (ingot, gem, etc.) Again, this can be any item.
     * @param nugget   The nugget item (optional). Can be any item or null.
     */
    protected void compressionRecipes(RecipeOutput consumer, ItemLike block, ItemLike item, @Nullable ItemLike nugget) {
        compressionRecipes(consumer, RecipeCategory.MISC, block, item, nugget);
    }

    /**
     * Adds recipes that convert between items, where nine of one is used to craft the bigger
     * version. These items can be anything, but this is typically used for things like metal
     * ingots/blocks/nuggets.
     * <p>
     * If the {@code nugget} parameter is null, block/item recipes will still generate, but the
     * nugget recipes will not.
     *
     * @param consumer RecipeOutput
     * @param category The recipe cateogry
     * @param block    The block item (mandatory). Does not need to be a block, but is assumed to be
     *                 one.
     * @param item     The normal item (ingot, gem, etc.) Again, this can be any item.
     * @param nugget   The nugget item (optional). Can be any item or null.
     */
    protected void compressionRecipes(RecipeOutput consumer, RecipeCategory category, ItemLike block, ItemLike item, @Nullable ItemLike nugget) {
        String blockName = NameUtils.fromItem(block).getPath();
        String itemName = NameUtils.fromItem(item).getPath();

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, block, 1)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', item)
                .unlockedBy("has_item", has(item))
                .save(consumer, modId(itemName + "_from_block"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, item, 9)
                .requires(block)
                .unlockedBy("has_item", has(item))
                .save(consumer, modId(blockName));

        if (nugget != null) {
            String nuggetName = NameUtils.fromItem(nugget).getPath();

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, item, 1)
                    .pattern("###")
                    .pattern("###")
                    .pattern("###")
                    .define('#', nugget)
                    .unlockedBy("has_item", has(item))
                    .save(consumer, modId(itemName + "_from_nugget"));
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, nugget, 9)
                    .requires(item)
                    .unlockedBy("has_item", has(item))
                    .save(consumer, modId(nuggetName));
        }
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(TagKey<Item> tagKey) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(tagKey).build());
    }
}
