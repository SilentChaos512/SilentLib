/*
 * SilentLib - ChatHelper
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

package net.silentchaos512.lib.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class ChatHelper {

  public static void sendMessage(EntityPlayer player, String message) {

    player.sendMessage(new TextComponentString(message));
  }

  public static void sendMessage(EntityPlayer player, ITextComponent component) {

    player.sendMessage(component);
  }

  public static void sendStatusMessage(EntityPlayer player, String message, boolean actionBar) {

    player.sendStatusMessage(new TextComponentString(message), actionBar);
  }

  public static void sendStatusMessage(EntityPlayer player, ITextComponent component, boolean actionBar) {

    player.sendStatusMessage(component, actionBar);
  }
}
