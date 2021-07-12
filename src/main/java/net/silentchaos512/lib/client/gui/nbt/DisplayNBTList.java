package net.silentchaos512.lib.client.gui.nbt;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.text.StringTextComponent;
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
        public void render(MatrixStack matrix, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
            TextRenderUtils.renderScaled(matrix, this.mc.font, new StringTextComponent(this.text).getVisualOrderText(), p_230432_4_, p_230432_3_, 1.0f, 0xFFFFFF, true);
        }
    }
}
