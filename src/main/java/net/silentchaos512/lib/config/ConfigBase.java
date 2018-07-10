/*
 * SilentLib - ConfigBase
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

package net.silentchaos512.lib.config;

import com.google.common.primitives.UnsignedInts;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.silentchaos512.lib.registry.IPhasedInitializer;
import net.silentchaos512.lib.registry.SRegistry;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public abstract class ConfigBase implements IPhasedInitializer {

    public static final String SEP = Configuration.CATEGORY_SPLITTER;
    public static final String CAT_MAIN = "main";

    protected Configuration config;
    protected final String modId;

    public ConfigBase(String modId) {
        this.modId = modId;
        MinecraftForge.EVENT_BUS.register(this);
    }

    public abstract void load();

    public int loadInt(String key, String category, int defaultValue, String comment) {
        Property prop = config.get(category, key, defaultValue);
        prop.setComment(comment);
        return prop.getInt(defaultValue);
    }

    public int loadInt(String key, String category, int defaultValue, int min, int max, String comment) {
        Property prop = config.get(category, key, defaultValue);
        prop.setComment(comment + " [range: " + min + " ~ " + max + ", default: " + defaultValue + "]");
        prop.setMinValue(min);
        prop.setMaxValue(max);
        int val = prop.getInt(defaultValue);
        return val < min ? min : val > max ? max : val;
    }

    public double loadDouble(String key, String category, double defaultValue, String comment) {
        Property prop = config.get(category, key, Double.toString(defaultValue));
        prop.setComment(comment);
        double val = 0.0;
        try {
            val = Double.parseDouble(prop.getString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return val;
    }

    public double loadDouble(String key, String category, double defaultValue, double min, double max, String comment) {
        Property prop = config.get(category, key, Double.toString(defaultValue));
        prop.setComment(comment + " [range: " + min + " ~ " + max + ", default: " + defaultValue + "]");
        prop.setMinValue(min);
        prop.setMaxValue(max);
        double val = 0.0;
        try {
            val = Double.parseDouble(prop.getString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return val < min ? min : val > max ? max : val;
    }

    public float loadFloat(String key, String category, float defaultValue, String comment) {
        return (float) loadDouble(key, category, defaultValue, comment);
    }

    public float loadFloat(String key, String category, float defaultValue, float min, float max, String comment) {
        return (float) loadDouble(key, category, defaultValue, min, max, comment);
    }

    public boolean loadBoolean(String key, String category, boolean defaultValue, String comment) {
        Property prop = config.get(category, key, defaultValue);
        prop.setComment(comment);
        return prop.getBoolean();
    }

    /**
     * Load an HTML-style color code from the config file.
     *
     * @param key          Config key
     * @param category     Config category
     * @param defaultValue Default value
     * @param includeAlpha The highest eight bits will be loaded for use as an alpha component. If
     *                     the loaded value contains zero for alpha and this is true, it will be
     *                     changed to 0xFF.
     * @param comment      Config comment
     * @return A 32-bit integer in the form 0xAARRGGBB
     * @throws NumberFormatException If the string in the config file cannot be parsed.
     * @since 2.2.19
     */
    public int loadColorCode(String key, String category, int defaultValue, boolean includeAlpha, String comment)
            throws NumberFormatException {
        String str = config.getString(key, category, String.format(includeAlpha ? "%08x" : "%06x", defaultValue), comment);
        int result = UnsignedInts.parseUnsignedInt(str, 16);
        if (includeAlpha && (result & 0xFF000000) == 0)
            result |= 0xFF000000;
        return result;
    }

    /**
     * Loads an enum from a string value in the config. Valid values will be appended to comment.
     *
     * @param key                        Config key
     * @param category                   Config category
     * @param enumClass                  The enum class, no special requirements
     * @param defaultValue               The default value, also used if the config contains an
     *                                   invalid value
     * @param comment                    Config comment
     * @param <T>                        Any enum
     * @return An enum whose {@code name} matches (ignoring case) the string loaded from the config,
     * or {@code defaultValue} if there is no match
     * @since 2.3.6
     */
    public <T extends Enum<T>> T loadEnum(String key, String category, Class<T> enumClass, T defaultValue, String comment) {
        return loadEnum(key, category, enumClass, defaultValue, comment, true);
    }

    /**
     * Loads an enum from a string value in the config.
     *
     * @param key                        Config key
     * @param category                   Config category
     * @param enumClass                  The enum class, no special requirements
     * @param defaultValue               The default value, also used if the config contains an
     *                                   invalid value
     * @param comment                    Config comment
     * @param appendValidValuesToComment If true, the valid values are added to comment
     * @param <T>                        Any enum
     * @return An enum whose {@code name} matches (ignoring case) the string loaded from the config,
     * or {@code defaultValue} if there is no match
     * @since 2.3.6
     */
    public <T extends Enum<T>> T loadEnum(String key, String category, Class<T> enumClass, T defaultValue, String comment, boolean appendValidValuesToComment) {
        String[] validValues = new String[enumClass.getEnumConstants().length];
        for (T t : enumClass.getEnumConstants())
            validValues[t.ordinal()] = t.name();

        if (appendValidValuesToComment)
            comment += "\nValid values: " + Arrays.toString(validValues);
        String value = config.getString(key, category, defaultValue.name(), comment, validValues);

        for (T t : enumClass.getEnumConstants())
            if (t.name().equalsIgnoreCase(value))
                return t;
        return defaultValue;
    }

    public void init(File file) {
        config = new Configuration(file);
        load();
    }

    public void save() {
        if (config.hasChanged())
            config.save();
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equalsIgnoreCase(modId)) {
            load();
            save();
        }
    }

    public ConfigCategory getCategory(String category) {
        return config.getCategory(category);
    }

    @Deprecated
    public List<IConfigElement> getConfigElements() {
        return new ConfigElement(getCategory(CAT_MAIN)).getChildElements();
    }

    public Configuration getConfiguration() {
        return config;
    }

    @Override
    public void preInit(SRegistry registry, FMLPreInitializationEvent event) {
        this.init(event.getSuggestedConfigurationFile());
    }

    @Override
    public void init(SRegistry registry, FMLInitializationEvent event) {
        this.save();
    }

    @Override
    public void postInit(SRegistry registry, FMLPostInitializationEvent event) {
    }
}
