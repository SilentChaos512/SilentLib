/*
 * Inspired by the Actually Additions booklet by Ellpeck.
 */

package net.silentchaos512.lib.guidebook.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.guidebook.GuideBook;
import net.silentchaos512.lib.guidebook.IGuideChapter;
import net.silentchaos512.lib.guidebook.IGuidePage;
import net.silentchaos512.lib.guidebook.gui.GuiEntry;
import net.silentchaos512.lib.guidebook.gui.GuiMainPage;
import net.silentchaos512.lib.guidebook.gui.GuiPage;
import net.silentchaos512.lib.guidebook.internal.GuiGuideBase;
import net.silentchaos512.lib.item.ItemGuideBookSL;

import java.util.ArrayList;
import java.util.List;

public final class GuideBookUtils {
    public static IGuidePage findFirstPageForStack(GuideBook book, ItemStack stack) {
        for (IGuidePage page : book.getPagesWithItemOrFluidData()) {
            List<ItemStack> stacks = new ArrayList<>();
            page.getItemStacksForPage(stacks);
            if (stacks != null && !stacks.isEmpty()) {
                for (ItemStack pageStack : stacks) {
                    if (pageStack.isItemEqual(stack)) {
                        return page;
                    }
                }
            }
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public static GuiPage createBookletGuiFromPage(GuideBook book, GuiScreen previousScreen, IGuidePage page) {
        GuiMainPage mainPage = new GuiMainPage(book, previousScreen);

        IGuideChapter chapter = page.getChapter();
        GuiEntry entry = new GuiEntry(book, previousScreen, mainPage, chapter.getEntry(), chapter, "", false);

        return createPageGui(book, previousScreen, entry, page);
    }

    @SideOnly(Side.CLIENT)
    public static GuiPage createPageGui(GuideBook book, GuiScreen previousScreen, GuiGuideBase parentPage, IGuidePage page) {
        IGuideChapter chapter = page.getChapter();

        IGuidePage[] allPages = chapter.getAllPages();
        int pageIndex = chapter.getPageIndex(page);
        IGuidePage page1;
        IGuidePage page2;

        if (page.shouldBeOnLeftSide()) {
            page1 = page;
            page2 = pageIndex >= allPages.length - 1 ? null : allPages[pageIndex + 1];
        } else {
            page1 = pageIndex <= 0 ? null : allPages[pageIndex - 1];
            page2 = page;
        }

        return new GuiPage(book, previousScreen, parentPage, page1, page2);
    }

    public static IGuidePage getBookletPageById(GuideBook book, String id) {
        if (id != null) {
            for (IGuideChapter chapter : book.getChapters()) {
                for (IGuidePage page : chapter.getAllPages()) {
                    if (id.equals(page.getIdentifier())) {
                        return page;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Sometimes the GUIs don't get the GuideBook they are supposed to be initialized with. In those
     * cases, we can get the book from the client player's held stack.
     */
    @SideOnly(Side.CLIENT)
    public static GuideBook getBookFromClientPlayerHand() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null) {
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.isEmpty())
                stack = player.getHeldItemOffhand();

            if (stack.getItem() instanceof ItemGuideBookSL) {
                ItemGuideBookSL itemBook = (ItemGuideBookSL) stack.getItem();
                return itemBook.book;
            }
        }
        return null;
    }
}
