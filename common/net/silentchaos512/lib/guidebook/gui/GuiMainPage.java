/*
 * Inspired by the Actually Additions booklet by Ellpeck.
 */

package net.silentchaos512.lib.guidebook.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.gui.TexturedButton;
import net.silentchaos512.lib.guidebook.GuideBook;
import net.silentchaos512.lib.guidebook.IGuideEntry;
import net.silentchaos512.lib.guidebook.button.EntryButton;

@SideOnly(Side.CLIENT)
public class GuiMainPage extends GuiGuide {

  // private static final String[] QUOTES = new String[] {};

  private TexturedButton achievementButton;
  private TexturedButton configButton;

  private GuiButton tutorialButton;
  private boolean showTutorial;

  private String bookletName;
  private String bookletEdition;

  private List<String> quote;
  private String quoteGuy;

  public GuiMainPage(GuideBook book, GuiScreen previousScreen) {

    super(book, previousScreen, null);
  }

  private List<IGuideEntry> getDisplayedEntries() {

    List<IGuideEntry> displayed = new ArrayList<IGuideEntry>();

    for (IGuideEntry entry : book.getEntries()) {
      if (entry.visibleOnFrontPage()) {
        displayed.add(entry);
      }
    }

    return displayed;
  }

  @Override
  public void initGui() {

    super.initGui();

    this.bookletName = "guide." + book.getModId() + ":manualName.1";

    // String usedQuote = QUOTES[this.mc.world.rand.nextInt(QUOTES.length)];
    // String[] quoteSplit = usedQuote.split("@");
    // if (quoteSplit.length == 2) {
    // this.quote = this.fontRendererObj.listFormattedStringToWidth(quoteSplit[0], 120);
    // this.quoteGuy = quoteSplit[1];
    // }

    String playerName = this.mc.player.getName();
    if (playerName.equalsIgnoreCase("derp")) {
      this.bookletEdition = "derp edition";
    } else {
      int mod10 = book.edition % 10;
      String str = book.edition
          + (mod10 == 1 ? "st" : mod10 == 2 ? "nd" : mod10 == 3 ? "rd" : "th");
      this.bookletEdition = book.loc.getLocalizedString("guide.silentlib:edition", (Object) str);
    }

    List<String> configText = new ArrayList<String>();
    configText.add(TextFormatting.GOLD + book.loc.getLocalizedString("guide", "configButton.name"));
    configText.addAll(this.fontRendererObj.listFormattedStringToWidth(
        book.loc.getLocalizedString("guide", "configButton.desc", book.getModId())
            .replaceAll("\\\\n", "\n"),
        200));
    this.configButton = new TexturedButton(book.getResourceGadgets(), -388, this.guiLeft + 16,
        this.guiTop + this.ySize - 30, 188, 14, 16, 16, configText);
    this.buttonList.add(this.configButton);

    List<String> achievementText = new ArrayList<String>();
    achievementText
        .add(TextFormatting.GOLD + book.loc.getLocalizedString("guide", "achievementButton.name"));
    achievementText.addAll(this.fontRendererObj.listFormattedStringToWidth(
        book.loc.getLocalizedString("booklet", "achievementButton.desc", book.getModId()), 200));
    this.achievementButton = new TexturedButton(book.getResourceGadgets(), -389, this.guiLeft + 36,
        this.guiTop + this.ySize - 30, 204, 14, 16, 16, achievementText);
    this.buttonList.add(this.achievementButton);

    // FIXME?
    // PlayerSave data = PlayerData.getDataFromPlayer(this.mc.player);
    // if (!data.didBookTutorial) {
    // this.showTutorial = true;
    //
    // this.tutorialButton = new GuiButton(666666, this.guiLeft + 140 / 2 - 50, this.guiTop + 146,
    // 100, 20, "Please click me <3");
    // this.buttonList.add(this.tutorialButton);
    //
    // this.configButton.visible = false;
    // this.achievementButton.visible = false;
    // }

    for (int i = 0; i < BUTTONS_PER_PAGE; i++) {
      List<IGuideEntry> displayed = getDisplayedEntries();
      if (displayed.size() > i) {
        IGuideEntry entry = displayed.get(i);
        this.buttonList.add(new EntryButton(this, i, this.guiLeft + 156, this.guiTop + 11 + i * 13,
            115, 10, "- " + entry.getLocalizedNameWithFormatting(), null));
      } else {
        return;
      }
    }
  }

