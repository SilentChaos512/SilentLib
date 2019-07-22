package net.silentchaos512.lib.util.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.block.IBlockProvider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Deprecated
public final class TagGenerator {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<ResourceLocation, Collection<String>> BLOCKS = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, Collection<String>> ITEMS = new ConcurrentHashMap<>();

    private TagGenerator() { throw new IllegalAccessError("Utility class"); }

    public static Tag<Block> block(ResourceLocation tag, IBlockProvider block) {
        return block(tag, block.asBlock());
    }

    public static Tag<Block> block(ResourceLocation tag, IBlockProvider first, IBlockProvider... rest) {
        return block(tag, first.asBlock(),
                Arrays.stream(rest)
                        .map(IBlockProvider::asBlock)
                        .toArray(Block[]::new));
    }

    @SuppressWarnings("TypeMayBeWeakened")
    public static Tag<Block> block(ResourceLocation tag, Block block) {
        return block(tag, Objects.requireNonNull(block.getRegistryName()).toString());
    }

    @SuppressWarnings("TypeMayBeWeakened")
    public static Tag<Block> block(ResourceLocation tag, Block first, Block... rest) {
        return block(tag, Objects.requireNonNull(first.getRegistryName()).toString(),
                Arrays.stream(rest)
                        .map(block -> Objects.requireNonNull(block.getRegistryName()).toString())
                        .toArray(String[]::new));
    }

    public static Tag<Block> block(ResourceLocation tag, Tag<?> value) {
        return block(tag, "#" + value.getId());
    }

    public static Tag<Block> block(ResourceLocation tag, String value) {
        Collection<String> blocks = getBlocks(tag);
        blocks.add(value);
        return new BlockTags.Wrapper(tag);
    }

    public static Tag<Block> block(ResourceLocation tag, String first, String... rest) {
        Collection<String> blocks = getBlocks(tag);
        blocks.add(first);
        blocks.addAll(Arrays.asList(rest));
        return new BlockTags.Wrapper(tag);
    }

    public static Tag<Item> item(ResourceLocation tag, IItemProvider item) {
        return item(tag, item.asItem());
    }

    public static Tag<Item> item(ResourceLocation tag, IItemProvider first, IItemProvider... rest) {
        return item(tag, first.asItem(),
                Arrays.stream(rest)
                        .map(IItemProvider::asItem)
                        .toArray(Item[]::new));
    }

    @SuppressWarnings("TypeMayBeWeakened")
    public static Tag<Item> item(ResourceLocation tag, Item item) {
        return item(tag, Objects.requireNonNull(item.getRegistryName()).toString());
    }

    @SuppressWarnings("TypeMayBeWeakened")
    public static Tag<Item> item(ResourceLocation tag, Item first, Item... rest) {
        return item(tag, Objects.requireNonNull(first.getRegistryName()).toString(),
                Arrays.stream(rest)
                        .map(i -> Objects.requireNonNull(i.getRegistryName()).toString())
                        .toArray(String[]::new));
    }

    public static Tag<Item> item(ResourceLocation tag, Tag<?> value) {
        return item(tag, "#" + value.getId());
    }

    public static Tag<Item> item(ResourceLocation tag, String value) {
        Collection<String> items = getItems(tag);
        items.add(value);
        return new ItemTags.Wrapper(tag);
    }

    public static Tag<Item> item(ResourceLocation tag, String first, String... rest) {
        Collection<String> items = getItems(tag);
        items.add(first);
        items.addAll(Arrays.asList(rest));
        return new ItemTags.Wrapper(tag);
    }

    private static Collection<String> getBlocks(ResourceLocation tag) {
        if (!BLOCKS.containsKey(tag))
            BLOCKS.put(tag, new HashSet<>());
        return BLOCKS.get(tag);
    }

    private static Collection<String> getItems(ResourceLocation tag) {
        if (!ITEMS.containsKey(tag))
            ITEMS.put(tag, new HashSet<>());
        return ITEMS.get(tag);
    }

    /**
     * DO NOT CALL. Silent Lib will call this during InterModProcessEvent event. Add tags you want
     * to generate before then.
     *
     * @deprecated Internal use only! Deprecated as a warning.
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public static void generateFiles() {
        // FIXME: This will block all usages, because isDevBuild is kinda dumb right now
        if (SilentLib.isDevBuild()) {
            BLOCKS.forEach((tag, blocks) -> writeFile(tag, "blocks", blocks));
            ITEMS.forEach((tag, items) -> writeFile(tag, "items", items));
        }
        // Don't need these anymore, allow it to be garbage collected
        BLOCKS.clear();
        ITEMS.clear();
    }

    private static JsonObject getJson(Iterable<String> values) {
        JsonObject json = new JsonObject();
        json.addProperty("replace", false);

        JsonArray array = new JsonArray();
        values.forEach(array::add);
        json.add("values", array);

        return json;
    }

    private static void writeFile(ResourceLocation tag, String subDir, Iterable<String> things) {
        String str = "output/data/" + tag.getNamespace() + "/tags/" + subDir + "/" + tag.getPath() + ".json";
        final File output = new File(str);
        final File directory = new File(output.getParent());

        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                SilentLib.LOGGER.error("Could not create directory: {}", directory.getAbsolutePath());
                return;
            }
        }

        try (FileWriter writer = new FileWriter(output)) {
            JsonObject json = getJson(things);
            GSON.toJson(json, writer);
            SilentLib.LOGGER.info("Wrote tag file {}", output.getAbsolutePath());
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
}
