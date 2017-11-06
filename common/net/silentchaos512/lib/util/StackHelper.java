package net.silentchaos512.lib.util;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;
import net.silentchaos512.lib.collection.ItemStackList;

public class StackHelper {

  @Nonnull
  public static ItemStack grow(@Nonnull ItemStack stack, int amount) {

    stack.grow(amount);
    return stack;
  }

  @Nonnull
  public static ItemStack shrink(@Nonnull ItemStack stack, int amount) {

    stack.shrink(amount);
    return stack;
  }

  @Nonnull
  public static ItemStack safeCopy(@Nonnull ItemStack stack) {

    return stack.copy();
  }

  public static int getCount(@Nonnull ItemStack stack) {

    return stack.getCount();
  }

  @Nonnull
  public static ItemStack setCount(@Nonnull ItemStack stack, int amount) {

    stack.setCount(amount);
    if (amount <= 0)
      return empty();
    return stack;
  }

  public static boolean isEmpty(@Nonnull ItemStack stack) {

    return stack == null || stack.isEmpty();
  }

  public static boolean isValid(@Nonnull ItemStack stack) {

    return stack != null && !stack.isEmpty();
  }

  @Nonnull
  public static ItemStack loadFromNBT(NBTTagCompound tags) {

    if (tags == null)
      return empty();
    return new ItemStack(tags);
  }

  @Nonnull
  public static ItemStack empty() {

    return ItemStack.EMPTY;
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

  public static List<ItemStack> getOres(String oreDictKey) {

    return OreDictionary.getOres(oreDictKey);
  }

  public static List<ItemStack> getOres(String oreDictKey, boolean alwaysCreateEntry) {

    return OreDictionary.getOres(oreDictKey, alwaysCreateEntry);
  }

  public static boolean matchesOreDict(ItemStack stack, String oreDictKey) {

    if (StackHelper.isEmpty(stack))
      return false;

    for (ItemStack stackOre : getOres(oreDictKey))
      if (stack.isItemEqual(stackOre))
        return true;

    return false;
  }

  public static ItemStack getAndSplit(ItemStackList stacks, int index, int amount) {

    return ItemStackHelper.getAndSplit(stacks, index, amount);
  }

  public static ItemStack getAndRemove(ItemStackList stacks, int index) {

    return ItemStackHelper.getAndRemove(stacks, index);
  }

  public static NBTTagCompound saveAllItems(NBTTagCompound tags, ItemStackList stacks) {

    return saveAllItems(tags, stacks, true);
  }

  public static NBTTagCompound saveAllItems(NBTTagCompound tags, ItemStackList stacks,
      boolean p_191281_2_) {

    return ItemStackHelper.saveAllItems(tags, stacks, p_191281_2_);
  }

  public static void loadAllItems(NBTTagCompound tags, ItemStackList stacks) {

    ItemStackHelper.loadAllItems(tags, stacks);
  }
}
