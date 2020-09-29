package net.silentchaos512.lib.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("MethodWithTooManyParameters")
public final class TextRenderUtils {
    private TextRenderUtils() { throw new IllegalAccessError("Utility class"); }

    public static FontRenderer getFontRenderer() {
        return Minecraft.getInstance().fontRenderer;
    }

    private static void render(MatrixStack matrix, FontRenderer fontRenderer, IReorderingProcessor text, float x, float y, int color, boolean shadow) {
        if (shadow) fontRenderer.func_238407_a_(matrix, text, x, y, color);
        else fontRenderer.func_238422_b_(matrix, text, x, y, color);
    }

    public static void renderScaled(MatrixStack matrix, FontRenderer fontRenderer, IReorderingProcessor text, int x, int y, float scale, int color, boolean shadow) {
        matrix.push();
        matrix.scale(scale, scale, scale);
        // FIXME?
//        boolean oldUnicode = fontRenderer.getBidiFlag();
//        fontRenderer.setBidiFlag(false);

        render(matrix, fontRenderer, text, x / scale, y / scale, color, shadow);

//        fontRenderer.setBidiFlag(oldUnicode);
        matrix.pop();
    }

    public static void renderSplit(MatrixStack matrix, FontRenderer fontRenderer, ITextProperties text, int x, int y, int width, int color, boolean shadow) {
        List<IReorderingProcessor> list = fontRenderer.trimStringToWidth(text, width);
        for (int i = 0; i < list.size(); i++) {
            IReorderingProcessor line = list.get(i);
            int yTranslated = y + (i * fontRenderer.FONT_HEIGHT);
            render(matrix, fontRenderer, line, x, yTranslated, color, shadow);
        }
    }

    public static void renderSplitScaled(MatrixStack matrix, FontRenderer fontRenderer, ITextProperties text, int x, int y, float scale, int color, boolean shadow, int length) {
        List<IReorderingProcessor> lines = fontRenderer.trimStringToWidth(text, (int) (length / scale));
        for (int i = 0; i < lines.size(); i++) {
            int yTranslated = y + (i * (int) (fontRenderer.FONT_HEIGHT * scale + 3));
            renderScaled(matrix, fontRenderer, lines.get(i), x, yTranslated, scale, color, shadow);
        }
    }
}
