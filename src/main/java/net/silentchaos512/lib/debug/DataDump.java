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
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.lib.SilentLib;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DataDump {
    private static final String REGISTRY_NAME_IS_NULL = "Registry name is null! This indicates a broken mod and is a serious problem!";
    private static final String SEPARATOR = "--------------------------------------------------------------------------------";

    private DataDump() {
        throw new IllegalAccessError("Utility class");
    }

    public static void dumpBlocks() {
        SilentLib.LOGGER.info(SEPARATOR);
        List<String> lines = new ArrayList<>();

        for (Block block : ForgeRegistries.BLOCKS) {
            try {
                ResourceLocation name = Objects.requireNonNull(block.getRegistryName(), REGISTRY_NAME_IS_NULL);
                String translatedName = block.getNameTextComponent().getFormattedText();
                lines.add(String.format("%-60s %-60s", name, translatedName));
            } catch (Exception ex) {
                SilentLib.LOGGER.warn("*** Error on block: {} ***", block);
                SilentLib.LOGGER.catching(ex);
            }
        }

        lines.sort(String::compareToIgnoreCase);
        lines.forEach(SilentLib.LOGGER::info);
        SilentLib.LOGGER.info(SEPARATOR);
    }

    public static void dumpEnchantments() {
        SilentLib.LOGGER.info(SEPARATOR);
        for (Enchantment ench : ForgeRegistries.ENCHANTMENTS) {
            try {
                ResourceLocation name = Objects.requireNonNull(ench.getRegistryName(), REGISTRY_NAME_IS_NULL);
                String translatedName = ench.getDisplayName(1).getFormattedText().replaceFirst(" I$", "");
                String type = ench.type == null ? "null" : ench.type.name();
                SilentLib.LOGGER.info(String.format("%-30s %-40s type=%-10s", translatedName, name, type));
            } catch (Exception ex) {
                SilentLib.LOGGER.warn("*** Error on enchantment: {} ***", ench);
                SilentLib.LOGGER.catching(ex);
            }
        }
        SilentLib.LOGGER.info(SEPARATOR);
    }

    public static void dumpEntityList() {
        SilentLib.LOGGER.info(SEPARATOR);
        List<String> list = Lists.newArrayList();

        for (EntityType<?> entry : ForgeRegistries.ENTITIES) {
            try {
                ResourceLocation name = Objects.requireNonNull(entry.getRegistryName(), REGISTRY_NAME_IS_NULL);
                Class<? extends Entity> clazz = entry.getEntityClass();
                String oldName = entry.getTranslationKey();
                list.add(String.format("%-30s   %-40s %-40s", oldName, name, clazz));
            } catch (Exception ex) {
                SilentLib.LOGGER.warn("*** Error on entity: {} ***", entry.getEntityClass());
                SilentLib.LOGGER.catching(ex);
            }
        }

        list.sort(String::compareToIgnoreCase);
        list.forEach(SilentLib.LOGGER::info);
        SilentLib.LOGGER.info(SEPARATOR);
    }

    public static void dumpItems() {
        SilentLib.LOGGER.info(SEPARATOR);
        List<String> lines = new ArrayList<>();

        for (Item item : ForgeRegistries.ITEMS) {
            try {
                ResourceLocation name = Objects.requireNonNull(item.getRegistryName(), REGISTRY_NAME_IS_NULL);
                ItemStack stack = new ItemStack(item);
                String translatedName = stack.getDisplayName().getFormattedText();
                lines.add(String.format("%-60s %-60s", name, translatedName));
            } catch (Exception ex) {
                SilentLib.LOGGER.warn("*** Error on item: {} ***", item);
                SilentLib.LOGGER.catching(ex);
            }
        }

//        lines.sort(String::compareToIgnoreCase);
        lines.forEach(SilentLib.LOGGER::info);
        SilentLib.LOGGER.info(SEPARATOR);
    }

    public static void dumpPotionEffects() {
        SilentLib.LOGGER.info(SEPARATOR);
        for (Potion pot : ForgeRegistries.POTIONS) {
            try {
                ResourceLocation name = Objects.requireNonNull(pot.getRegistryName(), REGISTRY_NAME_IS_NULL);
                SilentLib.LOGGER.info(String.format("%-30s %-40s", pot.getName(), name));
            } catch (Exception ex) {
                SilentLib.LOGGER.warn("*** Error on potion: {} ***", pot);
                SilentLib.LOGGER.catching(ex);
            }
        }
        SilentLib.LOGGER.info(SEPARATOR);
    }

    public static void dumpRecipes() {
//        SilentLib.LOGGER.info(SEPARATOR);
//        SilentLib.LOGGER.info("The following is a list of all recipes registered as of Silent Lib's post-init:");
//
//        for (IRecipe rec : CraftingManager.REGISTRY) {
//            try {
//                ResourceLocation name = Objects.requireNonNull(rec.getRegistryName(), REGISTRY_NAME_IS_NULL);
//                int id = CraftingManager.REGISTRY.getIDForObject(rec);
//                if (id < 0) throw new IndexOutOfBoundsException("id < 0");
//                SilentLib.LOGGER.info(String.format("%-6d %-40s", id, name));
//            } catch (Exception ex) {
//                SilentLib.LOGGER.info("*** Error on recipe: {} ***", rec);
//                throw ex;
//            }
//        }
//
//        SilentLib.LOGGER.info(SEPARATOR);
    }
}
