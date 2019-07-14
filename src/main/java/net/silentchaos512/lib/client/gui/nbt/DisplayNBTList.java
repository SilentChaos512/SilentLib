package net.silentchaos512.lib.client.gui.nbt;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.silentchaos512.lib.util.TextRenderUtils;

public class DisplayNBTList extends ExtendedList<DisplayNBTList.Entry> {
    private final DisplayNBTScreen screen;

    public DisplayNBTList(DisplayNBTScreen screen, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.screen = screen;
        this.screen.lines.forEach(line -> addEntry(new Entry(line)));
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 200;
    }

    public final class Entry extends ExtendedList.AbstractListEntry<Entry> {
        private final String text;
        private final Minecraft mc;

        public Entry(String text) {
            this.text = text;
            this.mc = Minecraft.getInstance();
        }

        @Override
        public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
            TextRenderUtils.renderScaled(this.mc.fontRenderer, this.text, p_render_3_, p_render_2_, 1.0f, 0xFFFFFF, true);
        }
    }
}
