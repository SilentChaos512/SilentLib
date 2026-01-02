package net.silentchaos512.lib.client.gui.nbt;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.silentchaos512.lib.util.TextRenderUtils;

public class DisplayNBTList extends ObjectSelectionList<DisplayNBTList.Entry> {
    private final DisplayNBTScreen screen;

    public DisplayNBTList(DisplayNBTScreen screen, Minecraft mcIn, int widthIn, int heightIn, int topIn, int slotHeightIn) {
        super(mcIn, widthIn, heightIn, topIn, slotHeightIn);
        this.screen = screen;
        this.screen.lines.forEach(line -> addEntry(new Entry(line)));
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 200;
    }

    public final class Entry extends ObjectSelectionList.Entry<Entry> {
        private final String text;
        private final Minecraft mc;

        public Entry(String text) {
            this.text = text;
            this.mc = Minecraft.getInstance();
        }

        @Override
        public void renderContent(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
            TextRenderUtils.renderScaled(graphics, this.mc.font, Component.literal(this.text).getVisualOrderText(), mouseX, mouseY, 1.0f, 0xFFFFFF, true);
        }

        @Override
        public Component getNarration() {
            return Component.empty();
        }
    }
}
