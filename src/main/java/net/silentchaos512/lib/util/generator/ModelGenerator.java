package net.silentchaos512.lib.util.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.lib.SilentLib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public final class ModelGenerator {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private ModelGenerator() { throw new IllegalAccessError("Utility class"); }

    public static void create(Block block) {
        create(BlockBuilder.create(block));
    }

    public static void create(BlockBuilder builder) {
        ResourceLocation name = builder.blockName;
        writeFile(builder.buildBlockState(), "blockstates", name);
        writeFile(builder.buildBlockModel(), "models/block", name);
        writeFile(builder.buildItemModel(), "models/item", name);
    }

    public static void create(Item item) {
        create(ItemBuilder.create(item));
    }

    public static void create(ItemBuilder builder) {
        ResourceLocation name = builder.itemName;
        writeFile(builder.build(), "models/item", name);
    }

    private static void writeFile(JsonObject json, String subdir, ResourceLocation name) {
        String fileName = name.getPath();
        final String dirPath = "output/assets/" + name.getNamespace() + "/" + subdir;
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

    public static final class BlockBuilder {
        private final Block block;
        private final ResourceLocation blockName;
        private String comment = "";

        // BlockStates
        private Map<String, String> variants;

        // Block Model
        private String parentModel = "block/cube_all";
        private final Map<String, ResourceLocation> blockTextures = new LinkedHashMap<>();

        // Item Model
        private ItemBuilder itemBuilder;
        private ResourceLocation defaultItemModel;
        private ResourceLocation defaultItemTexture;

        private BlockBuilder(Block block) {
            this.block = block;
            this.blockName = this.block.getRegistryName();
        }

        public static BlockBuilder create(Block block) {
            return new BlockBuilder(block);
        }

        public BlockBuilder variant(String key, String model) {
            return variant(key, new ResourceLocation(blockName.getNamespace(), "block/" + model));
        }

        public BlockBuilder variant(String key, ResourceLocation modelPath) {
            if (variants == null) variants = new LinkedHashMap<>();
            variants.put(key, modelPath.toString());
            if (defaultItemModel == null) defaultItemModel = modelPath;
            return this;
        }

        public BlockBuilder parent(String parentModel) {
            this.parentModel = parentModel;
            return this;
        }

        public BlockBuilder texture(String texture) {
            return texture("all", texture);
        }

        public BlockBuilder texture(ResourceLocation texturePath) {
            return texture("all", texturePath);
        }

        public BlockBuilder texture(String key, String texture) {
            return texture(key, new ResourceLocation(blockName.getNamespace(), "block/" + texture));
        }

        public BlockBuilder texture(String key, ResourceLocation texturePath) {
            this.blockTextures.put(key, texturePath);
            if (defaultItemTexture == null) defaultItemTexture = texturePath;
            return this;
        }

        public BlockBuilder item(ItemBuilder builder) {
            this.itemBuilder = builder;
            return this;
        }

        public BlockBuilder comment(String comment) {
            this.comment = comment;
            return this;
        }

        private String getDefaultModel() {
            return blockName.getNamespace() + ":block/" + blockName.getPath();
        }

        private String getDefaultTexture() {
            return blockName.getNamespace() + ":block/" + blockName.getPath();
        }

        private ItemBuilder getDefaultItemBuilder() {
            return ItemBuilder.create(block.asItem())
                    .parent(defaultItemModel == null ? getDefaultModel() : defaultItemModel.toString());
        }

        JsonObject buildBlockState() {
            JsonObject json = new JsonObject();
            if (!comment.isEmpty()) json.addProperty("__comment__", comment);

            JsonObject variantsJson = new JsonObject();

            if (this.variants == null || this.variants.isEmpty()) {
                JsonObject model = new JsonObject();
                model.addProperty("model", getDefaultModel());
                variantsJson.add("", model);
            } else {
                this.variants.forEach((key, val) -> {
                    JsonObject model = new JsonObject();
                    model.addProperty("model", val);
                    variantsJson.add(key, model);
                });
            }

            json.add("variants", variantsJson);

            return json;
        }

        // TODO: This is flawed, as it will not work if multiple models are needed
        JsonObject buildBlockModel() {
            JsonObject json = new JsonObject();
            json.addProperty("parent", this.parentModel);

            JsonObject textures = new JsonObject();
            if (this.blockTextures.isEmpty()) {
                textures.addProperty("all", getDefaultTexture());
            } else {
                this.blockTextures.forEach((key, val) -> textures.addProperty(key, val.toString()));
            }
            json.add("textures", textures);

            return json;
        }

        JsonObject buildItemModel() {
            return itemBuilder != null
                    ? itemBuilder.build()
                    : getDefaultItemBuilder().build();
        }
    }

    public static final class ItemBuilder {
        private final Item item;
        private final ResourceLocation itemName;
        private String parentModel = "item/generated";
        private final List<ResourceLocation> itemTextures = new ArrayList<>();
        private String comment = "";

        private ItemBuilder(Item item) {
            this.item = item;
            this.itemName = Objects.requireNonNull(this.item.getRegistryName());
        }

        public static ItemBuilder create(Item item) {
            return new ItemBuilder(item);
        }

        public ItemBuilder parent(String parentModel) {
            this.parentModel = parentModel;
            return this;
        }

        public ItemBuilder texture(String texture) {
            return texture(new ResourceLocation(itemName.getNamespace(), "item/" + texture));
        }

        public ItemBuilder texture(ResourceLocation texturePath) {
            this.itemTextures.add(texturePath);
            return this;
        }

        public ItemBuilder comment(String comment) {
            this.comment = comment;
            return this;
        }

        private String getDefaultTexture() {
            return itemName.getNamespace() + ":item/" + itemName.getPath();
        }

        JsonObject build() {
            JsonObject json = new JsonObject();
            if (!comment.isEmpty()) json.addProperty("__comment__", comment);

            json.addProperty("parent", this.parentModel);

            if (this.itemTextures.isEmpty() && !(item instanceof ItemBlock)) {
                // No specified texture, not a block -> use default
                JsonObject textures = new JsonObject();
                textures.addProperty("layer0", getDefaultTexture());
                json.add("textures", textures);
            } else if (!this.itemTextures.isEmpty()) {
                // Textures specified, may be block or item, doesn't matter
                JsonObject textures = new JsonObject();
                for (int i = 0; i < this.itemTextures.size(); ++i) {
                    textures.addProperty("layer" + i, this.itemTextures.get(i).toString());
                }
                json.add("textures", textures);
            }

            return json;
        }
    }
}
