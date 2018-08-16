/*
 * Inspired by the Actually Additions booklet by Ellpeck.
 */

package net.silentchaos512.lib.guidebook;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.guidebook.entry.GuideEntry;
import net.silentchaos512.lib.guidebook.entry.GuideEntryAllItems;
import net.silentchaos512.lib.guidebook.gui.GuiGuide;
import net.silentchaos512.lib.util.I18nHelper;
import net.silentchaos512.lib.util.LogHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Holds all the unique data for your guide book. Extend this class and create an instance of
 * ItemGuideBookSL to add a guide book for your mod. Call your GuideBook's preInit and postInit
 * methods during your pre/post-init phases. A single mod can have multiple guide books, in theory.
 *
 * @author SilentChaos512
 * @since 2.1.0
 */
public abstract class GuideBook {
    protected final String modId;
    protected final List<IGuideEntry> entries = Lists.newArrayList();
    protected final List<IGuideChapter> chapters = Lists.newArrayList();
    protected final List<IGuidePage> pagesWithItemOrFluidData = Lists.newArrayList();
    protected ResourceLocation resourceGui;
    protected ResourceLocation resourceGadgets;
    public GuideEntry entryAllAndSearch = new GuideEntryAllItems(this, "allAndSearch").setImportant();
    public final I18nHelper i18n;
    public int edition = -1;
    public boolean showPatreonButton = false;

    @SideOnly(Side.CLIENT)
    public GuiGuide lastViewedPage;

    public GuideBook(String modId, I18nHelper i18n) {
        this.modId = modId;
        this.i18n = i18n;
        this.showPatreonButton = modId.equals("silentgems");
        this.resourceGui = new ResourceLocation(SilentLib.MOD_ID, "textures/gui/gui_guide.png");
        this.resourceGadgets = new ResourceLocation(SilentLib.MOD_ID, "textures/gui/gui_guide_gadgets.png");
    }

    // ==========================================================
    // = Initializers. Call them during the appropriate phases! =
    // ==========================================================

    /**
     * <b>Call during your pre-init phase!</b> Calls initEntries to register entries.
     */
    public final void preInit() {
        initEntries();
    }

    /**
     * <b>Call during your post-init phase!</b> Calls initChapters to populate entries, sorts
     * everything, and spits out
     * some stats.
     */
    public final void postInit() {
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

                        List<ItemStack> items = new ArrayList<>();
                        page.getItemStacksForPage(items);
                        List<FluidStack> fluids = new ArrayList<>();
                        page.getFluidStacksForPage(fluids);

                        if ((!items.isEmpty() || !fluids.isEmpty()) && !pagesWithItemOrFluidData.contains(page)) {
                            pagesWithItemOrFluidData.add(page);
                            countInfo++;
                        }
                    }
                }
            }
        }

        entries.sort(Comparator.comparing(IGuideEntry::getSortingPriority, Comparator.reverseOrder()));
        chapters.sort(Comparator.comparing(IGuideChapter::getSortingPriority, Comparator.reverseOrder()));
        pagesWithItemOrFluidData.sort(Comparator.comparing(IGuidePage::getSortingPriority, Comparator.reverseOrder()));

        LogHelper log = SilentLib.logHelper;
        log.info("Guide book for mod \"{}\" initialized!", modId);
        log.info("    Entries:  {}", entries.size());
        log.info("    Chapters: {}", countChapter);
        log.info("    Pages:    {}", countPage);
        log.info("    Info:     {}", countInfo);
    }

    // =======================================================================
    // = Override these methods to control guide book contents/behavior/etc. =
    // =======================================================================

    /**
     * Initialize your entries here. DO NOT populate the entries!
     */
    public abstract void initEntries();

    /**
     * Initialize chapters/pages here.
     */
    public abstract void initChapters();

    /**
     * Gets "quotes" to display on the main page. Each quote can optionally end with "@username" to
     * attach a name/source to the quote. A quote is selected at random each time the main page
     * displays.
     *
     * @return Array of all possible quotes.
     * @since 2.1.1
     */
    public abstract String[] getQuotes();

    @Nonnull
    public String selectQuote(Random rand) {
        String[] quotes = getQuotes();
        if (quotes.length > 0) {
            return quotes[rand.nextInt(quotes.length)];
        }
        return "";
    }

    /**
     * Gets a screen for the config button to display. Null may be returned if you do not have one.
     *
     * @return The config GUI screen or null.
     * @since 2.1.1
     */
    @SideOnly(Side.CLIENT)
    @Nullable
    public abstract GuiScreen getConfigScreen(GuiScreen parent);

    /**
     * Gets a screen for the achievement button to display. Null may be returned if you do not have
     * one.
     *
     * @return The achievement GUI screen or null.
     * @since 2.1.1
     */
    @SideOnly(Side.CLIENT)
    @Nullable
    public abstract GuiScreen getAchievementScreen(GuiScreen parent);

    /**
     * Gets a resource location to use for the book/pages background texture. Override to use a
     * custom texture.
     *
     * @return ResourceLocation for the book texture.
     */
    public ResourceLocation getResourceGui() {
        return resourceGui;
    }

    /**
     * Gets a resource location to use for the GUI elements of the book. Override to use a custom
     * texture.
     *
     * @return
     */
    public ResourceLocation getResourceGadgets() {
        return resourceGadgets;
    }

    /**
     * Gets a String to display for the edition. You can optionally override this.
     *
     * @return The displayed edition String.
     * @since 2.1.1
     */
    public String getEditionString(EntityPlayer player) {
        String str = getEditionNumberString();
        return i18n.translate("guide.silentlib.edition", (Object) str);
    }

    protected String getEditionNumberString() {
        if (edition >= 11 && edition <= 13)
            return edition + "th";
        int mod10 = edition % 10;
        String str = edition + (mod10 == 1 ? "st" : mod10 == 2 ? "nd" : mod10 == 3 ? "rd" : "th");
        return str;
    }

    // ==============================================
    // = Some random methods you likely won't need. =
    // ==============================================

    /**
     * @return The mod ID the guide book is associated with.
     */
    public String getModId() {
        return modId;
    }

    /**
     * @return A list of all chapters.
     */
    public List<IGuideChapter> getChapters() {
        return chapters;
    }

    /**
     * @return A list of all entries.
     */
    public List<IGuideEntry> getEntries() {
        return entries;
    }

    /**
     * @return A list of all pages with items/fluids displayed on them.
     */
    public List<IGuidePage> getPagesWithItemOrFluidData() {
        return pagesWithItemOrFluidData;
    }

    /**
     * <b>Do not call!</b> GuideEntry will call this automatically in its constructor.
     *
     * @param entry
     */
    public void addEntry(IGuideEntry entry) {
        entries.add(entry);
    }
}
