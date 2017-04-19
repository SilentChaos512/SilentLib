/*
 * Inspired by the Actually Additions booklet by Ellpeck.
 */

package net.silentchaos512.lib.guidebook;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public interface IGuideEntry {

  List<IGuideChapter> getAllChapters();

  String getIdentifier();

  @SideOnly(Side.CLIENT)
  String getLocalizedName();

  @SideOnly(Side.CLIENT)
  String getLocalizedNameWithFormatting();

  void addChapter(IGuideChapter chapter);

  @SideOnly(Side.CLIENT)
  List<IGuideChapter> getChaptersForDisplay(String searchBarText);

  int getSortingPriority();

  @SideOnly(Side.CLIENT)
  boolean visibleOnFrontPage();
}
