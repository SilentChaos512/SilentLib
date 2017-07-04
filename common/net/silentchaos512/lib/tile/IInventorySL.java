package net.silentchaos512.lib.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public interface IInventorySL extends IInventory {

  boolean isUsable(EntityPlayer player);

  @Override
  default boolean isUsableByPlayer(EntityPlayer player) {

    return isUsable(player);
  }
}
