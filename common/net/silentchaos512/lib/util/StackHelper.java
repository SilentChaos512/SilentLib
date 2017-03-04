package net.silentchaos512.lib.util;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;
import net.silentchaos512.lib.collection.ItemStackList;

public class StackHelper {

  @Nonnull
  public static ItemStack grow(@Nonnull ItemStack stack, int amount) {

    stack.stackSize += amount;
    if (stack.stackSize <= 0)
      return null;
    return stack;
  }

  @Nonnull
  public static ItemStack shrink(@Nonnull ItemStack stack, int amount) {

    return grow(stack, -amount);
  }

  @Nonnull
  public static ItemStack safeCopy(@Nullable ItemStack stack) {

    if (stack == null)
      return null;

    stack = stack.copy();
    if (stack.stackSize == 0)
      stack.stackSize = 1;
    return stack;
  }

  public static int getCount(@Nullable ItemStack stack) {

    if (stack == null)
      return 0;
    return stack.stackSize;
  }

  @Nonnull
  public static ItemStack setCount(@Nonnull ItemStack stack, int amount) {

    if (amount <= 0) {
      stack.stackSize = 0;
      return null;
    }

    stack.stackSize = amount;
    return stack;
  }

  public static boolean isEmpty(@Nullable ItemStack stack) {

    if (stack == null)
      return true;
    return stack.stackSize <= 0;
  }

  public static boolean isValid(@Nullable ItemStack stack) {

    if (stack == null)
      return false;
    return stack.stackSize > 0;
  }

  @Nullable
  public static ItemStack loadFromNBT(NBTTagCompound tags) {

    if (tags == null)
      return empty();
    return ItemStack.loadItemStackFromNBT(tags);
  }

  @Nonnull
  public static ItemStack empty() {

    return null;
  }

  @Nonnull
  public static ItemStack extractItem(@Nullable TileEntity tileEntity, int slot, int amount) {

    if (tileEntity != null
        && tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
      IItemHandler capability = tileEntity
          .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
      return capability.extractItem(slot, amount, false);
    } else if (tileEntity instanceof IInventory) {
      IInventory inventory = (IInventory) tileEntity;
      return inventory.decrStackSize(slot, amount);
    }
    return empty();
  }

  public static void setStack(@Nullable TileEntity tileEntity, int slot, @Nonnull ItemStack stack) {

    if (tileEntity != null
        && tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
      IItemHandler capability = tileEntity
          .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
      capability.extractItem(slot, 64, false);
      capability.insertItem(slot, stack, false);
    } else if (tileEntity instanceof IInventory) {
      IInventory inventory = (IInventory) tileEntity;
      inventory.setInventorySlotContents(slot, stack);
    }
  }

  public static List<ItemStack> getOres(String name) {

    return OreDictionary.getOres(name);
  }

  public static List<ItemStack> getOres(String name, boolean alwaysCreateEntry) {

    return OreDictionary.getOres(name, alwaysCreateEntry);
  }

  public static ItemStack getAndSplit(ItemStackList stacks, int index, int amount) {

    return index >= 0 && index < stacks.size() && StackHelper.isValid(stacks.get(index))
        && amount > 0 ? stacks.get(index).splitStack(amount) : empty();
  }

  public static ItemStack getAndRemove(ItemStackList stacks, int index) {

    return index >= 0 && index < stacks.size() ? stacks.set(index, empty()) : empty();
  }

  public static NBTTagCompound saveAllItems(NBTTagCompound tags, ItemStackList stacks) {

    return saveAllItems(tags, stacks, true);
  }

  public static NBTTagCompound saveAllItems(NBTTagCompound tags, ItemStackList stacks,
      boolean p_191281_2_) {

    NBTTagList nbttaglist = new NBTTagList();

    for (int i = 0; i < stacks.size(); ++i) {
      ItemStack itemstack = stacks.get(i);

      if (isValid(itemstack)) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setByte("Slot", (byte) i);
        itemstack.writeToNBT(nbttagcompound);
        nbttaglist.appendTag(nbttagcompound);
      }
    }

    if (!nbttaglist.hasNoTags() || p_191281_2_) {
      tags.setTag("Items", nbttaglist);
    }

    return tags;
  }

  public static void loadAllItems(NBTTagCompound tags, ItemStackList stacks) {

    NBTTagList nbttaglist = tags.getTagList("Items", 10);

    for (int i = 0; i < nbttaglist.tagCount(); ++i) {
      NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
      int j = nbttagcompound.getByte("Slot") & 255;

      if (j >= 0 && j < stacks.size()) {
        stacks.set(j, loadFromNBT(nbttagcompound));
      }
    }
  }
}
