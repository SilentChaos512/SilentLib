package net.silentchaos512.lib.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public abstract class TileInventorySL extends TileEntity implements IInventory {

  protected NonNullList<ItemStack> inventory;

  public TileInventorySL() {

    inventory = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
  }

  @Override
  public ITextComponent getDisplayName() {

    String name = getName();
    if (name == null)
      return null;
    return new TextComponentString(name);
  }

  @Override
  public String getName() {

    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean hasCustomName() {

    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isEmpty() {

    for (ItemStack stack : inventory)
      if (!stack.isEmpty())
        return false;
    return true;
  }

  @Override
  public ItemStack getStackInSlot(int index) {

    if (index < 0 || index >= inventory.size())
      return ItemStack.EMPTY;
    return inventory.get(index);
  }

  @Override
  public ItemStack decrStackSize(int index, int count) {

    return ItemStackHelper.getAndSplit(inventory, index, count);
  }

  @Override
  public ItemStack removeStackFromSlot(int index) {

    return ItemStackHelper.getAndRemove(inventory, index);
  }

  @Override
  public void setInventorySlotContents(int index, ItemStack stack) {

    inventory.set(index, stack);

    if (stack.getCount() > getInventoryStackLimit())
      stack.setCount(getInventoryStackLimit());
  }

  @Override
  public int getInventoryStackLimit() {

    return 64;
  }

  @Override
  public boolean isUsableByPlayer(EntityPlayer player) {

    return world.getTileEntity(pos) != this ? false : player.getDistanceSq(pos) <= 64.0;
  }

  @Override
  public void openInventory(EntityPlayer player) {

  }

  @Override
  public void closeInventory(EntityPlayer player) {

  }

  @Override
  public boolean isItemValidForSlot(int index, ItemStack stack) {

    return true;
  }

  @Override
  public int getField(int id) {

    return 0;
  }

  @Override
  public void setField(int id, int value) {

  }

  @Override
  public int getFieldCount() {

    return 0;
  }

  @Override
  public void clear() {

    inventory.clear();
  }

  @Override
  public void readFromNBT(NBTTagCompound tags) {

    super.readFromNBT(tags);

    inventory = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
    ItemStackHelper.loadAllItems(tags, inventory);
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound tags) {

    super.writeToNBT(tags);

    ItemStackHelper.saveAllItems(tags, inventory);

    return tags;
  }
}
