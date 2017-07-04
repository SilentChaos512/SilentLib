package net.silentchaos512.lib.util;

import net.minecraft.enchantment.Enchantment;

public class EnchantmentUtils {

  public static boolean canApplyTogether(Enchantment e1, Enchantment e2) {

    return e1.canApplyTogether(e2) && e2.canApplyTogether(e1);
  }
}
