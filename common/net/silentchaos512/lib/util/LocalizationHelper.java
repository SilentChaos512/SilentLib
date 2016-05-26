package net.silentchaos512.lib.util;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.translation.LanguageMap;

public class LocalizationHelper {

  public final String modId;
  private boolean replacesAmpersandWithSectionSign = true;

  /**
   * Constructor. The mod ID is converted to lower case.
   * 
   * @param modId
   */
  public LocalizationHelper(String modId) {

    this.modId = modId.toLowerCase();
  }

  /**
   * Sets whether or not to replace ampersands with section signs. This allows formatting codes to easily be used in
   * localization files.
   */
  public LocalizationHelper setReplaceAmpersand(boolean value) {

    replacesAmpersandWithSectionSign = value;
    return this;
  }

  // ===============
  // General methods
  // ===============

  public String getLocalizedString(String key) {

    String str = I18n.format(key).trim();
    if (replacesAmpersandWithSectionSign) {
      str = str.replaceAll("&", "\u00a7");
    }
    return str;
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

  public String getBlockSubText(String blockName, String key) {

    return getLocalizedString("tile", blockName +"." + key);
  }

  public String getItemSubText(String itemName, String key) {

    return getLocalizedString("item", itemName + "." + key);
  }

  // =================
  // Description lines
  // =================

  public List<String> getBlockDescriptionLines(String blockName) {

    return getDescriptionLines("tile." + modId + ":" + blockName + ".desc");
  }

  public List<String> getItemDescriptionLines(String itemName) {

    return getDescriptionLines("item." + modId + ":" + itemName + ".desc");
  }

  public List<String> getDescriptionLines(String key) {

    List<String> list = Lists.newArrayList();
    int i = 1;
    String line = getLocalizedString(key + i);
    while (!line.equals(key + i)) {
      list.add(line);
      line = getLocalizedString(key + ++i);
    }

    if (list.isEmpty()) {
      line = getLocalizedString(key);
      if (!line.equals(key)) {
        list.add(line);
      }
    }

    return list;
  }
}
