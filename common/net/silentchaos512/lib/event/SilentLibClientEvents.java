/*
 * SilentLib - SilentLibClientEvents
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
