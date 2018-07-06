/*
 * SilentLib - DyeHelper
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

package net.silentchaos512.lib.util;

import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;

public class DyeHelper {

  public static String getOreName(EnumDyeColor dye) {

    String name = dye.getUnlocalizedName();
    return "dye" + (dye == EnumDyeColor.SILVER ? "LightGray"
        : Character.toUpperCase(name.charAt(0)) + name.substring(1));
  }

  public static int getColor(EnumDyeColor dye) {

    return ItemDye.DYE_COLORS[dye.getDyeDamage()];
  }

  public static boolean isItemDye(ItemStack stack) {

    return oreDictDyeToVanilla(stack) != null;
  }

  @Nullable
  public static ItemStack oreDictDyeToVanilla(ItemStack stack) {

    for (int id : OreDictionary.getOreIDs(stack)) {
      String name = OreDictionary.getOreName(id);
      if (name.startsWith("dye")) {
        name = name.substring(3);
        if (name.equals("lightgray"))
          name = "silver";

        for (int i = 0; i < EnumDyeColor.values().length; ++i)
          if (EnumDyeColor.values()[i].getUnlocalizedName().equalsIgnoreCase(name))
            return new ItemStack(Items.DYE, 1, i);
      }
    }

    return null;
  }
}
