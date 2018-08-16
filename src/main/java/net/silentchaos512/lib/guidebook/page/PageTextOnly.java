/*
 * Inspired by the Actually Additions booklet by Ellpeck.
 */

package net.silentchaos512.lib.guidebook.page;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.guidebook.GuideBook;
import net.silentchaos512.lib.guidebook.internal.GuiGuideBase;

public class PageTextOnly extends GuidePage {
    public PageTextOnly(GuideBook book, int localizationKey, int priority) {
        super(book, localizationKey, priority);
    }

    public PageTextOnly(GuideBook book, int localizationKey) {
        super(book, localizationKey);
    }

    @SideOnly(Side.CLIENT)
    public static void renderTextToPage(GuiGuideBase gui, GuidePage page, int x, int y) {
        String text = page.getInfoText();
        if (text != null && !text.isEmpty()) {
            gui.renderSplitScaledAsciiString(text, x, y, 0, false, gui.getMediumFontSize(), 120);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawScreenPre(GuiGuideBase gui, int startX, int startY, int mouseX, int mouseY, float partialTicks) {
        super.drawScreenPre(gui, startX, startY, mouseX, mouseY, partialTicks);
        renderTextToPage(gui, this, startX + 6, startY + 5);
    }
}
