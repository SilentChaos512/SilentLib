package net.silentchaos512.lib.util.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.lib.SilentLib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public final class RecipeGenerator {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<Class<?>, Function<Object, JsonObject>> COMPONENT_SERIALIZERS = new HashMap<>();

    static {
        // Ingredient serializers
        // ItemStack and IItemProvider are serialized as items
        // String, ResourceLocation, and Tag<?> serialize as tags
        COMPONENT_SERIALIZERS.put(ItemStack.class, RecipeGenerator::serializeItemStack);
        COMPONENT_SERIALIZERS.put(IItemProvider.class, o -> serialize(new ItemStack((IItemProvider) o)));
        COMPONENT_SERIALIZERS.put(String.class, RecipeGenerator::serializeTag);
        COMPONENT_SERIALIZERS.put(ResourceLocation.class, o -> serializeTag(o.toString()));
        COMPONENT_SERIALIZERS.put(Tag.class, o -> serializeTag(((Tag) o).getId()));
    }

    private RecipeGenerator() { throw new IllegalAccessError("Utility class"); }

    /**
     * Generate a recipe file using the given {@link IRecipeBuilder}. Built-in builders include
     * {@link ShapedBuilder}, {@link ShapelessBuilder}, and {@link SmeltingBuilder}.
     *
     * @param name    Unique ID for the recipe. The path becomes the file name.
     * @param builder The recipe builder, a thing that generates a {@link JsonObject}
     */
    public static void create(ResourceLocation name, IRecipeBuilder builder) {
        writeFile(name, builder.build());
    }

    private static void writeFile(ResourceLocation name, JsonObject json) {
        String fileName = name.getPath();
        String dirPath = "output/" + name.getNamespace() + "/" + name.getNamespace();
        File directory = new File(dirPath);

        if (!directory.exists() && !directory.mkdirs()) {
            SilentLib.LOGGER.error("Could not create directory: {}", dirPath);
            return;
        }

        File output = new File(directory, fileName + ".json");

        try (FileWriter writer = new FileWriter(output)) {
            GSON.toJson(json, writer);
            SilentLib.LOGGER.info("Wrote recipe file {}", output.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //region Compression shortcuts

    /**
     * Creates two recipe files, one where four of the small item become one of the big, and the
     * reverse. Group is omitted.
     *
     * @param name  Unique ID for the recipe. The path becomes the file name.
     * @param big   The larger item (storage blocks, etc.)
     * @param small The smaller item (nuggets, ingots, gems, etc.)
     */
    public static void compress4(ResourceLocation name, IItemProvider big, IItemProvider small) {
        compress4(name, "", big, small);
    }

    /**
     * Creates two recipe files, one where four of the small item become one of the big, and the
     * reverse.
     *
     * @param name  Unique ID for the recipe. The path becomes the file name.
     * @param group A group for the recipes.
     * @param big   The larger item (storage blocks, etc.)
     * @param small The smaller item (nuggets, ingots, gems, etc.)
     */
    public static void compress4(ResourceLocation name, ResourceLocation group, IItemProvider big, IItemProvider small) {
        compress4(name, group.toString(), big, small);
    }

    /**
     * Creates two recipe files, one where four of the small item become one of the big, and the
     * reverse.
     *
     * @param name  Unique ID for the recipe. The path becomes the file name.
     * @param group A group for the recipes.
     * @param big   The larger item (storage blocks, etc.)
     * @param small The smaller item (nuggets, ingots, gems, etc.)
     */
    public static void compress4(ResourceLocation name, String group, IItemProvider big, IItemProvider small) {
        create(new ResourceLocation(name.getNamespace(), name.getPath() + "_compress"),
                RecipeGenerator.ShapedBuilder
                        .create(big)
                        .group(!group.isEmpty() ? group + "_compress" : "")
                        .layout("##", "##")
                        .key('#', small));
        create(new ResourceLocation(name.getNamespace(), name.getPath() + "_uncompress"),
                RecipeGenerator.ShapedBuilder
                        .create(new ItemStack(small, 4))
                        .group(!group.isEmpty() ? group + "_uncompress" : "")
                        .layout("#")
                        .key('#', big));
    }

    /**
     * Creates two recipe files, one where nine of the small item become one of the big, and the
     * reverse. Group is omitted.
     *
     * @param name  Unique ID for the recipe. The path becomes the file name.
     * @param big   The larger item (storage blocks, etc.)
     * @param small The smaller item (nuggets, ingots, gems, etc.)
     */
    public static void compress9(ResourceLocation name, IItemProvider big, IItemProvider small) {
        compress9(name, "", big, small);
    }

    /**
     * Creates two recipe files, one where nine of the small item become one of the big, and the
     * reverse.
     *
     * @param name  Unique ID for the recipe. The path becomes the file name.
     * @param group A group for the recipes.
     * @param big   The larger item (storage blocks, etc.)
     * @param small The smaller item (nuggets, ingots, gems, etc.)
     */
    public static void compress9(ResourceLocation name, ResourceLocation group, IItemProvider big, IItemProvider small) {
        compress9(name, group.toString(), big, small);
    }

    /**
     * Creates two recipe files, one where nine of the small item become one of the big, and the
     * reverse.
     *
     * @param name  Unique ID for the recipe. The path becomes the file name.
     * @param group A group for the recipes.
     * @param big   The larger item (storage blocks, etc.)
     * @param small The smaller item (nuggets, ingots, gems, etc.)
     */
    public static void compress9(ResourceLocation name, String group, IItemProvider big, IItemProvider small) {
        create(new ResourceLocation(name.getNamespace(), name.getPath() + "_compress"),
                RecipeGenerator.ShapedBuilder
                        .create(big)
                        .group(!group.isEmpty() ? group + "_compress" : "")
                        .layout("###", "###", "###")
                        .key('#', small));
        create(new ResourceLocation(name.getNamespace(), name.getPath() + "_uncompress"),
                RecipeGenerator.ShapedBuilder
                        .create(new ItemStack(small, 9))
                        .group(!group.isEmpty() ? group + "_uncompress" : "")
                        .layout("#")
                        .key('#', big));
    }

    //endregion

    //region Ingredient serializers

    private static JsonObject serialize(Object obj) {
        Function<Object, JsonObject> function = null;
        for (Class<?> clazz : COMPONENT_SERIALIZERS.keySet()) {
            if (clazz.isInstance(obj)) {
                function = COMPONENT_SERIALIZERS.get(clazz);
                break;
            }
        }

        if (function == null) {
            throw new IllegalArgumentException("Don't know how to serialize object of type " + obj.getClass());
        }
        return function.apply(obj);
    }

    private static JsonObject serializeItemStack(Object o) {
        final ItemStack stack = (ItemStack) o;
        final JsonObject json = new JsonObject();
        json.addProperty("item", Objects.requireNonNull(stack.getItem().getRegistryName()).toString());

        if (stack.getCount() > 1) {
            json.addProperty("count", stack.getCount());
        }

        if (stack.hasTag()) {
            SilentLib.LOGGER.warn("Recipe component contains NBT and cannot be serialized properly: {}", o);
        }

        return json;
    }

    private static JsonObject serializeTag(Object o) {
        String tag = o.toString();
        JsonObject json = new JsonObject();
        json.addProperty("tag", tag);
        return json;
    }

    //endregion

    //region Built-in IRecipeBuilders

    public static final class ShapedBuilder implements IRecipeBuilder {
        private final ItemStack result;
        private String group = "";
        private final Collection<String> layout = new ArrayList<>();
        private final Map<String, Object> ingredients = new HashMap<>();

        public static ShapedBuilder create(ItemStack result) {
            return new ShapedBuilder(result);
        }

        public static ShapedBuilder create(IItemProvider result) {
            return new ShapedBuilder(new ItemStack(result));
        }

        public static ShapedBuilder create(IItemProvider result, int count) {
            return new ShapedBuilder(new ItemStack(result, count));
        }

        private ShapedBuilder(ItemStack result) {
            this.result = result;
        }

        public ShapedBuilder group(ResourceLocation group) {
            return group(group.toString());
        }

        public ShapedBuilder group(String group) {
            this.group = group;
            return this;
        }

        public ShapedBuilder layout(String line1) {
            checkLayoutEmpty();
            layout.add(line1);
            return this;
        }

        public ShapedBuilder layout(String line1, String line2) {
            checkLayoutEmpty();
            layout.add(line1);
            layout.add(line2);
            return this;
        }

        public ShapedBuilder layout(String line1, String line2, String line3) {
            checkLayoutEmpty();
            layout.add(line1);
            layout.add(line2);
            layout.add(line3);
            return this;
        }

        private void checkLayoutEmpty() {
            if (!layout.isEmpty()) {
                throw new IllegalStateException("layout called more than once");
            }
        }

        public ShapedBuilder key(char key, Object obj) {
            String strKey = String.valueOf(key);
            if (ingredients.containsKey(strKey)) {
                throw new IllegalStateException("key already contains '" + key + "'");
            }
            ingredients.put(strKey, obj);
            return this;
        }

        @Override
        public JsonObject build() {
            JsonObject json = new JsonObject();
            json.addProperty("type", "crafting_shaped");
            if (!group.isEmpty()) {
                json.addProperty("group", group);
            }

            JsonArray pattern = new JsonArray();
            layout.forEach(pattern::add);
            json.add("pattern", pattern);

            JsonObject key = new JsonObject();
            ingredients.forEach((s, o) -> key.add(s, serialize(o)));
            json.add("key", key);

            JsonObject resultObj = serialize(result);
            json.add("result", resultObj);

            return json;
        }
    }

    public static final class ShapelessBuilder implements IRecipeBuilder {
        private final ItemStack result;
        private String group = "";
        private final Collection<Object> ingredients = new ArrayList<>();

        public static ShapelessBuilder create(ItemStack result) {
            return new ShapelessBuilder(result);
        }

        public static ShapelessBuilder create(IItemProvider result) {
            return new ShapelessBuilder(new ItemStack(result));
        }

        public static ShapelessBuilder create(IItemProvider result, int count) {
            return new ShapelessBuilder(new ItemStack(result, count));
        }

        private ShapelessBuilder(ItemStack result) {
            this.result = result;
        }

        public ShapelessBuilder group(ResourceLocation group) {
            return group(group.toString());
        }

        public ShapelessBuilder group(String group) {
            this.group = group;
            return this;
        }

        public ShapelessBuilder ingredient(Object ingredient) {
            ingredients.add(ingredient);
            return this;
        }

        public ShapelessBuilder ingredient(Object ingredient, int count) {
            for (int i = 0; i < count; ++i) {
                ingredients.add(ingredient);
            }
            return this;
        }

        @Override
        public JsonObject build() {
            JsonObject json = new JsonObject();
            json.addProperty("type", "crafting_shapeless");
            if (!group.isEmpty()) {
                json.addProperty("group", group);
            }

            JsonArray ingredientsObj = new JsonArray();
            ingredients.forEach(o -> ingredientsObj.add(serialize(o)));
            json.add("ingredients", ingredientsObj);

            JsonObject resultObj = serialize(result);
            json.add("result", resultObj);

            return json;
        }
    }

    public static final class SmeltingBuilder implements IRecipeBuilder {
        private final ItemStack result;
        private String group = "";
        private Object ingredient;
        private float experience = 0f;
        private int cookingTime = 200;

        public static SmeltingBuilder create(ItemStack result) {
            return new SmeltingBuilder(result);
        }

        public static SmeltingBuilder create(IItemProvider result) {
            return new SmeltingBuilder(new ItemStack(result));
        }

        public static SmeltingBuilder create(IItemProvider result, int count) {
            return new SmeltingBuilder(new ItemStack(result, count));
        }

        private SmeltingBuilder(ItemStack result) {
            this.result = result;
        }

        public SmeltingBuilder group(ResourceLocation group) {
            return group(group.toString());
        }

        public SmeltingBuilder group(String group) {
            this.group = group;
            return this;
        }

        public SmeltingBuilder ingredient(Object ingredient) {
            this.ingredient = ingredient;
            return this;
        }

        public SmeltingBuilder experience(float experience) {
            this.experience = experience;
            return this;
        }

        public SmeltingBuilder cookingTime(int cookingTime) {
            this.cookingTime = cookingTime;
            return this;
        }

        @Override
        public JsonObject build() {
            JsonObject json = new JsonObject();
            json.addProperty("type", "smelting");
            if (!group.isEmpty()) {
                json.addProperty("group", group);
            }

            JsonObject ingredientObj = serialize(ingredient);
            json.add("ingredient", ingredientObj);

            ResourceLocation resultName = Objects.requireNonNull(result.getItem().getRegistryName());
            json.addProperty("result", resultName.toString());

            json.addProperty("experience", experience);
            json.addProperty("cookingtime", cookingTime);

            return json;
        }
    }

    //endregion
}
