package net.silentchaos512.lib.util;

import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;
import net.silentchaos512.lib.SilentLib;

public class PlayerHelper {

  public static void addChatMessage(EntityPlayer player, String msg) {

    player.sendMessage(new TextComponentString(msg));
  }

  public static void giveItem(EntityPlayer player, ItemStack stack) {

    giveItem(player, stack, player.posX, player.posY + 1.0, player.posZ);
  }

  public static void giveItem(EntityPlayer player, ItemStack stack, double posX, double posY,
      double posZ) {

    EntityItem entityItem = new EntityItem(player.world, posX, posY, posZ, stack);
    entityItem.setDefaultPickupDelay();
    player.world.spawnEntity(entityItem);
  }

  public static void removeItem(EntityPlayer player, ItemStack stack) {

    List<NonNullList<ItemStack>> inventories = Lists.newArrayList(player.inventory.mainInventory,
        player.inventory.offHandInventory, player.inventory.armorInventory);

    for (NonNullList<ItemStack> inv : inventories) {
      for (int i = 0; i < inv.size(); ++i) {
        if (stack == inv.get(i)) {
          inv.set(i, ItemStack.EMPTY);
          return;
        }
      }
    }
  }

  static Predicate<ItemStack> predicateAny = new Predicate() {

    @Override
    public boolean apply(Object input) {

      return true;
    }
  };

  public static NonNullList<ItemStack> getNonEmptyStacks(EntityPlayer player) {

    return getNonEmptyStacks(player, true, true, true, predicateAny);
  }

  public static NonNullList<ItemStack> getNonEmptyStacks(EntityPlayer player,
      Predicate<ItemStack> predicate) {

    return getNonEmptyStacks(player, true, true, true, predicate);
  }

  public static NonNullList<ItemStack> getNonEmptyStacks(EntityPlayer player, boolean includeMain,
      boolean includeOffHand, boolean includeArmor) {

    return getNonEmptyStacks(player, includeMain, includeOffHand, includeArmor, predicateAny);
  }

  public static NonNullList<ItemStack> getNonEmptyStacks(EntityPlayer player, boolean includeMain,
      boolean includeOffHand, boolean includeArmor, Predicate<ItemStack> predicate) {

    NonNullList<ItemStack> list = NonNullList.create();

    if (includeMain)
      for (ItemStack stack : player.inventory.mainInventory)
        if (!stack.isEmpty() && predicate.apply(stack))
          list.add(stack);

    if (includeOffHand)
      for (ItemStack stack : player.inventory.offHandInventory)
        if (!stack.isEmpty() && predicate.apply(stack))
          list.add(stack);

    if (includeArmor)
      for (ItemStack stack : player.inventory.armorInventory)
        if (!stack.isEmpty() && predicate.apply(stack))
          list.add(stack);

    return list;
  }

  @Deprecated // Renamed to getNonEmptyStacks
  public static NonNullList<ItemStack> getNonNullStacks(EntityPlayer player) {

    return getNonEmptyStacks(player);
  }

  @Deprecated // Renamed to getNonEmptyStacks
  public static NonNullList<ItemStack> getNonNullStacks(EntityPlayer player,
      Predicate<ItemStack> predicate) {

    return getNonEmptyStacks(player, predicate);
  }

  @Deprecated // Renamed to getNonEmptyStacks
  public static NonNullList<ItemStack> getNonNullStacks(EntityPlayer player, boolean includeMain,
      boolean includeOffHand, boolean includeArmor) {

    return getNonEmptyStacks(player, includeMain, includeOffHand, includeArmor);
  }

  @Deprecated // Renamed to getNonEmptyStacks
  public static NonNullList<ItemStack> getNonNullStacks(EntityPlayer player, boolean includeMain,
      boolean includeOffHand, boolean includeArmor, Predicate<ItemStack> predicate) {

    return getNonEmptyStacks(player, includeMain, includeOffHand, includeArmor, predicate);
  }
}
