package net.silentchaos512.lib.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

// TODO: Rename to ILeftClickItem?
public interface IItemSL {

  /**
   * Networked left-click handler. Called in SilentLibEventHandlers on both the client- and server-side (via packet)
   * when a player left-clicks on nothing (in the air).
   *
   * @return If this returns SUCCESS on the client-side, a packet will be sent to the server.
   */
  public default ActionResult<ItemStack> onItemLeftClickSL(World world, EntityPlayer player,
      EnumHand hand) {

    return new ActionResult<ItemStack>(EnumActionResult.PASS, player.getHeldItem(hand));
  }

  /**
   * Called when the player left-clicks on a block. Defaults to the same behavior as an empty click (onItemLeftClickSL).
   *
   * @return If this returns SUCCESS on the client-side, a packet will be sent to the server.
   */
  public default ActionResult<ItemStack> onItemLeftClickBlockSL(World world, EntityPlayer player,
      EnumHand hand) {

    return onItemLeftClickSL(world, player, hand);
  }
}
