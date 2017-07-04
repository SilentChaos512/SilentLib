package net.silentchaos512.lib.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.silentchaos512.lib.collection.ItemStackList;
import net.silentchaos512.lib.util.StackHelper;

public abstract class TileInventorySL extends TileEntitySL implements IInventorySL {

  protected ItemStackList inventory;

  public TileInventorySL() {

    inventory = ItemStackList.create(getSizeInventory());
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
  public ItemStack getStackInSlot(int index) {

    if (index < 0 || index >= inventory.size())
      return StackHelper.empty();
    return inventory.get(index);
  }

  @Override
  public ItemStack decrStackSize(int index, int count) {

    return StackHelper.getAndSplit(inventory, index, count);
  }

  @Override
  public ItemStack removeStackFromSlot(int index) {

    return StackHelper.getAndRemove(inventory, index);
  }

  @Override
  public void setInventorySlotContents(int index, ItemStack stack) {

    inventory.set(index, stack);

    if (StackHelper.getCount(stack) > getInventoryStackLimit())
      StackHelper.setCount(stack, getInventoryStackLimit());
  }

  @Override
  public int getInventoryStackLimit() {

    return 64;
  }

  @Override
  public boolean isUsable(EntityPlayer player) {

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

    inventory = ItemStackList.create(getSizeInventory());
    StackHelper.loadAllItems(tags, inventory);
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound tags) {

    super.writeToNBT(tags);

    StackHelper.saveAllItems(tags, inventory);

    return tags;
  }

  @Override
  public SPacketUpdateTileEntity getUpdatePacket() {

    NBTTagCompound tags = getUpdateTag();
    StackHelper.saveAllItems(tags, inventory);
    return new SPacketUpdateTileEntity(pos, 1, tags);
  }

  @Override
  public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {

    super.onDataPacket(net, packet);
    StackHelper.loadAllItems(packet.getNbtCompound(), inventory);
  }
}
