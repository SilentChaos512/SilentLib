package net.silentchaos512.lib.data.recipe;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.criterion.ImpossibleTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings({"SameParameterValue", "MethodMayBeStatic", "WeakerAccess", "unused"})
public abstract class LibRecipeProvider extends RecipeProvider {
    private final String modId;
    protected final HolderGetter<Item> items;

    public static <T extends RecipeProvider> RecipeProvider.Runner createRunner(
            PackOutput packOutput,
            CompletableFuture<HolderLookup.Provider> registryLookup,
            String name,
            BiFunction<HolderLookup.Provider, RecipeOutput, T> constructor
    ) {
        return new RecipeProvider.Runner(packOutput, registryLookup) {
            @Override
            protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
                return constructor.apply(registries, output);
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }

    public LibRecipeProvider(HolderLookup.Provider registries, RecipeOutput recipeOutput, String modId) {
        super(registries, recipeOutput);
        this.modId = modId;
        this.items = registries.lookupOrThrow(Registries.ITEM);
    }

    @Override
    protected abstract void buildRecipes();

    /**
     * Gets a {@link net.minecraft.resources.Identifier} with {@link #modId} as the namespace. This is used
     * internally, but may be used by the extending class as well.
     *
     * @param path The path to use
     * @return A {@link net.minecraft.resources.Identifier} with {@link #modId} as the namespace and the given path
     */
    protected ResourceKey<Recipe<?>> modId(String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(this.modId, path));
    }

    protected void registerCustomRecipe(RecipeOutput consumer, Function<CraftingBookCategory, Recipe<?>> serializer, Identifier recipeId) {
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
     * @param tag          The ingredient (ore, etc.)
     * @param result       The result (ingot, gem, etc.)
     * @param experienceIn The experience (XP) the recipe yields
     */
    protected void smeltingAndBlastingRecipes(RecipeOutput consumer, String id, TagKey<Item> tag, ItemLike result, float experienceIn) {
        smeltingAndBlastingRecipes(consumer, id, Ingredient.of(this.items.getOrThrow(tag)), result, experienceIn);
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
                .save(this.output, modId("blasting/" + id));
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
     * @param category The recipe category
     * @param block    The block item (mandatory). Does not need to be a block, but is assumed to be
     *                 one.
     * @param item     The normal item (ingot, gem, etc.) Again, this can be any item.
     * @param nugget   The nugget item (optional). Can be any item or null.
     */
    protected void compressionRecipes(RecipeOutput consumer, RecipeCategory category, ItemLike block, ItemLike item, @Nullable ItemLike nugget) {
        String blockName = NameUtils.fromItem(block).getPath();
        String itemName = NameUtils.fromItem(item).getPath();

        shaped(RecipeCategory.MISC, block, 1)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', item)
                .unlockedBy("has_item", has(item))
                .save(consumer, modId(itemName + "_from_block"));
        shapeless(RecipeCategory.MISC, item, 9)
                .requires(block)
                .unlockedBy("has_item", has(item))
                .save(consumer, modId(blockName));

        if (nugget != null) {
            String nuggetName = NameUtils.fromItem(nugget).getPath();

            shaped(RecipeCategory.MISC, item, 1)
                    .pattern("###")
                    .pattern("###")
                    .pattern("###")
                    .define('#', nugget)
                    .unlockedBy("has_item", has(item))
                    .save(consumer, modId(itemName + "_from_nugget"));
            shapeless(RecipeCategory.MISC, nugget, 9)
                    .requires(item)
                    .unlockedBy("has_item", has(item))
                    .save(consumer, modId(nuggetName));
        }
    }
}
