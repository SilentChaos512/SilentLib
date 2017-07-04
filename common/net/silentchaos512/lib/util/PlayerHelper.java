package net.silentchaos512.lib.util;

import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.silentchaos512.lib.collection.ItemStackList;

public class PlayerHelper {

  /** @deprecated Use ChatHelper instead. */
  @Deprecated
  public static void addChatMessage(EntityPlayer player, String msg) {

    ChatHelper.sendMessage(player, msg);
  }

  /**
   * Gives the player an item. Currently, this just spawns an EntityItem on the player in all cases, since that seems to
   * be the most reliable method. Spawns 1 block above player's feet.
   */
  public static void giveItem(EntityPlayer player, ItemStack stack) {

    giveItem(player, stack, player.posX, player.posY + 1.0, player.posZ);
  }

  /**
   * Gives the player an item. Currently, this just spawns an EntityItem on the player in all cases, since that seems to
   * be the most reliable method. This version allows an exact position to be given.
   */
  public static void giveItem(EntityPlayer player, ItemStack stack, double posX, double posY,
      double posZ) {

    EntityItem entityItem = new EntityItem(player.world, posX, posY, posZ, stack);
    entityItem.setDefaultPickupDelay();
    player.world.spawnEntity(entityItem);
  }

  /**
   * Removes the stack from the player's inventory, if it exists.
   */
  public static void removeItem(EntityPlayer player, ItemStack stack) {

    List<ItemStack[]> inventories = Lists.newArrayList(player.inventory.mainInventory,
        player.inventory.offHandInventory, player.inventory.armorInventory);

    for (ItemStack[] inv : inventories) {
      for (int i = 0; i < inv.length; ++i) {
        if (stack == inv[i]) {
          inv[i] = StackHelper.empty();
          return;
        }
      }
    }
  }

  public static ItemStackList getNonEmptyStacks(EntityPlayer player) {

    return getNonEmptyStacks(player, true, true, true, s -> true);
  }

  public static ItemStackList getNonEmptyStacks(EntityPlayer player,
      Predicate<ItemStack> predicate) {

    return getNonEmptyStacks(player, true, true, true, predicate);
  }

  public static ItemStackList getNonEmptyStacks(EntityPlayer player, boolean includeMain,
      boolean includeOffHand, boolean includeArmor) {

    return getNonEmptyStacks(player, includeMain, includeOffHand, includeArmor, s -> true);
  }

  public static ItemStackList getNonEmptyStacks(EntityPlayer player, boolean includeMain,
      boolean includeOffHand, boolean includeArmor, Predicate<ItemStack> predicate) {

    ItemStackList list = ItemStackList.create();

    if (includeMain)
      for (ItemStack stack : player.inventory.mainInventory)
        if (StackHelper.isValid(stack) && predicate.apply(stack))
          list.add(stack);

    if (includeOffHand)
      for (ItemStack stack : player.inventory.offHandInventory)
        if (StackHelper.isValid(stack) && predicate.apply(stack))
          list.add(stack);

    if (includeArmor)
      for (ItemStack stack : player.inventory.armorInventory)
        if (StackHelper.isValid(stack) && predicate.apply(stack))
          list.add(stack);

    return list;
  }

  /** @deprecated Renamed to getNonEmptyStacks */
  @Deprecated
  public static ItemStackList getNonNullStacks(EntityPlayer player) {

    return getNonEmptyStacks(player);
  }

  /** @deprecated Renamed to getNonEmptyStacks */
  @Deprecated
  public static ItemStackList getNonNullStacks(EntityPlayer player,
      Predicate<ItemStack> predicate) {

    return getNonEmptyStacks(player, predicate);
  }

  /** @deprecated Renamed to getNonEmptyStacks */
  @Deprecated
  public static ItemStackList getNonNullStacks(EntityPlayer player, boolean includeMain,
      boolean includeOffHand, boolean includeArmor) {

    return getNonEmptyStacks(player, includeMain, includeOffHand, includeArmor);
  }

  /** @deprecated Renamed to getNonEmptyStacks */
  @Deprecated
  public static ItemStackList getNonNullStacks(EntityPlayer player, boolean includeMain,
      boolean includeOffHand, boolean includeArmor, Predicate<ItemStack> predicate) {

    return getNonEmptyStacks(player, includeMain, includeOffHand, includeArmor, predicate);
  }
}
