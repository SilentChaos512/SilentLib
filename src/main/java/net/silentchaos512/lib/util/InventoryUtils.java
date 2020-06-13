/*
 * Silent Lib -- InventoryUtils
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.lib.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.silentchaos512.lib.collection.StackList;
import net.silentchaos512.utils.MathUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

public final class InventoryUtils {
    private InventoryUtils() {throw new IllegalAccessError("Utility class");}

    /**
     * Creates slots for the player's inventory for a {@link Container}. Convenience method to
     * improve readability of Container code.
     *
     * @param playerInventory Player's inventory
     * @param startX          X-position of top-left slot
     * @param startY          Y-position of top-left slot
     * @return A collection of slots to be added
     * @since 4.1.1
     */
    public static Collection<Slot> createPlayerSlots(PlayerInventory playerInventory, int startX, int startY) {
        Collection<Slot> list = new ArrayList<>();
        // Backpack
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                list.add(new Slot(playerInventory, x + y * 9 + 9, startX + x * 18, startY + y * 18));
            }
        }
        // Hotbar
        for (int x = 0; x < 9; ++x) {
            list.add(new Slot(playerInventory, x, 8 + x * 18, startY + 58));
        }
        return list;
    }

    public static boolean canItemsStack(ItemStack a, ItemStack b) {
        if (a.getItem() != b.getItem())
            return false;
        if (a.getTag() == null && b.getTag() != null)
            return false;
        return a.getTag() == null || a.getTag().equals(b.getTag()) && a.areCapsCompatible(b);
    }

    /**
     * Obtain the first matching stack. {@link StackList} has a similar method, but this avoids
     * creating the entire list when it isn't needed.
     *
     * @param inv       The inventory to search
     * @param predicate Condition to match
     * @return The first matching stack, or {@link ItemStack#EMPTY} if there is none
     * @since 3.1.0 (was in StackHelper from 3.0.6)
     */
    public static ItemStack firstMatch(IInventory inv, Predicate<ItemStack> predicate) {
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty() && predicate.test(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack mergeItem(IInventory inventory, int slotStart, int slotEndExclusive, ItemStack stack) {
        if (inventory == null || stack.isEmpty()) {
            return stack;
        }

        // Merge into non-empty slots first
        for (int i = slotStart; i < slotEndExclusive && !stack.isEmpty(); ++i) {
            ItemStack inSlot = inventory.getStackInSlot(i);
            if (canItemsStack(inSlot, stack)) {
                int amountCanFit = MathUtils.min(inSlot.getMaxStackSize() - inSlot.getCount(), stack.getCount(), inventory.getInventoryStackLimit());
                inSlot.grow(amountCanFit);
                stack.shrink(amountCanFit);
                inventory.setInventorySlotContents(i, inSlot);
            }
        }

        // Fill empty slots next
        for (int i = slotStart; i < slotEndExclusive && !stack.isEmpty(); ++i) {
            if (inventory.getStackInSlot(i).isEmpty()) {
                int amountCanFit = MathUtils.min(stack.getCount(), inventory.getInventoryStackLimit());
                ItemStack toInsert = stack.copy();
                toInsert.setCount(amountCanFit);
                stack.shrink(amountCanFit);
                inventory.setInventorySlotContents(i, toInsert);
            }
        }

        return stack;
    }

    public static Collection<ItemStack> mergeItems(IInventory inventory, int slotStart, int slotEndExclusive, Collection<ItemStack> stacks) {
        if (inventory == null && stacks.isEmpty()) {
            return ImmutableList.of();
        }

        ImmutableList.Builder<ItemStack> leftovers = ImmutableList.builder();

        for (ItemStack stack : stacks) {
            stack = mergeItem(inventory, slotStart, slotEndExclusive, stack);

            // Failed to merge?
            if (!stack.isEmpty()) {
                leftovers.add(stack);
            }
        }

        return leftovers.build();
    }
}
