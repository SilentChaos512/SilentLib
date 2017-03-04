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
