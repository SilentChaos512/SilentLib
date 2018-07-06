/*
 * SilentLib - MessageLeftClick
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

package net.silentchaos512.lib.network.internal;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.silentchaos512.lib.item.IItemSL;
import net.silentchaos512.lib.network.MessageSL;
import net.silentchaos512.lib.util.StackHelper;

public final class MessageLeftClick extends MessageSL {

  public enum Type {
    EMPTY, BLOCK;
  }

  public int type;
  public boolean mainHand;

  public MessageLeftClick() {

    this.type = 0;
    this.mainHand = true;
  }

  public MessageLeftClick(Type type, EnumHand hand) {

    this.type = type.ordinal();
    this.mainHand = hand == EnumHand.MAIN_HAND;
  }

  @Override
  public IMessage handleMessage(MessageContext context) {

    if (context.side != Side.SERVER)
      return null;

    EnumHand hand = mainHand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
    EntityPlayer player = context.getServerHandler().player;
    ItemStack heldItem = player.getHeldItem(hand);

    if (StackHelper.isValid(heldItem) && heldItem.getItem() instanceof IItemSL) {
      IItemSL item = (IItemSL) heldItem.getItem();
      if (type == Type.EMPTY.ordinal()) {
        item.onItemLeftClickSL(player.world, player, hand);
      } else {
        item.onItemLeftClickBlockSL(player.world, player, hand);
      }
    }

    return null;
  }
}
