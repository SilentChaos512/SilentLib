package net.silentchaos512.lib.event;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.item.IItemSL;
import net.silentchaos512.lib.network.internal.MessageLeftClick;
import net.silentchaos512.lib.util.StackHelper;

/**
 * Silent Lib's client event handler. Do not call any functions of this class.
 * 
 * @author SilentChaos512
 * @since 2.1.4
 */
public final class SilentLibClientEvents {

  @SubscribeEvent
  public void onLeftClickEmpty(LeftClickEmpty event) {

    ItemStack stack = event.getItemStack();
    if (StackHelper.isValid(stack) && stack.getItem() instanceof IItemSL) {
      // Client-side call
      ActionResult<ItemStack> result = ((IItemSL) stack.getItem())
          .onItemLeftClickSL(event.getWorld(), event.getEntityPlayer(), event.getHand());
      // Server-side call
      if (result.getType() == EnumActionResult.SUCCESS) {
        SilentLib.network.wrapper
            .sendToServer(new MessageLeftClick(MessageLeftClick.Type.EMPTY, event.getHand()));
      }
    }
  }

  @SubscribeEvent
  public void onLeftClickBlock(LeftClickBlock event) {

    ItemStack stack = event.getItemStack();
    if (StackHelper.isValid(stack) && stack.getItem() instanceof IItemSL) {
      // Client-side call
      ActionResult<ItemStack> result = ((IItemSL) stack.getItem())
          .onItemLeftClickBlockSL(event.getWorld(), event.getEntityPlayer(), event.getHand());
      // Server-side call
      if (result.getType() == EnumActionResult.SUCCESS) {
        SilentLib.network.wrapper
            .sendToServer(new MessageLeftClick(MessageLeftClick.Type.BLOCK, event.getHand()));
      }
    }
  }
}
