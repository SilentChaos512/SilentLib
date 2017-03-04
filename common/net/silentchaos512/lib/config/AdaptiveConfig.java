package net.silentchaos512.lib.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public abstract class AdaptiveConfig extends ConfigBase {

  public static final String CAT_LAST_VERSION = "zzlastversion";

  protected @Nonnull Adaptor adaptor;
  protected boolean adaptorEnabled;
  protected int currentBuild;

  public AdaptiveConfig(String modId, boolean adaptorEnabled, int currentBuild) {

    super(modId);
    this.adaptorEnabled = adaptorEnabled;
    this.currentBuild = currentBuild;
  }

  @Override
  public void init(File file) {

    config = new Configuration(file);
    int lastBuild = config.get(CAT_LAST_VERSION, "last_build", currentBuild).getInt(currentBuild);
    adaptor = new Adaptor(adaptorEnabled, lastBuild, currentBuild);
    load();
  }

  @Override
  public void save() {

    config.get(CAT_LAST_VERSION, "last_build", currentBuild).setValue(currentBuild);

    super.save();
  }

  @Override
  public int loadInt(String key, String category, int defaultValue, String comment) {

    Property prop = config.get(category, key, defaultValue);
    prop.setComment(comment);
    adaptor.adaptProperty(prop, prop.getInt(defaultValue));
    return prop.getInt(defaultValue);
  }

  @Override
  public int loadInt(String key, String category, int defaultValue, int min, int max, String comment) {

    Property prop = config.get(category, key, defaultValue);
    if (comment != null && !comment.isEmpty())
      prop.setComment(comment + " [range: " + min + " ~ " + max + ", default: " + defaultValue + "]");
    prop.setMinValue(min);
    prop.setMaxValue(max);
    adaptor.adaptProperty(prop, prop.getInt(defaultValue));
    int val = prop.getInt(defaultValue);
    return val < min ? min : val > max ? max : val;
  }

  public double loadDouble(String key, String category, double defaultValue, String comment) {

    Property prop = config.get(category, key, defaultValue);
    prop.setComment(comment);
    adaptor.adaptProperty(prop, prop.getDouble(defaultValue));
    return prop.getDouble();
  }

  public double loadDouble(String key, String category, double defaultValue, double min, double max, String comment) {

    Property prop = config.get(category, key, defaultValue);
    if (comment != null && !comment.isEmpty())
      prop.setComment(comment + " [range: " + min + " ~ " + max + ", default: " + defaultValue + "]");
    prop.setMinValue(min);
    prop.setMaxValue(max);
    adaptor.adaptProperty(prop, prop.getDouble(defaultValue));
    double val = prop.getDouble(defaultValue);
    return val < min ? min : val > max ? max : val;
  }

  public <T> void addAdaptorMapping(int version, String key, T val) {

    adaptor.addMapping(version, key, val);
  }

  public static class Adaptor {

    private boolean enabled;
    private int lastBuild;
    private int currentBuild;

    private final Map<String, List<AdaptableValue>> adaptableValues = Maps.newHashMap();
    private final List<String> changes = Lists.newArrayList();

    public Adaptor(boolean enabled, int lastBuild, int currentBuild) {

      this.enabled = enabled;
      this.lastBuild = lastBuild;
      this.currentBuild = currentBuild;
    }

    public <T> void adaptProperty(Property prop, T val) {

      if (!enabled)
        return;

      String name = prop.getName();

      if (!adaptableValues.containsKey(name))
        return;

      AdaptableValue<T> bestValue = null;
      for (AdaptableValue<T> value : adaptableValues.get(name)) {
        if (value.version >= lastBuild)
          continue;
        if (bestValue == null || value.version > bestValue.version)
          bestValue = value;
      }

      if (bestValue != null) {
        T expected = bestValue.value;
        T def = (T) prop.getDefault();

        if (areEqualNumbers(val, expected) && !areEqualNumbers(val, def)) {
          prop.setValue(def.toString());
          changes.add(" " + prop.getName() + ": " + val + " -> " + def);
        }
      }
    }

    public <T> void addMapping(int version, String key, T val) {

      if (!enabled)
        return;

      AdaptableValue<T> adapt = new AdaptableValue<T>(version, val);
      if (!adaptableValues.containsKey(key))
        adaptableValues.put(key, new ArrayList<AdaptableValue>());

      adaptableValues.get(key).add(adapt);
    }

    public boolean areEqualNumbers(Object v1, Object v2) {

      double epsilon = 1.0E-6;
      float v1f = ((Number) v1).floatValue();
      float v2f;

      if (v2 instanceof String)
        v2f = Float.parseFloat((String) v2);
      else
        v2f = ((Number) v2).floatValue();

      return Math.abs(v1f - v2f) < epsilon;
    }
  }

  public static class AdaptableValue<T> {

    public final int version;
    public final T value;
    public final Class<? extends T> valueType;

    public AdaptableValue(int version, T value) {

      this.version = version;
      this.value = value;
      this.valueType = (Class<? extends T>) value.getClass();
    }
  }
}
