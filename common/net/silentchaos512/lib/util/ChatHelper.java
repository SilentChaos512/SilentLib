package net.silentchaos512.lib.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class ChatHelper {

  public static void sendMessage(EntityPlayer player, String message) {

    player.addChatMessage(new TextComponentString(message));
  }

  public static void sendMessage(EntityPlayer player, ITextComponent component) {

    player.addChatMessage(component);
  }

  public static void sendStatusMessage(EntityPlayer player, String message, boolean actionBar) {

    sendMessage(player, new TextComponentString(message));
  }

  public static void sendStatusMessage(EntityPlayer player, ITextComponent component, boolean actionBar) {

    sendMessage(player, component);
  }
}
