/*
 * Inspired by the Actually Additions booklet by Ellpeck.
 */

package net.silentchaos512.lib.guidebook;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public interface IGuideChapter {

  IGuidePage[] getAllPages();

  @SideOnly(Side.CLIENT)
  String getLocalizedName();

  @SideOnly(Side.CLIENT)
  String getLocalizedNameWithFormatting();

  IGuideEntry getEntry();

  @Nonnull
  ItemStack getDisplayItemStack();

  String getIdentifier();

  int getPageIndex(IGuidePage page);

  int getSortingPriority();
}
