/*
 * Inspired by the Actually Additions booklet by Ellpeck.
 */

package net.silentchaos512.lib.guidebook;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.guidebook.entry.GuideEntry;
import net.silentchaos512.lib.guidebook.entry.GuideEntryAllItems;
import net.silentchaos512.lib.guidebook.gui.GuiGuide;
import net.silentchaos512.lib.util.LocalizationHelper;
import net.silentchaos512.lib.util.LogHelper;

public abstract class GuideBook {

  protected final String modId;
  protected final List<IGuideEntry> entries = Lists.newArrayList();
  protected final List<IGuideChapter> chapters = Lists.newArrayList();
  protected final List<IGuidePage> pagesWithItemOrFluidData = Lists.newArrayList();
  protected ResourceLocation resourceGui;
  protected ResourceLocation resourceGadgets;
  public GuideEntry entryAllAndSearch = new GuideEntryAllItems(this, "allAndSearch").setImportant();
  public final LocalizationHelper loc;
  public int edition = -1;

  @SideOnly(Side.CLIENT)
  public GuiGuide lastViewedPage;

  public GuideBook(String modId) {

    this.modId = modId;
    this.resourceGui = new ResourceLocation(SilentLib.instance.MOD_ID,
        "textures/gui/gui_guide.png");
    this.resourceGadgets = new ResourceLocation(SilentLib.instance.MOD_ID,
        "textures/gui/gui_guide_gadgets.png");
    LocalizationHelper locForMod = SilentLib.instance.getLocalizationHelperForMod(modId);

    // Does the mod have a registered localization helper? If not, just create one.
    if (locForMod == null) {
      SilentLib.logHelper.warning(String.format(
          "<Guide Book> Mod \"%s\" has no localization helper! A new one will be created, but not registered for use with blocks/items.",
          modId));
      loc = new LocalizationHelper(modId).setReplaceAmpersand(true);
    } else {
      loc = locForMod;
    }
  }

  public void preInit() {

    initEntries();
  }

  public void postInit() {

    initChapters();

    int countChapter = 0;
    int countPage = 0;
    int countInfo = 0;

    for (IGuideEntry entry : entries) {
      for (IGuideChapter chapter : entry.getAllChapters()) {
        if (!chapters.contains(chapter)) {
          chapters.add(chapter);
          ++countChapter;

          for (IGuidePage page : chapter.getAllPages()) {
            ++countPage;

            List<ItemStack> items = Lists.newArrayList();
            page.getItemStacksForPage(items);
            List<FluidStack> fluids = Lists.newArrayList();
            page.getFluidStacksForPage(fluids);

            if ((items != null && !items.isEmpty()) || (fluids != null && !fluids.isEmpty())) {
              if (!pagesWithItemOrFluidData.contains(page)) {
                pagesWithItemOrFluidData.add(page);
                countInfo++;
              }
            }
          }
        }
      }
    }

    Collections.sort(entries, new Comparator<IGuideEntry>() {

      @Override
      public int compare(IGuideEntry entry1, IGuideEntry entry2) {

        Integer p1 = entry1.getSortingPriority();
        Integer p2 = entry2.getSortingPriority();
        return p2.compareTo(p1);
      }
    });
    Collections.sort(chapters, new Comparator<IGuideChapter>() {

      @Override
      public int compare(IGuideChapter chapter1, IGuideChapter chapter2) {

        Integer p1 = chapter1.getSortingPriority();
        Integer p2 = chapter2.getSortingPriority();
        return p2.compareTo(p1);
      }
    });
    Collections.sort(pagesWithItemOrFluidData, new Comparator<IGuidePage>() {

      @Override
      public int compare(IGuidePage page1, IGuidePage page2) {

        Integer p1 = page1.getSortingPriority();
        Integer p2 = page2.getSortingPriority();
        return p2.compareTo(p1);
      }
    });

    LogHelper log = SilentLib.logHelper;
    log.info(String.format("Guide book for mod \"%s\" initialized!", modId));
    log.info("    Entries:  " + entries.size());
    log.info("    Chapters: " + countChapter);
    log.info("    Pages:    " + countPage);
    log.info("    Info:     " + countInfo);
  }

  public abstract void initEntries();

  public abstract void initChapters();

  public String getModId() {

    return modId;
  }

  public List<IGuideChapter> getChapters() {

    return chapters;
  }

  public List<IGuideEntry> getEntries() {

    return entries;
  }

  public List<IGuidePage> getPagesWithItemOrFluidData() {

    return pagesWithItemOrFluidData;
  }

  public void addEntry(IGuideEntry entry) {

    entries.add(entry);
  }

  public ResourceLocation getResourceGui() {

    return resourceGui;
  }

  public ResourceLocation getResourceGadgets() {

    return resourceGadgets;
  }
}
