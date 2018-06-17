package net.silentchaos512.lib.util;

import java.util.List;
import java.util.Locale;

import com.google.common.collect.Lists;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

/**
 * A simple wrapper for localization, with some helper methods to get some common key types (tile.modid:...,
 * item.modid:..., etc.)
 * 
 * @author SilentChaos512
 *
 */
public class LocalizationHelper {

  /** Mod ID is stored so you never need to pass it in when localizing text. */
  public final String modId;
  /** Will replace ampersands (&) with the section sign Minecraft uses for formatting codes. */
  private boolean replacesAmpersandWithSectionSign = true;
  /** If I18n prepends "Format error: " to the string, it will be removed if this is true. */
  private boolean hideFormatErrors = false;

  public LocalizationHelper(String modId) {

    this.modId = modId.toLowerCase(Locale.ROOT);
  }

  /**
   * Sets whether or not to replace ampersands with section signs. This allows formatting codes to easily be used in
   * localization files.
   */
  public LocalizationHelper setReplaceAmpersand(boolean value) {

    replacesAmpersandWithSectionSign = value;
    return this;
  }

  public LocalizationHelper setHideFormatErrors(boolean value) {

    hideFormatErrors = value;
    return this;
  }

  // ===============
  // General methods
  // ===============

  @SuppressWarnings("deprecation")
  public String getLocalizedString(String key, Object... parameters) {

    // On server, use deprecated I18n.
    if (FMLCommonHandler.instance().getSide() == Side.SERVER)
      return net.minecraft.util.text.translation.I18n.translateToLocalFormatted(key, parameters);

    // On client, use the new client-side I18n.
    String str = I18n.format(key, parameters).trim();

    if (replacesAmpersandWithSectionSign)
      str = str.replaceAll("&(?=[^\\s])", "\u00a7");
    if (hideFormatErrors)
      str = str.replaceFirst("Format error: ", "");

    return str;
  }

  public String getLocalizedString(String prefix, String key, Object... parameters) {

    return getLocalizedString(prefix + "." + modId + ":" + key, parameters);
  }

  // ===============
  // Special methods
  // ===============

  public String getMiscText(String key, Object... parameters) {

    return getLocalizedString("misc", key, parameters);
  }

  public String getWikiText(String key, Object... parameters) {

    return getLocalizedString("wiki", key, parameters);
  }

  public String getBlockSubText(String blockName, String key, Object... parameters) {

    return getLocalizedString("tile", blockName + "." + key, parameters);
  }

  public String getItemSubText(String itemName, String key, Object... parameters) {

    return getLocalizedString("item", itemName + "." + key, parameters);
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

    boolean oldHideFormatErrors = hideFormatErrors;
    hideFormatErrors = true;

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

    hideFormatErrors = oldHideFormatErrors;

    return list;
  }
}
