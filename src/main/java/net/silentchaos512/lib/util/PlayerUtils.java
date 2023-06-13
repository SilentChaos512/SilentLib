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
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;

public final class PlayerUtils {
    private PlayerUtils() {}

    /**
     * Gives the player an item. If it can't be given directly, it spawns an EntityItem. Spawns 1
     * block above player's feet.
     *
     * @param player The player
     * @param stack  The item
     */
    public static void giveItem(Player player, ItemStack stack) {
        ItemStack copy = stack.copy();
        if (!player.getInventory().add(copy)) {
            ItemEntity entityItem = new ItemEntity(player.level(), player.getX(), player.getY(0.5), player.getZ(), copy);
            entityItem.setNoPickUpDelay();
            entityItem.setThrower(player.getUUID());
            player.level().addFreshEntity(entityItem);
        }
    }

    /**
     * Removes the stack from the player's inventory, if it exists.
     *
     * @param player The player
     * @param stack  The item
     */
    public static void removeItem(Player player, ItemStack stack) {
        List<NonNullList<ItemStack>> inventories = ImmutableList.of(player.getInventory().items, player.getInventory().offhand, player.getInventory().armor);
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
     * @param subCompoundKey The key for the tag compound (ideally should contain mod ID)
     * @return The tag compound, creating it if it does not exist.
     */
    public static CompoundTag getPersistedDataSubcompound(Player player, String subCompoundKey) {
        CompoundTag forgeData = player.getPersistentData();
        if (!forgeData.contains(Player.PERSISTED_NBT_TAG)) {
            forgeData.put(Player.PERSISTED_NBT_TAG, new CompoundTag());
        }

        CompoundTag persistedData = forgeData.getCompound(Player.PERSISTED_NBT_TAG);
        if (!persistedData.contains(subCompoundKey)) {
            persistedData.put(subCompoundKey, new CompoundTag());
        }

        return persistedData.getCompound(subCompoundKey);
    }

    @Nonnull
    public static NonNullList<ItemStack> getNonEmptyStacks(Player player) {
        return getNonEmptyStacks(player, true, true, true, s -> true);
    }

    @Nonnull
    public static NonNullList<ItemStack> getNonEmptyStacks(Player player, Predicate<ItemStack> predicate) {
        return getNonEmptyStacks(player, true, true, true, predicate);
    }

    @Nonnull
    public static NonNullList<ItemStack> getNonEmptyStacks(Player player, boolean includeMain, boolean includeOffHand, boolean includeArmor) {
        return getNonEmptyStacks(player, includeMain, includeOffHand, includeArmor, s -> true);
    }

    @Nonnull
    public static NonNullList<ItemStack> getNonEmptyStacks(Player player, boolean includeMain, boolean includeOffHand, boolean includeArmor, Predicate<ItemStack> predicate) {
        NonNullList<ItemStack> list = NonNullList.create();

        if (includeMain)
            for (ItemStack stack : player.getInventory().items)
                if (!stack.isEmpty() && predicate.test(stack))
                    list.add(stack);

        if (includeOffHand)
            for (ItemStack stack : player.getInventory().offhand)
                if (!stack.isEmpty() && predicate.test(stack))
                    list.add(stack);

        if (includeArmor)
            for (ItemStack stack : player.getInventory().armor)
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
    public static ItemStack getFirstValidStack(Player player, boolean includeMain, boolean includeOffHand, boolean includeArmor, Predicate<ItemStack> predicate) {
        if (includeOffHand)
            for (ItemStack stack : player.getInventory().offhand)
                if (!stack.isEmpty() && predicate.test(stack))
                    return stack;

        if (includeArmor)
            for (ItemStack stack : player.getInventory().armor)
                if (!stack.isEmpty() && predicate.test(stack))
                    return stack;

        if (includeMain)
            for (ItemStack stack : player.getInventory().items)
                if (!stack.isEmpty() && predicate.test(stack))
                    return stack;

        return ItemStack.EMPTY;
    }
}
