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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.silentchaos512.lib.SilentLib;

import java.util.List;
import java.util.Objects;

public final class DataDump {
    private static final String REGISTRY_NAME_IS_NULL = "Registry name is null! This indicates a broken mod and is a serious problem!";
    private static final String SEPARATOR = "--------------------------------------------------------------------------------";

    private DataDump() {
        throw new IllegalAccessError("Utility class");
    }

    public static void dumpEnchantments() {
        SilentLib.logHelper.info(SEPARATOR);
        for (Enchantment ench : Enchantment.REGISTRY) {
            try {
                ResourceLocation name = Objects.requireNonNull(ench.getRegistryName(), REGISTRY_NAME_IS_NULL);
                String translatedName = ench.getTranslatedName(1).replaceFirst(" I$", "");
                String type = ench.type == null ? "null" : ench.type.name();
                SilentLib.logHelper.info(String.format("%-30s %-40s type=%-10s", translatedName, name, type));
            } catch (Exception ex) {
                SilentLib.logHelper.warn("*** Error on enchantment: {} ***", ench);
                SilentLib.logHelper.catching(ex);
            }
        }
        SilentLib.logHelper.info(SEPARATOR);
    }

    public static void dumpEntityList() {
        SilentLib.logHelper.info(SEPARATOR);
        List<String> list = Lists.newArrayList();

        for (EntityEntry entry : ForgeRegistries.ENTITIES) {
            try {
                ResourceLocation name = Objects.requireNonNull(entry.getRegistryName(), REGISTRY_NAME_IS_NULL);
                Class<? extends Entity> clazz = EntityList.getClass(entry.getRegistryName());
                String oldName = EntityList.getTranslationName(name);
                int id = EntityList.getID(clazz);
                list.add(String.format("%-30s %4d   %-40s %-40s", oldName, id, name, clazz));
            } catch (Exception ex) {
                SilentLib.logHelper.warn("*** Error on entity: {} ***", entry.getEntityClass());
                SilentLib.logHelper.catching(ex);
            }
        }

        list.sort(String::compareToIgnoreCase);
        list.forEach(str -> SilentLib.logHelper.info(str));
        SilentLib.logHelper.info(SEPARATOR);
    }

    public static void dumpPotionEffects() {
        SilentLib.logHelper.info(SEPARATOR);
        for (Potion pot : Potion.REGISTRY) {
            try {
                ResourceLocation name = Objects.requireNonNull(pot.getRegistryName(), REGISTRY_NAME_IS_NULL);
                SilentLib.logHelper.info(String.format("%-30s %-40s", pot.getName(), name));
            } catch (Exception ex) {
                SilentLib.logHelper.warn("*** Error on potion: {} ***", pot);
                SilentLib.logHelper.catching(ex);
            }
        }
        SilentLib.logHelper.info(SEPARATOR);
    }

    public static void dumpRecipes() {
        SilentLib.logHelper.info(SEPARATOR);
        SilentLib.logHelper.info("The following is a list of all recipes registered as of Silent Lib's post-init:");

        for (IRecipe rec : CraftingManager.REGISTRY) {
            try {
                ResourceLocation name = Objects.requireNonNull(rec.getRegistryName(), REGISTRY_NAME_IS_NULL);
                int id = CraftingManager.REGISTRY.getIDForObject(rec);
                if (id < 0) throw new IndexOutOfBoundsException("id < 0");
                SilentLib.logHelper.info(String.format("%-6d %-40s", id, name));
            } catch (Exception ex) {
                SilentLib.logHelper.info("*** Error on recipe: {} ***", rec);
                throw ex;
            }
        }

        SilentLib.logHelper.info(SEPARATOR);
    }
}
