/*
 * Inspired by the Actually Additions booklet by Ellpeck.
 */

package net.silentchaos512.lib.guidebook.gui;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.guidebook.GuideBook;
import net.silentchaos512.lib.guidebook.IGuideChapter;
import net.silentchaos512.lib.guidebook.IGuideEntry;
import net.silentchaos512.lib.guidebook.IGuidePage;
import net.silentchaos512.lib.guidebook.button.EntryButton;
import net.silentchaos512.lib.guidebook.internal.GuiGuideBase;
import net.silentchaos512.lib.guidebook.misc.GuideBookUtils;

@SideOnly(Side.CLIENT)
public class GuiEntry extends GuiGuide {

  // The page in the entry. Say you have 2 more chapters than fit on one double page, then those 2 would be displayed on
  // entryPage 1 instead.
  private final int entryPage;
  private final int pageAmount;
  private final IGuideEntry entry;
  private final List<IGuideChapter> chapters;
  private final String searchText;
  private final boolean focusSearch;
  protected GuideBook book;

  public GuiEntry(GuideBook book, GuiScreen previousScreen, GuiGuideBase parentPage,
      IGuideEntry entry, int entryPage, String search, boolean focusSearch) {

    super(book, previousScreen, parentPage);
    this.entryPage = entryPage;
    this.entry = entry;
    this.searchText = search;
    this.focusSearch = focusSearch;
    this.chapters = entry.getChaptersForDisplay(search);

    if (!this.chapters.isEmpty()) {
      IGuideChapter lastChap = this.chapters.get(this.chapters.size() - 1);
      this.pageAmount = lastChap == null ? 1
          : calcEntryPage(this.entry, lastChap, this.searchText) + 1;
    } else {
      this.pageAmount = 1;
    }
  }

  public GuiEntry(GuideBook book, GuiScreen previousScreen, GuiGuideBase parentPage,
      IGuideEntry entry, IGuideChapter chapterForPageCalc, String search, boolean focusSearch) {

    this(book, previousScreen, parentPage, entry, calcEntryPage(entry, chapterForPageCalc, search),
        search, focusSearch);
  }

  private static int calcEntryPage(IGuideEntry entry, IGuideChapter chapterForPageCalc,
      String search) {

    int index = entry.getChaptersForDisplay(search).indexOf(chapterForPageCalc);
    return index / (BUTTONS_PER_PAGE * 2);
  }

  @Override
  public void drawScreenPre(int mouseX, int mouseY, float partialTicks) {

    super.drawScreenPre(mouseX, mouseY, partialTicks);

    String name = this.entry.getLocalizedName();
    this.fontRendererObj.drawString(name,
        this.guiLeft + this.xSize / 2 - this.fontRendererObj.getStringWidth(name) / 2,
        this.guiTop - 1, 0xFFFFFF, true);

    for (int i = 0; i < 2; i++) {
      String pageStrg = "Page " + (this.entryPage * 2 + i + 1) + "/" + this.pageAmount * 2;
      this.renderScaledAsciiString(pageStrg, this.guiLeft + 25 + i * 136,
          this.guiTop + this.ySize - 7, 0xFFFFFF, false, this.getLargeFontSize());
    }
  }

  @Override
  public void initGui() {

    super.initGui();

    if (this.hasSearchBar() && this.searchText != null) {
      this.searchField.setText(this.searchText);
      if (this.focusSearch) {
        this.searchField.setFocused(true);
      }
    }

    int idOffset = this.entryPage * (BUTTONS_PER_PAGE * 2);
    for (int x = 0; x < 2; x++) {
      for (int y = 0; y < BUTTONS_PER_PAGE; y++) {
        int id = y + x * BUTTONS_PER_PAGE;
        if (this.chapters.size() > id + idOffset) {
          IGuideChapter chapter = this.chapters.get(id + idOffset);
          this.buttonList.add(
              new EntryButton(this, id, this.guiLeft + 14 + x * 142, this.guiTop + 11 + y * 13, 115,
                  10, chapter.getLocalizedNameWithFormatting(), chapter.getDisplayItemStack()));
        } else {
          return;
        }
      }
    }
  }

  @Override
  protected void actionPerformed(GuiButton button) throws IOException {

    if (button instanceof EntryButton) {
      int actualId = button.id + this.entryPage * (BUTTONS_PER_PAGE * 2);

      if (this.chapters.size() > actualId) {
        IGuideChapter chapter = this.chapters.get(actualId);
        if (chapter != null) {
          IGuidePage[] pages = chapter.getAllPages();
          if (pages != null && pages.length > 0) {
            this.mc.displayGuiScreen(
                GuideBookUtils.createPageGui(book, this.previousScreen, this, pages[0]));
          }
        }
      }
    } else {
      super.actionPerformed(button);
    }
  }

  @Override
  public void addOrModifyItemRenderer(ItemStack renderedStack, int x, int y, float scale,
      boolean shouldTryTransfer) {

  }

  @Override
  public boolean hasPageLeftButton() {

    return this.entryPage > 0;
  }

  @Override
  public void onPageLeftButtonPressed() {

    this.mc.displayGuiScreen(new GuiEntry(book, this.previousScreen, this.parentPage, this.entry,
        this.entryPage - 1, this.searchText, this.searchField.isFocused()));
  }

  @Override
  public boolean hasPageRightButton() {

    return !this.chapters.isEmpty() && this.entryPage < this.pageAmount - 1;
  }

  @Override
  public void onPageRightButtonPressed() {

    this.mc.displayGuiScreen(new GuiEntry(book, this.previousScreen, this.parentPage, this.entry,
        this.entryPage + 1, this.searchText, this.searchField.isFocused()));
  }

  @Override
  public boolean hasBackButton() {

    return true;
  }

  @Override
  public void onBackButtonPressed() {

    if (!isShiftKeyDown()) {
      this.mc.displayGuiScreen(this.parentPage);
    } else {
      super.onBackButtonPressed();
    }
  }
}
