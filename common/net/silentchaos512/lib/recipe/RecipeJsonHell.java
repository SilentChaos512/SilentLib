package net.silentchaos512.lib.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copied from Bookshelf by Darkhax. Original class:
 * https://github.com/Darkhax-Minecraft/Bookshelf/blob/master/src/main/java/net/darkhax/bookshelf/json/RecipeGenerator.java
 * This class is flawed, as it does not support NBT or Ingredients.
 */
public final class RecipeJsonHell {

    /**
     * An instance of Gson which is used by this class specifically.
     */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Utility classes, such as this one, are not meant to be instantiated. Java adds an
     * implicit public constructor to every class which does not define at lease one
     * explicitly. Hence why this constructor was added.
     */
    private RecipeJsonHell() {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * Creates a json file for a shaped recipe. Supports normal and ore recipes.
     *
     * @param result     The resulting item.
     * @param components The components of the shaped recipe.
     */
    public static void createShapedRecipe(String name, ItemStack result, Object... components) {
        final Map<String, Object> json = new HashMap<>();
        final List<String> pattern = new ArrayList<>();
        final Map<String, Map<String, Object>> inputMap = new HashMap<>();

        boolean isOreDict = false;
        Character curKey = null;

        // The current recipe argument index
        int i = 0;

        // Adds the initial pattern args to the json map.
        while (i < components.length && components[i] instanceof String) {
            pattern.add((String) components[i]);
            i++;
        }

        // Parses the rest of the recipe arguments
        for (; i < components.length; i++) {
            final Object component = components[i];

            // Handles character params. These should always be followed by an
            // ingredient.
            if (component instanceof Character) {
                if (curKey != null) {
                    throw new IllegalArgumentException("Provided two char keys in a row");
                }
                curKey = (Character) component;
            }

            // Handles ingredients.
            else {
                if (curKey == null) {
                    throw new IllegalArgumentException("Providing object without a char key");
                }

                // If a string is used as an ingredient, it means this is an
                // oredict recipe.
                if (component instanceof String) {
                    isOreDict = true;
                }

                inputMap.put(Character.toString(curKey), serializeComponent(component));
                curKey = null;
            }
        }

        json.put("pattern", pattern);
        json.put("key", inputMap);
        json.put("type", isOreDict ? "forge:ore_shaped" : "minecraft:crafting_shaped");
        json.put("result", serializeComponent(result));

        createRecipeFile(json, name);
    }

    /**
     * Creates a json file for a shapeless recipe. Supports normal and ore recipes.
     *
     * @param result     The resulting item.
     * @param components The components of the shapeless recipe.
     */
    public static void createShapelessRecipe(String name, ItemStack result, Object... components) {
        final Map<String, Object> json = new HashMap<>();
        final List<Map<String, Object>> ingredients = new ArrayList<>();
        boolean isOreDict = false;

        // Handles the processing/serialization for all the ingredients.
        for (final Object component : components) {
            // If a string is used as an ingredient, it means this is an oredict
            // recipe.
            if (component instanceof String) {
                isOreDict = true;
            }

            ingredients.add(serializeComponent(component));
        }

        json.put("ingredients", ingredients);
        json.put("type", isOreDict ? "forge:ore_shapeless" : "minecraft:crafting_shapeless");
        json.put("result", serializeComponent(result));

        createRecipeFile(json, name);
    }

    /**
     * Serializes a component so it can be used in a json file.
     *
     * @param component The component to serialize.
     * @return A map containing the data for the componet.
     */
    private static Map<String, Object> serializeComponent(Object component) {
        // If the ingredient is an item, run it through again as an ItemStack.
        if (component instanceof Item) {
            return serializeComponent(new ItemStack((Item) component));
        }

        // If the ingredient is a block, run it through again as an ItemStack.
        if (component instanceof Block) {
            return serializeComponent(new ItemStack((Block) component));
        }

        // Handles the serialization of ItemStack. Doesn't support NBT yet.
        if (component instanceof ItemStack) {
            final ItemStack stack = (ItemStack) component;
            final Map<String, Object> ret = new HashMap<>();

            // Sets the item id.
            ret.put("item", stack.getItem().getRegistryName().toString());

            // Sets the meta value of the item
            if (stack.getItem().getHasSubtypes() || stack.getItemDamage() != 0) {
                ret.put("data", stack.getItemDamage());
            }

            // Sets the amount of items.
            if (stack.getCount() > 1) {
                ret.put("count", stack.getCount());
            }

            if (stack.hasTagCompound()) {
                throw new IllegalArgumentException("Too lazy to implement nbt support rn");
            }

            return ret;
        }

        // Handles the serialization of an Ore Dict entry.
        if (component instanceof String) {
            final Map<String, Object> ret = new HashMap<>();
            ret.put("type", "forge:ore_dict");
            ret.put("ore", component);

            return ret;
        }

        throw new IllegalArgumentException("Could not serialize the unsupported type " + component.getClass().getName());
    }

    /**
     * Creates a new json file which contains the recipe data.
     *
     * @param json     A map containing json fields.
     * @param fileName The name of the recipe file.
     */
    private static void createRecipeFile(Map<String, Object> json, String fileName) {
        fileName = fileName.replaceAll(":", "_");
        // Sets the parent directory to that of the current mod being loaded.
        final File directory = new File("recipes/" + Loader.instance().activeModContainer().getModId());

        if (!directory.exists()) {
            directory.mkdirs();
        }

        // The actual file being written.
        final File output = new File(directory, fileName + ".json");
        //System.out.println(output.getAbsolutePath());

        // Writes the json file.
        try (FileWriter writer = new FileWriter(output)) {
            GSON.toJson(json, writer);
            writer.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
