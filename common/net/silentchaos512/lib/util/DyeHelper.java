package net.silentchaos512.lib.util;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;

public class DyeHelper {

  public static String getOreName(EnumDyeColor dye) {

    String name = dye.getUnlocalizedName();
    return "dye" + (dye == EnumDyeColor.SILVER ? "LightGray"
        : Character.toUpperCase(name.charAt(0)) + name.substring(1));
  }

  public static int getColor(EnumDyeColor dye) {

    return ItemDye.DYE_COLORS[dye.getDyeDamage()];
  }
}
