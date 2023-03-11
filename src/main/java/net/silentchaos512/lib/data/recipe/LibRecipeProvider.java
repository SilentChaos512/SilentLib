package net.silentchaos512.lib.data.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.function.Consumer;

@SuppressWarnings({"SameParameterValue", "MethodMayBeStatic", "WeakerAccess", "unused"})
public abstract class LibRecipeProvider extends RecipeProvider {
    private final String modId;

    public LibRecipeProvider(DataGenerator generatorIn, String modId) {
        super(generatorIn.getPackOutput());
        this.modId = modId;
    }

    @Override
    protected abstract void buildRecipes(Consumer<FinishedRecipe> consumer);

    /**
     * Gets a {@link ResourceLocation} with {@link #modId} as the namespace. This is used
     * internally, but may be used by the extending class as well.
     *
     * @param path The path to use
     * @return A {@link ResourceLocation} with {@link #modId} as the namespace and the given path
     */
    protected ResourceLocation modId(String path) {
        return new ResourceLocation(this.modId, path);
    }

    /**
     * Add recipe conditions to recipe JSON. This can be called from the {@code #addExtraData}
     * methods of some extended recipe builders.
     * <p>
     * Example: {@code shapedBuilder(...).addExtraData(json -> writeConditions(json, condition1,
     * conditions2...)...;}
     *
     * @param json       The recipe JSON
     * @param conditions The conditions to serialize
     */
    protected void writeConditions(JsonObject json, ICondition... conditions) {
        if (conditions.length > 0) {
            JsonArray array = new JsonArray();
            for (ICondition condition : conditions) {
                array.add(CraftingHelper.serialize(condition));
            }
            json.add("conditions", array);
        }
    }

    protected ExtendedShapedRecipeBuilder shapedBuilder(RecipeCategory category, ItemLike result) {
        return ExtendedShapedRecipeBuilder.vanillaBuilder(category, result, 1);
    }

    protected ExtendedShapedRecipeBuilder shapedBuilder(RecipeCategory category, ItemLike result, int count) {
        return ExtendedShapedRecipeBuilder.vanillaBuilder(category, result, count);
    }

    protected ExtendedShapedRecipeBuilder shapedBuilder(RecipeSerializer<?> serializer, RecipeCategory category, ItemLike result) {
        return ExtendedShapedRecipeBuilder.builder(serializer, category, result, 1);
    }

    protected ExtendedShapedRecipeBuilder shapedBuilder(RecipeSerializer<?> serializer, RecipeCategory category, ItemLike result, int count) {
        return ExtendedShapedRecipeBuilder.builder(serializer, category, result, count);
    }

    protected ExtendedShapelessRecipeBuilder shapelessBuilder(RecipeCategory category, ItemLike result) {
        return ExtendedShapelessRecipeBuilder.vanillaBuilder(category, result, 1);
    }

    protected ExtendedShapelessRecipeBuilder shapelessBuilder(RecipeCategory category, ItemLike result, int count) {
        return ExtendedShapelessRecipeBuilder.vanillaBuilder(category, result, count);
    }

    protected ExtendedShapelessRecipeBuilder shapelessBuilder(RecipeSerializer<?> serializer, RecipeCategory category, ItemLike result) {
        return ExtendedShapelessRecipeBuilder.builder(serializer, category, result, 1);
    }

    protected ExtendedShapelessRecipeBuilder shapelessBuilder(RecipeSerializer<?> serializer, RecipeCategory category, ItemLike result, int count) {
        return ExtendedShapelessRecipeBuilder.builder(serializer, category, result, count);
    }

    protected DamageItemRecipeBuilder damageItemBuilder(RecipeCategory category, ItemLike result) {
        return DamageItemRecipeBuilder.builder(category, result, 1);
    }

    protected DamageItemRecipeBuilder damageItemBuilder(RecipeCategory category, ItemLike result, int count) {
        return DamageItemRecipeBuilder.builder(category, result, count);
    }

