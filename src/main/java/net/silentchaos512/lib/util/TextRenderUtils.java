package net.silentchaos512.lib.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("MethodWithTooManyParameters")
public final class TextRenderUtils {
    private TextRenderUtils() { throw new IllegalAccessError("Utility class"); }

    public static Font getFontRenderer() {
        return Minecraft.getInstance().font;
    }

    public static void renderScaled(GuiGraphics graphics, Font font, FormattedCharSequence text, int x, int y, float scale, int color, boolean shadow) {
        PoseStack matrix = graphics.pose();

        matrix.pushPose();
        matrix.scale(scale, scale, scale);
        // FIXME?
//        boolean oldUnicode = fontRenderer.getBidiFlag();
//        fontRenderer.setBidiFlag(false);

        graphics.drawString(font, text, x / scale, y / scale, color, shadow);

//        fontRenderer.setBidiFlag(oldUnicode);
        matrix.popPose();
    }

    public static void renderSplit(GuiGraphics graphics, Font font, FormattedText text, int x, int y, int width, int color, boolean shadow) {
        List<FormattedCharSequence> list = font.split(text, width);
        for (int i = 0; i < list.size(); i++) {
            FormattedCharSequence line = list.get(i);
            int yTranslated = y + (i * font.lineHeight);
            graphics.drawString(font, line, x, yTranslated, color, shadow);
        }
    }

    public static void renderSplitScaled(GuiGraphics graphics, Font font, FormattedText text, int x, int y, float scale, int color, boolean shadow, int length) {
        List<FormattedCharSequence> lines = font.split(text, (int) (length / scale));
        for (int i = 0; i < lines.size(); i++) {
            int yTranslated = y + (i * (int) (font.lineHeight * scale + 3));
            renderScaled(graphics, font, lines.get(i), x, yTranslated, scale, color, shadow);
        }
    }
}