  @Override
  protected void actionPerformed(GuiButton button) throws IOException {

    if (button instanceof EntryButton) {
      List<IGuideEntry> displayed = getDisplayedEntries();
      if (displayed.size() > button.id) {
        IGuideEntry entry = displayed.get(button.id);
        if (entry != null) {
          this.mc
              .displayGuiScreen(new GuiEntry(book, this.previousScreen, this, entry, 0, "", false));
        }
      }
    } else if (button == this.achievementButton) {
      // FIXME
      // GuiScreen achievements = new GuiAAAchievements(this, this.mc.player.getStatFileWriter());
      // this.mc.displayGuiScreen(achievements);
    } else if (button == this.configButton) {
      // FIXME
      // GuiScreen config = new GuiConfiguration(this);
      // this.mc.displayGuiScreen(config);
    } else if (this.showTutorial && button == this.tutorialButton) {
      if (this.hasBookmarkButtons()) {
        if (!isShiftKeyDown()) {
          // for (int i = 0; i < InitBooklet.chaptersIntroduction.length; i++) {
          // this.bookmarkButtons[i].assignedPage = InitBooklet.chaptersIntroduction[i]
          // .getAllPages()[0];
          // }
        }
        this.showTutorial = false;
        this.tutorialButton.visible = false;

        this.configButton.visible = true;
        this.achievementButton.visible = true;

        // FIXME?
        // PlayerSave data = PlayerData.getDataFromPlayer(this.mc.player);
        // data.didBookTutorial = true;
        // PacketHandlerHelper.sendPlayerDataToServer(false, 1);
      }
    } else {
      super.actionPerformed(button);
    }
  }

  @Override
  public void drawScreenPre(int mouseX, int mouseY, float partialTicks) {

    super.drawScreenPre(mouseX, mouseY, partialTicks);

    String strg = TextFormatting.DARK_GREEN + book.loc.getLocalizedString(this.bookletName);
    this.fontRendererObj.drawString(strg,
        this.guiLeft + 72 - this.fontRendererObj.getStringWidth(strg) / 2 - 3, this.guiTop + 19, 0);
    strg = TextFormatting.DARK_GREEN + book.loc.getLocalizedString("guide", "manualName.2");
    this.fontRendererObj.drawString(strg,
        this.guiLeft + 72 - this.fontRendererObj.getStringWidth(strg) / 2 - 3,
        this.guiTop + 19 + this.fontRendererObj.FONT_HEIGHT, 0);

    strg = TextFormatting.GOLD + TextFormatting.ITALIC.toString() + this.bookletEdition;
    this.fontRendererObj.drawString(strg,
        this.guiLeft + 72 - this.fontRendererObj.getStringWidth(strg) / 2 - 3, this.guiTop + 40, 0);

    if (this.showTutorial) {
      String text = TextFormatting.BLUE
          + "It looks like this is the first time you are using this manual. \nIf you click the button below, some useful bookmarks will be stored at the bottom of the GUI. You should definitely check them out to get started with "
          + book.getModId() + "! \nIf you don't want this, shift-click the button.";
      this.renderSplitScaledAsciiString(text, this.guiLeft + 11, this.guiTop + 55, 0, false,
          this.getMediumFontSize(), 120);
    } else if (this.quote != null && !this.quote.isEmpty() && this.quoteGuy != null) {
      int quoteSize = this.quote.size();

      for (int i = 0; i < quoteSize; i++) {
        this.renderScaledAsciiString(TextFormatting.ITALIC + this.quote.get(i), this.guiLeft + 25,
            this.guiTop + 90 + (i * 8), 0, false, this.getMediumFontSize());
      }
      this.renderScaledAsciiString("- " + this.quoteGuy, this.guiLeft + 60,
          this.guiTop + 93 + quoteSize * 8, 0, false, this.getLargeFontSize());
    }
  }

  @Override
  public void addOrModifyItemRenderer(ItemStack renderedStack, int x, int y, float scale,
      boolean shouldTryTransfer) {

  }
}
