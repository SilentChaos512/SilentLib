package net.silentchaos512.lib.util;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.util.StatCollector;

public class LocalizationHelper {

  public final String modId;

  public LocalizationHelper(String modId) {

    this.modId = modId.toLowerCase();
  }

  // ===============
  // General methods
  // ===============

  public String getLocalizedString(String key) {

    return StatCollector.translateToLocal(key).trim();
  }

  public String getLocalizedString(String prefix, String key) {

    return getLocalizedString(prefix + "." + modId + ":" + key);
  }

  // ===============
  // Special methods
  // ===============

  public String getMiscText(String key) {

    return getLocalizedString("misc", key);
  }

  public String getWikiText(String key) {

    return getLocalizedString("wiki", key);
  }

  // =================
  // Description lines
  // =================

  public List<String> getBlockDescriptionLines(String blockName) {

    return getDescriptionLines("block." + modId + ":" + blockName + ".desc");
  }

  public List<String> getItemDescriptionLines(String itemName) {

    return getDescriptionLines("item." + modId + ":" + itemName + ".desc");
  }

  public List<String> getDescriptionLines(String key) {

    List<String> list = Lists.newArrayList();
    String line = getLocalizedString(key + 0);
    for (int i = 0; !line.equals(key); ++i) {
      list.add(line);
      line = getLocalizedString(key + i);
    }
    return list;
  }
}
