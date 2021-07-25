package net.silentchaos512.lib.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
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

    private static void render(PoseStack matrix, Font fontRenderer, FormattedCharSequence text, float x, float y, int color, boolean shadow) {
        if (shadow) fontRenderer.drawShadow(matrix, text, x, y, color);
        else fontRenderer.draw(matrix, text, x, y, color);
    }

    public static void renderScaled(PoseStack matrix, Font fontRenderer, FormattedCharSequence text, int x, int y, float scale, int color, boolean shadow) {
        matrix.pushPose();
        matrix.scale(scale, scale, scale);
        // FIXME?
//        boolean oldUnicode = fontRenderer.getBidiFlag();
//        fontRenderer.setBidiFlag(false);

        render(matrix, fontRenderer, text, x / scale, y / scale, color, shadow);

//        fontRenderer.setBidiFlag(oldUnicode);
        matrix.popPose();
    }

    public static void renderSplit(PoseStack matrix, Font fontRenderer, FormattedText text, int x, int y, int width, int color, boolean shadow) {
        List<FormattedCharSequence> list = fontRenderer.split(text, width);
        for (int i = 0; i < list.size(); i++) {
            FormattedCharSequence line = list.get(i);
            int yTranslated = y + (i * fontRenderer.lineHeight);
            render(matrix, fontRenderer, line, x, yTranslated, color, shadow);
        }
    }

    public static void renderSplitScaled(PoseStack matrix, Font fontRenderer, FormattedText text, int x, int y, float scale, int color, boolean shadow, int length) {
        List<FormattedCharSequence> lines = fontRenderer.split(text, (int) (length / scale));
        for (int i = 0; i < lines.size(); i++) {
            int yTranslated = y + (i * (int) (fontRenderer.lineHeight * scale + 3));
            renderScaled(matrix, fontRenderer, lines.get(i), x, yTranslated, scale, color, shadow);
        }
    }
}
