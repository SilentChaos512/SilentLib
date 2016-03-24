package net.silentchaos512.lib.util;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;

public class PlayerHelper {

  public static void addChatMessage(EntityPlayer player, String msg) {

    player.addChatMessage(new TextComponentString(msg));
  }

  public static void giveItem(EntityPlayer player, ItemStack stack) {

    giveItem(player, stack, player.posX, player.posY + 1.0, player.posZ);
  }

  public static void giveItem(EntityPlayer player, ItemStack stack, double posX, double posY,
      double posZ) {

    EntityItem entityItem = new EntityItem(player.worldObj, posX, posY, posZ, stack);
    player.worldObj.spawnEntityInWorld(entityItem);
  }

  public static void removeItem(EntityPlayer player, ItemStack stack) {

    for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
      if (stack == player.inventory.mainInventory[i]) {
        player.inventory.mainInventory[i] = null;
        return;
      }
    }

    for (int i = 0; i < player.inventory.offHandInventory.length; ++i) {
      if (stack == player.inventory.offHandInventory[i]) {
        player.inventory.offHandInventory[i] = null;
        return;
      }
    }

    for (int i = 0; i < player.inventory.armorInventory.length; ++i) {
      if (stack == player.inventory.armorInventory[i]) {
        player.inventory.armorInventory[i] = null;
        return;
      }
    }
  }

  public static List<ItemStack> getNonNullStacks(EntityPlayer player) {

    return getNonNullStacks(player, true, true, true);
  }

  public static List<ItemStack> getNonNullStacks(EntityPlayer player, boolean includeMain,
      boolean includeOffHand, boolean includeArmor) {

    List<ItemStack> list = Lists.newArrayList();

    if (includeMain)
      for (ItemStack stack : player.inventory.mainInventory)
        if (stack != null)
          list.add(stack);

    if (includeOffHand)
      for (ItemStack stack : player.inventory.offHandInventory)
        if (stack != null)
          list.add(stack);

    if (includeArmor)
      for (ItemStack stack : player.inventory.armorInventory)
        if (stack != null)
          list.add(stack);

    return list;
  }
}
