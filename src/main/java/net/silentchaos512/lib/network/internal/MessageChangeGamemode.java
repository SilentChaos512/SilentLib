/*
 * SilentLib - MessageChangeGamemode
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

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.silentchaos512.lib.network.MessageSL;

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

    EntityPlayerMP player = context.getServerHandler().player;
    int permLevel = player.getServer().getPlayerList().getOppedPlayers().getPermissionLevel(player.getGameProfile());
    if (permLevel >= 2)
      player.setGameType(GameType.getByID(this.type));

    return null;
  }
}
