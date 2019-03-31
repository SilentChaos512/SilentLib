/*
 * SilentLib - PlayerUtils
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

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;

public final class PlayerUtils {
    private PlayerUtils() {}

    /**
     * Gives the player an item. If it can't be given directly, it spawns an EntityItem. Spawns 1
     * block above player's feet.
     */
    public static void giveItem(EntityPlayer player, ItemStack stack) {
        ItemStack copy = stack.copy();
        if (!player.inventory.addItemStackToInventory(copy)) {
            EntityItem entityItem = new EntityItem(player.world, player.posX, player.posY + 1.0, player.posZ, copy);
            entityItem.setNoPickupDelay();
            entityItem.setOwnerId(player.getUniqueID());
            player.world.spawnEntity(entityItem);
        }
    }

    /**
     * Removes the stack from the player's inventory, if it exists.
     */
    public static void removeItem(EntityPlayer player, ItemStack stack) {
        List<NonNullList<ItemStack>> inventories = ImmutableList.of(player.inventory.mainInventory, player.inventory.offHandInventory, player.inventory.armorInventory);
        for (NonNullList<ItemStack> inv : inventories) {
            for (int i = 0; i < inv.size(); ++i) {
                if (stack == inv.get(i)) {
                    inv.set(i, ItemStack.EMPTY);
                    return;
                }
            }
        }
    }

    /**
     * Gets a tag compound from the player's persisted data NBT compound, or creates it if it does
     * not exist. This can be used to save additional data to a player.
     *
     * @param player         The player
     * @param subcompoundKey The key for the tag compound (ideally should contain mod ID)
     * @return The tag compound, creating it if it does not exist.
     */
    public static NBTTagCompound getPersistedDataSubcompound(EntityPlayer player, String subcompoundKey) {
        NBTTagCompound forgeData = player.getEntityData();
        if (!forgeData.contains(EntityPlayer.PERSISTED_NBT_TAG)) {
            forgeData.put(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        }

        NBTTagCompound persistedData = forgeData.getCompound(EntityPlayer.PERSISTED_NBT_TAG);
        if (!persistedData.contains(subcompoundKey)) {
            persistedData.put(subcompoundKey, new NBTTagCompound());
        }

        return persistedData.getCompound(subcompoundKey);
    }

    @Nonnull
    public static NonNullList<ItemStack> getNonEmptyStacks(EntityPlayer player) {
        return getNonEmptyStacks(player, true, true, true, s -> true);
    }

    @Nonnull
    public static NonNullList<ItemStack> getNonEmptyStacks(EntityPlayer player, Predicate<ItemStack> predicate) {
        return getNonEmptyStacks(player, true, true, true, predicate);
    }

    @Nonnull
    public static NonNullList<ItemStack> getNonEmptyStacks(EntityPlayer player, boolean includeMain, boolean includeOffHand, boolean includeArmor) {
        return getNonEmptyStacks(player, includeMain, includeOffHand, includeArmor, s -> true);
    }

    @Nonnull
    public static NonNullList<ItemStack> getNonEmptyStacks(EntityPlayer player, boolean includeMain, boolean includeOffHand, boolean includeArmor, Predicate<ItemStack> predicate) {
        NonNullList<ItemStack> list = NonNullList.create();

        if (includeMain)
            for (ItemStack stack : player.inventory.mainInventory)
                if (!stack.isEmpty() && predicate.test(stack))
                    list.add(stack);

        if (includeOffHand)
            for (ItemStack stack : player.inventory.offHandInventory)
                if (!stack.isEmpty() && predicate.test(stack))
                    list.add(stack);

        if (includeArmor)
            for (ItemStack stack : player.inventory.armorInventory)
                if (!stack.isEmpty() && predicate.test(stack))
                    list.add(stack);

        return list;
    }

    /**
     * Gets the first matching valid ItemStack in the players inventory.
     *
     * @param player         The player
     * @param includeMain    Search the players main inventory (hotbar and the 3 rows above that,
     *                       hotbar is first, I think).
     * @param includeOffHand Check the player's offhand slot as well.
     * @param includeArmor   Check the player's armor slots as well.
     * @param predicate      The condition to check for on the ItemStack.
     * @return The first ItemStack that matches the predicate, or ItemStack.EMPTY if there is no
     * match. Search order is offHand, armor, main.
     * @since 2.3.1
     */
    @Nonnull
    public static ItemStack getFirstValidStack(EntityPlayer player, boolean includeMain, boolean includeOffHand, boolean includeArmor, Predicate<ItemStack> predicate) {
        if (includeOffHand)
            for (ItemStack stack : player.inventory.offHandInventory)
                if (!stack.isEmpty() && predicate.test(stack))
                    return stack;

        if (includeArmor)
            for (ItemStack stack : player.inventory.armorInventory)
                if (!stack.isEmpty() && predicate.test(stack))
                    return stack;

        if (includeMain)
            for (ItemStack stack : player.inventory.mainInventory)
                if (!stack.isEmpty() && predicate.test(stack))
                    return stack;

        return ItemStack.EMPTY;
    }
}
