package net.silentchaos512.lib.util;

import com.google.gson.*;
import net.minecraft.nbt.*;
import net.silentchaos512.lib.SilentLib;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class NBTToJson {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private NBTToJson() {throw new IllegalAccessError("Utility class");}

    public static JsonElement toJson(Tag nbt) {
        //noinspection ChainOfInstanceofChecks
        if (nbt instanceof CompoundTag) {
            return toJsonObject((CompoundTag) nbt);
        } else if (nbt instanceof CollectionTag) {
            return toJsonArray((CollectionTag<?>) nbt);
        } else if (nbt instanceof NumericTag) {
            return new JsonPrimitive(((NumericTag) nbt).getAsNumber());
        } else if (nbt instanceof StringTag) {
            return new JsonPrimitive(nbt.getAsString());
        }
        return JsonNull.INSTANCE;
    }

    public static JsonObject toJsonObject(CompoundTag nbt) {
        JsonObject json = new JsonObject();
        for (String key : nbt.getAllKeys()) {
            Tag element = nbt.get(key);
            if (element != null) {
                json.add(key, toJson(element));
            }
        }
        return json;
    }

    public static JsonArray toJsonArray(CollectionTag<?> nbt) {
        JsonArray json = new JsonArray();
        for (Tag element : nbt) {
            json.add(toJson(element));
        }
        return json;
    }

    public static String writeFile(JsonObject json) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        String fileName = "nbt_export_" + dtf.format(now) + ".json";
        final String dirPath = "output/silentlib_user";
        File output = new File(dirPath, fileName);
        File directory = output.getParentFile();

        if (!directory.exists() && !directory.mkdirs()) {
            SilentLib.LOGGER.error("Could not create directory: {}", output.getParent());
            return "Could not create output directory";
        }

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(output), StandardCharsets.UTF_8)) {
            GSON.toJson(json, writer);
            String absolutePath = output.getAbsolutePath();
            SilentLib.LOGGER.info("Wrote model file {}", absolutePath);
            return "Wrote to " + absolutePath;
        } catch (final IOException ex) {
            ex.printStackTrace();
            return ex.toString();
        }
    }
}
