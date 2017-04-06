/*
 * Inspired by the Actually Additions booklet by Ellpeck.
 */

package net.silentchaos512.lib.guidebook.entry;

import java.util.List;

import net.silentchaos512.lib.guidebook.GuideBook;
import net.silentchaos512.lib.guidebook.IGuideChapter;

public class GuideEntryAllItems extends GuideEntry {

  public GuideEntryAllItems(GuideBook book, String identifier) {

    super(book, identifier, -Integer.MAX_VALUE);
  }

  @Override
  public void addChapter(IGuideChapter chapter) {

  }

  @Override
  public List<IGuideChapter> getAllChapters() {

    return book.getChapters();
  }
}
