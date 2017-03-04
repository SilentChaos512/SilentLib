package net.silentchaos512.lib.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;

public interface ISidedInventorySL extends ISidedInventory {

  boolean isUsable(EntityPlayer player);

  @Override
  default boolean isUseableByPlayer(EntityPlayer player) {

    return isUsable(player);
  }
}
