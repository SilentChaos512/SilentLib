package net.silentchaos512.lib.util;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.silentchaos512.lib.collection.ItemStackList;

public class PlayerHelper {

  /** @deprecated Use ChatHelper instead. */
  @Deprecated
  public static void addChatMessage(EntityPlayer player, String msg) {

    ChatHelper.sendMessage(player, msg);
  }

  /**
   * Gives the player an item. If it can't be given directly, it spawns an EntityItem. Spawns 1 block above player's
   * feet.
   */
  public static void giveItem(EntityPlayer player, ItemStack stack) {

    giveItem(player, stack, player.posX, player.posY + 1.0, player.posZ);
  }

  /**
   * Gives the player an item. If it can't be given directly, it spawns an EntityItem. This version allows an exact
   * position to be given.
   */
  public static void giveItem(EntityPlayer player, ItemStack stack, double posX, double posY,
      double posZ) {

    ItemStack copy = StackHelper.safeCopy(stack);
    if (!player.inventory.addItemStackToInventory(copy)) {
      EntityItem entityItem = new EntityItem(player.world, posX, posY, posZ, copy);
      entityItem.setDefaultPickupDelay();
      player.world.spawnEntity(entityItem);
    }
  }

  /**
   * Removes the stack from the player's inventory, if it exists.
   */
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

  @Nonnull
  public static ItemStackList getNonEmptyStacks(EntityPlayer player) {

    return getNonEmptyStacks(player, true, true, true, s -> true);
  }

  @Nonnull
  public static ItemStackList getNonEmptyStacks(EntityPlayer player,
      Predicate<ItemStack> predicate) {

    return getNonEmptyStacks(player, true, true, true, predicate);
  }

  @Nonnull
  public static ItemStackList getNonEmptyStacks(EntityPlayer player, boolean includeMain,
      boolean includeOffHand, boolean includeArmor) {

    return getNonEmptyStacks(player, includeMain, includeOffHand, includeArmor, s -> true);
  }

  @Nonnull
  public static ItemStackList getNonEmptyStacks(EntityPlayer player, boolean includeMain,
      boolean includeOffHand, boolean includeArmor, Predicate<ItemStack> predicate) {

    ItemStackList list = ItemStackList.create();

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

  /**
   * Gets the first matching valid ItemStack in the players inventory.
   * 
   * @param player
   *          The player
   * @param includeMain
   *          Search the players main inventory (hotbar and the 3 rows above that, hotbar is first, I think).
   * @param includeOffHand
   *          Check the player's offhand slot as well.
   * @param includeArmor
   *          Check the player's armor slots as well.
   * @param predicate
   *          The condition to check for on the ItemStack.
   * @return The first ItemStack that matches the predicate, or ItemStack.EMPTY if there is no match. Search order is
   *         offHand, armor, main.
   * @since 2.3.1
   */
  @Nonnull
  public static ItemStack getFirstValidStack(EntityPlayer player, boolean includeMain,
      boolean includeOffHand, boolean includeArmor, Predicate<ItemStack> predicate) {

    if (includeOffHand)
      for (ItemStack stack : player.inventory.offHandInventory)
        if (!stack.isEmpty() && predicate.apply(stack))
          return stack;

    if (includeArmor)
      for (ItemStack stack : player.inventory.armorInventory)
        if (!stack.isEmpty() && predicate.apply(stack))
          return stack;

    if (includeMain)
      for (ItemStack stack : player.inventory.mainInventory)
        if (!stack.isEmpty() && predicate.apply(stack))
          return stack;

    return StackHelper.empty();
  }

  /** @deprecated Renamed to getNonEmptyStacks */
  @Deprecated
  public static NonNullList<ItemStack> getNonNullStacks(EntityPlayer player) {

    return getNonEmptyStacks(player);
  }

  /** @deprecated Renamed to getNonEmptyStacks */
  @Deprecated
  public static NonNullList<ItemStack> getNonNullStacks(EntityPlayer player,
      Predicate<ItemStack> predicate) {

    return getNonEmptyStacks(player, predicate);
  }

  /** @deprecated Renamed to getNonEmptyStacks */
  @Deprecated
  public static NonNullList<ItemStack> getNonNullStacks(EntityPlayer player, boolean includeMain,
      boolean includeOffHand, boolean includeArmor) {

    return getNonEmptyStacks(player, includeMain, includeOffHand, includeArmor);
  }

  /** @deprecated Renamed to getNonEmptyStacks */
  @Deprecated
  public static NonNullList<ItemStack> getNonNullStacks(EntityPlayer player, boolean includeMain,
      boolean includeOffHand, boolean includeArmor, Predicate<ItemStack> predicate) {

    return getNonEmptyStacks(player, includeMain, includeOffHand, includeArmor, predicate);
  }
}
