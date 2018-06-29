/*
 * Inspired by the Actually Additions booklet by Ellpeck.
 */

package net.silentchaos512.lib.guidebook.chapter;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.guidebook.GuideBook;
import net.silentchaos512.lib.guidebook.IGuideChapter;
import net.silentchaos512.lib.guidebook.IGuideEntry;
import net.silentchaos512.lib.guidebook.IGuidePage;

import javax.annotation.Nonnull;

public class GuideChapter implements IGuideChapter {

  public final IGuidePage[] pages;
  public final IGuideEntry entry;
  public final ItemStack displayStack;
  private final GuideBook book;
  private final String identifier;
  private final int priority;
  public TextFormatting color;

  public GuideChapter(GuideBook book, String identifier, IGuideEntry entry,
      ItemStack displayStack, IGuidePage... pages) {

    this(book, identifier, entry, displayStack, 0, pages);
  }

  public GuideChapter(GuideBook book, String identifier, IGuideEntry entry,
      ItemStack displayStack, int priority, IGuidePage... pages) {

    this.book = book;
    this.pages = pages;
    this.identifier = identifier;
    this.entry = entry;
    this.displayStack = displayStack;
    this.priority = priority;
    this.color = TextFormatting.RESET;

    this.entry.addChapter(this);
    for (IGuidePage page : this.pages) {
      page.setChapter(this);
    }
  }

  @Override
  public IGuidePage[] getAllPages() {

    return this.pages;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public String getLocalizedName() {

    return book.loc.getLocalizedString("guide", "chapter." + this.getIdentifier() + ".name");
  }

  @Override
  @SideOnly(Side.CLIENT)
  public String getLocalizedNameWithFormatting() {

    return this.color + this.getLocalizedName();
  }

  @Override
  public IGuideEntry getEntry() {

    return this.entry;
  }

  @Nonnull
  @Override
  public ItemStack getDisplayItemStack() {

    return this.displayStack;
  }

  @Override
  public String getIdentifier() {

    return this.identifier;
  }

  @Override
  public int getPageIndex(IGuidePage page) {

    for (int i = 0; i < this.pages.length; i++) {
      if (this.pages[i] == page) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public int getSortingPriority() {

    return this.priority;
  }

  public GuideChapter setImportant() {

    this.color = TextFormatting.DARK_GREEN;
    return this;
  }

  public GuideChapter setSpecial() {

    this.color = TextFormatting.DARK_PURPLE;
    return this;
  }
}
