package net.silentchaos512.lib.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;
import net.silentchaos512.lib.collection.ItemStackList;
import net.silentchaos512.lib.collection.StackList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class StackHelper {

    @Deprecated
    @Nonnull
    public static ItemStack grow(@Nonnull ItemStack stack, int amount) {

        stack.grow(amount);
        return stack;
    }

    @Deprecated
    @Nonnull
    public static ItemStack shrink(@Nonnull ItemStack stack, int amount) {

        stack.shrink(amount);
        return stack;
    }

    @Deprecated
    @Nonnull
    public static ItemStack safeCopy(@Nonnull ItemStack stack) {

        return stack.copy();
    }

    @Deprecated
    public static int getCount(@Nonnull ItemStack stack) {

        return stack.getCount();
    }

    @Deprecated
    @Nonnull
    public static ItemStack setCount(@Nonnull ItemStack stack, int amount) {

        stack.setCount(amount);
        if (amount <= 0)
            return empty();
        return stack;
    }

    public static boolean isEmpty(ItemStack stack) {

        return stack == null || stack.isEmpty();
    }

    public static boolean isValid(ItemStack stack) {

        return stack != null && !stack.isEmpty();
    }

    @Nonnull
    public static ItemStack loadFromNBT(NBTTagCompound tags) {

        if (tags == null)
            return ItemStack.EMPTY;
        return new ItemStack(tags);
    }

    @Deprecated
    @Nonnull
    public static ItemStack empty() {

        return ItemStack.EMPTY;
    }

    /**
     * Gets the NBT tag compound for the stack.
     *
     * @param stack        the {@code ItemStack}
     * @param createIfNull if true, a new, empty {@code NBTTagCompound} will be set on the stack if it does not have one
     * @return The stack's tag compound, or {@code null} if it does not have one and {@code createIfNull} is false
     */
    @Deprecated
    public static NBTTagCompound getTagCompound(@Nonnull ItemStack stack, boolean createIfNull) {

        if (isEmpty(stack))
            return null;
        if (!stack.hasTagCompound() && createIfNull)
            stack.setTagCompound(new NBTTagCompound());
        return stack.getTagCompound();
    }

    @Nonnull
    public static ItemStack extractItem(@Nullable TileEntity tileEntity, int slot, int amount) {

        if (tileEntity != null && tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            IItemHandler capability = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            return capability.extractItem(slot, amount, false);
        } else if (tileEntity instanceof IInventory) {
            IInventory inventory = (IInventory) tileEntity;
            return inventory.decrStackSize(slot, amount);
        }
        return empty();
    }

    public static void setStack(@Nullable TileEntity tileEntity, int slot, @Nonnull ItemStack stack) {

        if (tileEntity != null && tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            IItemHandler capability = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            capability.extractItem(slot, 64, false);
            capability.insertItem(slot, stack, false);
        } else if (tileEntity instanceof IInventory) {
            IInventory inventory = (IInventory) tileEntity;
            inventory.setInventorySlotContents(slot, stack);
        }
    }

    @Nonnull
    public static List<ItemStack> getOres(String oreDictKey) {

        return OreDictionary.getOres(oreDictKey);
    }

    @Nonnull
    public static List<ItemStack> getOres(String oreDictKey, boolean alwaysCreateEntry) {

        return OreDictionary.getOres(oreDictKey, alwaysCreateEntry);
    }

    /**
     * Gets all ore dictionary keys for the stack. If the stack is empty, an empty list is returned.
     *
     * @param stack The ItemStack, which may be empty.
     * @return A list of strings, which may be empty.
     * @since 2.3.2
     */
    @Nonnull
    public static List<String> getOreNames(@Nonnull ItemStack stack) {

        List<String> list = new ArrayList<>();
        if (stack.isEmpty())
            return list;

        for (int id : OreDictionary.getOreIDs(stack))
            list.add(OreDictionary.getOreName(id));
        return list;
    }

    public static boolean matchesOreDict(ItemStack stack, String oreDictKey) {

        if (stack.isEmpty())
            return false;

        for (String oreName : getOreNames(stack))
            if (oreName.equals(oreDictKey))
                return true;

        for (ItemStack stackOre : getOres(oreDictKey))
            if (stack.isItemEqual(stackOre))
                return true;

        return false;
    }

    /**
     * Gets all non-empty stacks inside the inventory. Inventories can be painful to iterate over, so this takes care of the ugly part.
     *
     * @param inv The inventory
     * @return A StackList of non-empty ItemStacks
     * @since 3.0.0(?)
     */
    public static StackList getNonEmptyStacks(@Nonnull IInventory inv) {
        StackList list = StackList.of();
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            // StackList automatically filters empty stacks
            list.add(inv.getStackInSlot(i));
        }
        return list;
    }

    @Deprecated
    public static ItemStack getAndSplit(ItemStackList stacks, int index, int amount) {

        return ItemStackHelper.getAndSplit(stacks, index, amount);
    }

    @Deprecated
    public static ItemStack getAndRemove(ItemStackList stacks, int index) {

        return ItemStackHelper.getAndRemove(stacks, index);
    }

    @Deprecated
    public static NBTTagCompound saveAllItems(NBTTagCompound tags, ItemStackList stacks) {

        return saveAllItems(tags, stacks, true);
    }

    @Deprecated
    public static NBTTagCompound saveAllItems(NBTTagCompound tags, ItemStackList stacks, boolean saveEmpty) {

        return ItemStackHelper.saveAllItems(tags, stacks, saveEmpty);
    }

    @Deprecated
    public static void loadAllItems(NBTTagCompound tags, ItemStackList stacks) {

        ItemStackHelper.loadAllItems(tags, stacks);
    }
}
