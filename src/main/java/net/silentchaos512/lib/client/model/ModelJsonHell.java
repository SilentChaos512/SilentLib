/*
 * SilentLib - ModelJsonHell
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.lib.client.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.fml.common.Loader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public final class ModelJsonHell {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private ModelJsonHell() {
        throw new IllegalAccessError("Utility class");
    }

    public static void createItemModel(String fileName, String... textures) {
        final Map<String, Object> json = new HashMap<>();
        Map<String, Object> texList = new LinkedHashMap<>();

        for (int i = 0; i < textures.length; ++i) {
            texList.put("layer" + i, textures[i]);
        }

        json.put("parent", "item/generated");
        json.put("textures", texList);

        createModelFile(json, "item", fileName);
    }

    private static void createModelFile(Map<String, Object> json, String subFolder, String fileName) {
        fileName = fileName.replaceAll(":", "_");
        final File directory = new File("models/" + subFolder + "/" + Loader.instance().activeModContainer().getModId());

        if (!directory.exists()) {
            directory.mkdirs();
        }

        final File output = new File(directory, fileName + ".json");

        try (FileWriter writer = new FileWriter(output)) {
            GSON.toJson(json, writer);
            writer.close();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
}