    protected DamageItemRecipeBuilder damageItemBuilder(RecipeSerializer<?> serializer, RecipeCategory category, ItemLike result) {
        return DamageItemRecipeBuilder.builder(serializer, category, result, 1);
    }

    protected DamageItemRecipeBuilder damageItemBuilder(RecipeSerializer<?> serializer, RecipeCategory category, ItemLike result, int count) {
        return DamageItemRecipeBuilder.builder(serializer, category, result, count);
    }

    protected void registerCustomRecipe(Consumer<FinishedRecipe> consumer, SimpleCraftingRecipeSerializer<?> serializer) {
        registerCustomRecipe(consumer, serializer, NameUtils.fromRecipeSerializer(serializer));
    }

    protected void registerCustomRecipe(Consumer<FinishedRecipe> consumer, SimpleCraftingRecipeSerializer<?> serializer, ResourceLocation recipeId) {
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
     * @param consumer     Consumer from {@link #buildRecipes(Consumer)}
     * @param id           Recipe path ending
     * @param ingredientIn The ingredient (ore, etc.)
     * @param result       The result (ingot, gem, etc.)
     * @param experienceIn The experience (XP) the recipe yields
     */
    protected void smeltingAndBlastingRecipes(Consumer<FinishedRecipe> consumer, String id, ItemLike ingredientIn, ItemLike result, float experienceIn) {
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
     * @param consumer     Consumer from {@link #buildRecipes(Consumer)}
     * @param id           Recipe path ending
     * @param ingredientIn The ingredient (ore, etc.)
     * @param result       The result (ingot, gem, etc.)
     * @param experienceIn The experience (XP) the recipe yields
     */
    protected void smeltingAndBlastingRecipes(Consumer<FinishedRecipe> consumer, String id, TagKey<Item> ingredientIn, ItemLike result, float experienceIn) {
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
     * @param consumer     Consumer from {@link #buildRecipes(Consumer)}
     * @param id           Recipe path ending
     * @param ingredientIn The ingredient (ore, etc.)
     * @param result       The result (ingot, gem, etc.)
     * @param experienceIn The experience (XP) the recipe yields
     */
    protected void smeltingAndBlastingRecipes(Consumer<FinishedRecipe> consumer, String id, Ingredient ingredientIn, ItemLike result, float experienceIn) {
        SimpleCookingRecipeBuilder.blasting(ingredientIn, RecipeCategory.MISC, result, experienceIn, 100)
                .unlockedBy("impossible", new ImpossibleTrigger.TriggerInstance())
                .save(consumer, modId("blasting/" + id));
        SimpleCookingRecipeBuilder.smelting(ingredientIn, RecipeCategory.MISC, result, experienceIn, 200)
                .unlockedBy("impossible", new ImpossibleTrigger.TriggerInstance())
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
     * @param consumer Consumer from {@link #buildRecipes(Consumer)}
     * @param block    The block item (mandatory). Does not need to be a block, but is assumed to be
     *                 one.
     * @param item     The normal item (ingot, gem, etc.) Again, this can be any item.
     * @param nugget   The nugget item (optional). Can be any item or null.
     */
    protected void compressionRecipes(Consumer<FinishedRecipe> consumer, ItemLike block, ItemLike item, @Nullable ItemLike nugget) {
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
     * @param consumer Consumer from {@link #buildRecipes(Consumer)}
     * @param category The recipe cateogry
     * @param block    The block item (mandatory). Does not need to be a block, but is assumed to be
     *                 one.
     * @param item     The normal item (ingot, gem, etc.) Again, this can be any item.
     * @param nugget   The nugget item (optional). Can be any item or null.
     */
    protected void compressionRecipes(Consumer<FinishedRecipe> consumer, RecipeCategory category, ItemLike block, ItemLike item, @Nullable ItemLike nugget) {
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

    protected static InventoryChangeTrigger.TriggerInstance has(TagKey<Item> p_206407_) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(p_206407_).build());
    }
}
