package net.silentchaos512.lib.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;

@Deprecated
public interface ISidedInventorySL extends ISidedInventory {

    boolean isUsable(EntityPlayer player);

    @Override
    default boolean isUsableByPlayer(EntityPlayer player) {
        return isUsable(player);
    }

    @Override
    default boolean isEmpty() {
        return false;
    }
}
