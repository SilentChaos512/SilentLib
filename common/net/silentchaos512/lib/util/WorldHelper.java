package net.silentchaos512.lib.util;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldHelper {

  public static boolean mayPlace(World world, Block block, BlockPos pos, boolean ignoreBB,
      EnumFacing side, @Nullable Entity entity, ItemStack stack) {

    return world.canBlockBePlaced(block, pos, ignoreBB, side, entity, stack);
  }
}
