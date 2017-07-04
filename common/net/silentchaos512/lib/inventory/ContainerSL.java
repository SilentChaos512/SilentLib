package net.silentchaos512.lib.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSL extends Container {

  protected final IInventory tileInventory;

  public ContainerSL(InventoryPlayer playerInventory, IInventory tileInventory) {

    this.tileInventory = tileInventory;
    addTileInventorySlots(tileInventory);
    addPlayerInventorySlots(playerInventory);
  }

  protected void addTileInventorySlots(IInventory inv) {

  }

  protected void addPlayerInventorySlots(InventoryPlayer inv) {

    int i;
    for (i = 0; i < 3; ++i) {
      for (int j = 0; j < 9; ++j) {
        this.addSlotToContainer(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
      }
    }

    for (i = 0; i < 9; ++i) {
      this.addSlotToContainer(new Slot(inv, i, 8 + i * 18, 142));
    }
  }

  @Override
  public boolean canInteractWith(EntityPlayer player) {

    return tileInventory.isUsableByPlayer(player);
  }

  public static void onTakeFromSlot(Slot slot, EntityPlayer player, ItemStack stack) {

    slot.onPickupFromSlot(player, stack);
  }
}
