package net.silentchaos512.lib.tile;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public abstract class TileSidedInventorySL extends TileInventorySL implements ISidedInventory {

  @Override
  public int[] getSlotsForFace(EnumFacing side) {

    return new int[0];
  }

  @Override
  public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {

    return false;
  }

  @Override
  public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {

    return false;
  }
}
