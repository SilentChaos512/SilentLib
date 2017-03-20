package net.silentchaos512.lib.util;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Contains version-safe helper methods for Items.
 * 
 * @author SilentChaos512
 *
 */
public class ItemHelper {

  public static ActionResult<ItemStack> onItemRightClick(@Nonnull Item item, World world,
      EntityPlayer player, EnumHand hand) {

    return item.onItemRightClick(player.getHeldItem(hand), world, player, hand);
  }

  public static EnumActionResult onItemUse(@Nonnull Item item, EntityPlayer player, World world,
      BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {

    return item.onItemUse(player.getHeldItem(hand), player, world, pos, hand, side, hitX, hitY,
        hitZ);
  }

  public static EnumActionResult onItemUseFirst(@Nonnull Item item, EntityPlayer player,
      World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ,
      EnumHand hand) {

    return item.onItemUseFirst(player.getHeldItem(hand), player, world, pos, side, hitX, hitY, hitZ,
        hand);
  }

  public static EnumActionResult useItemAsPlayer(ItemStack stack, EntityPlayer player, World world,
      BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {

    // No need to place item in player's hand in 1.10.2.
    return stack.getItem().onItemUse(stack, player, world, pos, EnumHand.MAIN_HAND, side, hitX, hitY, hitZ);
  }
}
