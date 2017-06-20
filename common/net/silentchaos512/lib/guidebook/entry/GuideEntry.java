/*
 * Inspired by the Actually Additions booklet by Ellpeck.
 */

package net.silentchaos512.lib.guidebook.entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.guidebook.GuideBook;
import net.silentchaos512.lib.guidebook.IGuideChapter;
import net.silentchaos512.lib.guidebook.IGuideEntry;
import net.silentchaos512.lib.guidebook.IGuidePage;
import net.silentchaos512.lib.util.StackHelper;

public class GuideEntry implements IGuideEntry {

  private final String identifier;
  private final int priority;
  private final List<IGuideChapter> chapters = new ArrayList<IGuideChapter>();
  private TextFormatting color;
  protected final GuideBook book;

  public GuideEntry(GuideBook book, String key) {
    this(book, key, 0);
  }

  public GuideEntry(GuideBook book, String key, int priority) {
    this.identifier = key;
    this.priority = priority;
    this.book = book;
    book.addEntry(this);

    this.color = TextFormatting.RESET;
  }

  @SideOnly(Side.CLIENT)
  private static boolean fitsFilter(IGuidePage page, String searchBarText) {

    Minecraft mc = Minecraft.getMinecraft();

    List<ItemStack> items = new ArrayList<ItemStack>();
    page.getItemStacksForPage(items);
    if (!items.isEmpty()) {
      for (ItemStack stack : items) {
        if (StackHelper.isValid(stack)) {
          ITooltipFlag tooltipFlag = mc.gameSettings.advancedItemTooltips ? TooltipFlags.ADVANCED : TooltipFlags.NORMAL;
          List<String> tooltip = stack.getTooltip(mc.player, tooltipFlag);
          for (String strg : tooltip) {
            if (strg != null && strg.toLowerCase(Locale.ROOT).contains(searchBarText)) {
              return true;
            }
          }
        }
      }
    }

    List<FluidStack> fluids = new ArrayList<FluidStack>();
    page.getFluidStacksForPage(fluids);
    if (!fluids.isEmpty()) {
      for (FluidStack stack : fluids) {
        if (stack != null) {
          String strg = stack.getLocalizedName();
          if (strg != null && strg.toLowerCase(Locale.ROOT).contains(searchBarText)) {
            return true;
          }
        }
      }
    }

    return false;
  }

  @Override
  public List<IGuideChapter> getAllChapters() {

    return this.chapters;
  }

  @Override
  public String getIdentifier() {

    return this.identifier;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public String getLocalizedName() {

    return book.loc.getLocalizedString("guide", "indexEntry." + this.getIdentifier() + ".name");
  }

  @Override
  @SideOnly(Side.CLIENT)
  public String getLocalizedNameWithFormatting() {

    return this.color + this.getLocalizedName();
  }

  @Override
  public void addChapter(IGuideChapter chapter) {

    this.chapters.add(chapter);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public List<IGuideChapter> getChaptersForDisplay(String searchBarText) {

    if (searchBarText != null && !searchBarText.isEmpty()) {
      String search = searchBarText.toLowerCase(Locale.ROOT);

      List<IGuideChapter> fittingChapters = new ArrayList<IGuideChapter>();
      for (IGuideChapter chapter : this.getAllChapters()) {
        if (chapter.getLocalizedName().toLowerCase(Locale.ROOT).contains(search)) {
          fittingChapters.add(chapter);
        } else {
          for (IGuidePage page : chapter.getAllPages()) {
            if (fitsFilter(page, search)) {
              fittingChapters.add(chapter);
              break;
            }
          }
        }
      }

      return fittingChapters;
    } else {
      return this.getAllChapters();
    }
  }

  @Override
  public int getSortingPriority() {

    return this.priority;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean visibleOnFrontPage() {

    return true;
  }

  public GuideEntry setImportant() {

    this.color = TextFormatting.DARK_GREEN;
    return this;
  }

  public GuideEntry setSpecial() {

    this.color = TextFormatting.DARK_PURPLE;
    return this;
  }

}
