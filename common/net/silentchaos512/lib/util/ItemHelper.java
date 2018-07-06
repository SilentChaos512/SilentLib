/*
 * SilentLib - ItemHelper
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

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * Contains version-safe helper methods for Items.
 *
 * @author SilentChaos512
 *
 */
public class ItemHelper {

  @Deprecated
  public static boolean attemptDamageItem(ItemStack stack, int amount, Random rand) {

    return attemptDamageItem(stack, amount, rand, null);
  }

  @Deprecated
  public static boolean attemptDamageItem(ItemStack stack, int amount, Random rand, @Nullable EntityPlayer player) {

    EntityPlayerMP playermp = player instanceof EntityPlayerMP ? (EntityPlayerMP) player : null;
    return stack.attemptDamageItem(amount, rand, playermp);
  }

  @Deprecated
  public static ActionResult<ItemStack> onItemRightClick(@Nonnull Item item, World world, EntityPlayer player, EnumHand hand) {

    return item.onItemRightClick(world, player, hand);
  }

  @Deprecated
  public static EnumActionResult onItemUse(@Nonnull Item item, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX,
      float hitY, float hitZ) {

    return item.onItemUse(player, world, pos, hand, side, hitX, hitY, hitZ);
  }

  @Deprecated
  public static EnumActionResult onItemUseFirst(@Nonnull Item item, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY,
      float hitZ, EnumHand hand) {

    return item.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
  }

  public static EnumActionResult useItemAsPlayer(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY,
      float hitZ) {

    // Temporarily move stack to the player's offhand to allow it to be used.
    ItemStack currentOffhand = player.getHeldItemOffhand();
    player.setHeldItem(EnumHand.OFF_HAND, stack);

    // Use the item.
    Item item = stack.getItem();
    EnumActionResult result;
    result = stack.getItem().onItemUse(player, world, pos, EnumHand.OFF_HAND, side, hitX, hitY, hitZ);

    // Put everything back in its proper place...
    player.setHeldItem(EnumHand.OFF_HAND, currentOffhand);
    return result;
  }

  @Deprecated
  public static boolean isInCreativeTab(Item item, CreativeTabs targetTab) {

    for (CreativeTabs tab : item.getCreativeTabs())
      if (tab == targetTab)
        return true;
    CreativeTabs creativetabs = item.getCreativeTab();
    return creativetabs != null && (targetTab == CreativeTabs.SEARCH || targetTab == creativetabs);
  }
}
