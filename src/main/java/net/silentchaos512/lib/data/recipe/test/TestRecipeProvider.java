package net.silentchaos512.lib.data.recipe.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.crafting.ingredient.ExclusionIngredient;
import net.silentchaos512.lib.data.recipe.ExtendedShapedRecipeBuilder;
import net.silentchaos512.lib.data.recipe.ExtendedSingleItemRecipeBuilder;
import net.silentchaos512.lib.data.recipe.LibRecipeProvider;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public class TestRecipeProvider extends LibRecipeProvider {
    public TestRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn, SilentLib.MOD_ID);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        SilentLib.LOGGER.fatal("Running test recipe provider! These files should NOT be included in release!");

        damageItemBuilder(RecipeCategory.MISC, Items.DIAMOND, 9)
                .damageToItems(3)
                .requires(Items.DIAMOND_PICKAXE)
                .requires(Blocks.DIAMOND_BLOCK)
                .unlockedBy("has_item", has(Blocks.DIAMOND_BLOCK))
                .save(consumer, SilentLib.getId("damage_item_test1"));

        damageItemBuilder(RecipeCategory.MISC, Items.EMERALD, 9)
                .damageToItems(3)
                .addExtraData(json -> json.addProperty("test", "This is a test!"))
                .requires(Items.DIAMOND_PICKAXE)
                .requires(Blocks.EMERALD_BLOCK)
                .save(consumer, SilentLib.getId("damage_item_test2"));

        shapelessBuilder(RecipeCategory.MISC, Blocks.DIRT, 10)
                .requires(Tags.Items.GEMS_EMERALD)
                .addExtraData(json -> json.addProperty("test2", "Can you hear me now?"))
                .save(consumer, SilentLib.getId("extended_shapeless_test1"));

        shapedBuilder(RecipeCategory.MISC, Items.DIAMOND_SWORD)
                .pattern("  #")
                .pattern(" # ")
                .pattern("/  ")
                .define('#', Tags.Items.GEMS_DIAMOND)
                .define('/', Tags.Items.RODS_WOODEN)
                .addExtraData(json -> addLore(json, "Diagonal sword!", "<3 data generators"))
                .save(consumer, SilentLib.getId("extended_shaped_test1"));

        compressionRecipes(consumer, Items.EMERALD, Items.MAGMA_CREAM, Items.APPLE);

        smeltingAndBlastingRecipes(consumer, "reverse_glass_test", Tags.Items.GLASS_COLORLESS, Items.SAND, 0.625f);

        ExtendedSingleItemRecipeBuilder.stonecuttingBuilder(RecipeCategory.MISC, Ingredient.of(Items.COAL_BLOCK), Items.COAL, 9)
                .addExtraData(json -> json.addProperty("extra_test", "testing extra data!"))
                .build(consumer);

        ExtendedShapedRecipeBuilder.vanillaBuilder(RecipeCategory.MISC, Items.APPLE, 3)
                .define('#', ExclusionIngredient.of(ItemTags.PLANKS, Items.OAK_PLANKS))
                .pattern("###")
                .pattern("# #")
                .save(consumer);
    }

    private void addLore(JsonObject json, String... lore) {
        JsonObject result = GsonHelper.getAsJsonObject(json, "result");
        JsonObject display = new JsonObject();
        JsonObject nbt = new JsonObject();
        JsonArray array = new JsonArray();
        for (String line : lore) {
            array.add("\"" + line + "\"");
        }
        display.add("Lore", array);
        nbt.add("display", display);
        result.add("nbt", nbt);
    }
}
