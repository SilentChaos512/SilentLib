package net.silentchaos512.lib.network.internal;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.silentchaos512.lib.item.IItemSL;
import net.silentchaos512.lib.network.MessageSL;
import net.silentchaos512.lib.util.StackHelper;

public final class MessageChangeGamemode extends MessageSL {

  public int type;

  public MessageChangeGamemode() {

    this.type = 0;
  }

  public MessageChangeGamemode(int type) {

    this.type = type;
  }

  @Override
  public IMessage handleMessage(MessageContext context) {

    if (context.side != Side.SERVER)
      return null;

    context.getServerHandler().player.setGameType(type == 3 ? GameType.SPECTATOR : type == 1 ? GameType.CREATIVE : GameType.SURVIVAL);

    return null;
  }
}
