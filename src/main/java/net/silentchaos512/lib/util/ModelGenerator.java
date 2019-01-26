package net.silentchaos512.lib.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.lib.SilentLib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public final class ModelGenerator {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private ModelGenerator() { throw new IllegalAccessError("Utility class"); }

    public static void createFor(Block block) {
        ResourceLocation name = Objects.requireNonNull(block.getRegistryName());
        writeFile(createBlockstate(name), "blockstates", name);
        writeFile(createBlockModel(name), "models/block", name);
        writeFile(createBlockItemModel(name), "models/item", name);
    }

    public static void createFor(Item item) {
        ResourceLocation name = Objects.requireNonNull(item.getRegistryName());
        writeFile(createItemModel(name), "models/item", name);
    }

    private static JsonObject createBlockstate(ResourceLocation name) {
        JsonObject json = new JsonObject();
        JsonObject variants = new JsonObject();
        JsonObject model = new JsonObject();

        model.addProperty("model", name.getNamespace() + "block/" + name.getPath());
        variants.add("", model);
        json.add("variants", variants);

        return json;
    }

    private static JsonObject createBlockModel(ResourceLocation name) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "block/cube_all");

        JsonObject textures = new JsonObject();
        textures.addProperty("all", name.getNamespace() + "blocks/" + name.getPath());

        json.add("textures", textures);
        return json;
    }

    private static JsonObject createBlockItemModel(ResourceLocation name) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", name.getNamespace() + "block/" + name.getPath());
        return json;
    }

    private static JsonObject createItemModel(ResourceLocation name) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "item/generated");

        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", name.getNamespace() + "items/" + name.getPath());

        json.add("textures", textures);
        return json;
    }

    private static void writeFile(JsonObject json, String subdir, ResourceLocation name) {
        String fileName = name.getPath();
        final String dirPath = "output/" + name.getNamespace() + "/" + subdir;
        final File directory = new File(dirPath);

        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                SilentLib.LOGGER.error("Could not create directory: {}", dirPath);
                return;
            }
        }

        final File output = new File(directory, fileName + ".json");

        try (FileWriter writer = new FileWriter(output)) {
            GSON.toJson(json, writer);
            SilentLib.LOGGER.info("Wrote model file {}", output.getAbsolutePath());
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
}
