/*
 * SilentLib - DataDump
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

package net.silentchaos512.lib.debug;

import com.google.common.collect.Lists;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.lib.SilentLib;

import java.util.Iterator;
import java.util.List;

public class DataDump {

  static final String SEPARATOR = "--------------------------------------------------------------------------------";

  public static void dumpEnchantments() {

    SilentLib.logHelper.info(SEPARATOR);
    SilentLib.logHelper.info("The following is a list of all enchantments registered as of Silent Lib's post-init:");

    Iterator<Enchantment> iter = Enchantment.REGISTRY.iterator();
    while (iter.hasNext()) {
      Enchantment ench = iter.next();
      try {
        String str = "  %-30s %-40s type=%-10s";
        str = String.format(str, ench.getTranslatedName(1).replaceFirst(" I$", ""),
            ench.getRegistryName(), (ench.type == null ? "null" : ench.type.name()));
        SilentLib.logHelper.info(str);
      } catch (Exception ex) {
        SilentLib.logHelper.info("Errored on enchantment: " + ench);
      }
    }

    SilentLib.logHelper.info(SEPARATOR);
  }

  public static void dumpEntityList() {

    SilentLib.logHelper.info(SEPARATOR);
    SilentLib.logHelper.info("The following is a list of all entities registered as of Silent Lib's post-init:");

    List<String> list = Lists.newArrayList();

    for (ResourceLocation res : EntityList.getEntityNameList()) {
      try {
        Class clazz = EntityList.getClass(res);
        String name = EntityList.getTranslationName(res);
        int id = EntityList.getID(clazz);

        String str = "  %-30s %4d   %-40s %-40s";
        str = String.format(str, name, id, res, clazz);
        list.add(str);
      } catch (Exception ex) {
        list.add("***Errored on entity: " + res);
      }
    }

    list.sort((String s1, String s2) -> s1.compareToIgnoreCase(s2));
    list.forEach(str -> SilentLib.logHelper.info(str));

    SilentLib.logHelper.info(SEPARATOR);
  }

  public static void dumpPotionEffects() {

    SilentLib.logHelper.info(SEPARATOR);
    SilentLib.logHelper.info("The following is a list of all potion effects registered as of Silent Lib's post-init:");

    Iterator<Potion> iter = Potion.REGISTRY.iterator();
    while (iter.hasNext()) {
      Potion pot = iter.next();
      try {
        String str = "%-30s %-40s";
        str = String.format(str, pot.getName(), pot.getRegistryName().toString());
        SilentLib.logHelper.info(str);
      } catch (Exception ex) {
        SilentLib.logHelper.info("Errored on potion: " + pot);
      }
    }

    SilentLib.logHelper.info(SEPARATOR);
  }

  public static void dumpRecipes() {

    SilentLib.logHelper.info(SEPARATOR);
    SilentLib.logHelper.info("The following is a list of all recipes registered as of Silent Lib's post-init:");

    Iterator<IRecipe> iter = CraftingManager.REGISTRY.iterator();
    while (iter.hasNext()) {
      IRecipe rec = iter.next();
      try {
        int id = CraftingManager.REGISTRY.getIDForObject(rec);
        if (id < 0)
          throw new IndexOutOfBoundsException("id < 0");
        String str = "%-6d %-40s";
        str = String.format(str, id, rec.getRegistryName().toString());
        SilentLib.logHelper.info(str);
      } catch (Exception ex) {
        SilentLib.logHelper.info("Errored on recipe: " + rec);
        throw ex;
      }
    }

    SilentLib.logHelper.info(SEPARATOR);
  }
}
