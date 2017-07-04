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

  public static enum Type {
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
    EntityPlayer player = context.getServerHandler().playerEntity;
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
