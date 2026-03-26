package net.silentchaos512.lib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

@SuppressWarnings("MethodWithTooManyParameters")
public final class TextRenderUtils {
    private TextRenderUtils() { throw new IllegalAccessError("Utility class"); }

    public static Font getFontRenderer() {
        return Minecraft.getInstance().font;
    }

    public static void renderScaled(GuiGraphicsExtractor graphics, Font font, FormattedCharSequence text, int x, int y, float scale, int color, boolean shadow) {
        var matrix = graphics.pose();

        matrix.pushMatrix();
        matrix.scale(scale, scale, matrix);

        graphics.text(font, text, (int) (x / scale), (int) (y / scale), color, shadow);

        matrix.popMatrix();
    }

    public static void renderSplit(GuiGraphicsExtractor graphics, Font font, FormattedText text, int x, int y, int width, int color, boolean shadow) {
        List<FormattedCharSequence> list = font.split(text, width);
        for (int i = 0; i < list.size(); i++) {
            FormattedCharSequence line = list.get(i);
            int yTranslated = y + (i * font.lineHeight);
            graphics.text(font, line, x, yTranslated, color, shadow);
        }
    }

    public static void renderSplitScaled(GuiGraphicsExtractor graphics, Font font, FormattedText text, int x, int y, float scale, int color, boolean shadow, int length) {
        List<FormattedCharSequence> lines = font.split(text, (int) (length / scale));
        for (int i = 0; i < lines.size(); i++) {
            int yTranslated = y + (i * (int) (font.lineHeight * scale + 3));
            renderScaled(graphics, font, lines.get(i), x, yTranslated, scale, color, shadow);
        }
    }
}
