package net.silentchaos512.lib.config;

import java.io.File;
import java.util.List;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public abstract class ConfigBase {

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

  public List<IConfigElement> getConfigElements() {

    return new ConfigElement(getCategory(CAT_MAIN)).getChildElements();
  }

  public Configuration getConfiguration() {

    return config;
  }
}
