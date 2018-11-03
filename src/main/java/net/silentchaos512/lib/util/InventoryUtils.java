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
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.Collection;

public final class InventoryUtils {
    private InventoryUtils() {throw new IllegalAccessError("Utility class");}

    public static boolean canItemsStack(ItemStack a, ItemStack b) {
        if (a.isEmpty() || !a.isItemEqual(b) || a.hasTagCompound() != b.hasTagCompound())
            return false;
        //noinspection ConstantConditions
        return (!a.hasTagCompound() || a.getTagCompound().equals(b.getTagCompound())) && a.areCapsCompatible(b);
    }

    public static ItemStack mergeItem(IInventory inventory, int slotStart, int slotEndExclusive, ItemStack stack) {
        if (inventory == null || stack.isEmpty())
            return stack;

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
        if (inventory == null && stacks.isEmpty())
            return ImmutableList.of();

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
