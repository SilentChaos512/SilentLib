package net.silentchaos512.lib.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
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

    private static void render(FontRenderer fontRenderer, String text, float x, float y, int color, boolean shadow) {
        if (shadow) fontRenderer.drawStringWithShadow(text, x, y, color);
        else fontRenderer.drawString(text, x, y, color);
    }

    public static void renderScaled(FontRenderer fontRenderer, String text, int x, int y, float scale, int color, boolean shadow) {
        RenderSystem.pushMatrix();
        RenderSystem.scalef(scale, scale, scale);
        // Is this right? Was 'UnicodeFlag'
        boolean oldUnicode = fontRenderer.getBidiFlag();
        fontRenderer.setBidiFlag(false);

        render(fontRenderer, text, x / scale, y / scale, color, shadow);

        fontRenderer.setBidiFlag(oldUnicode);
        RenderSystem.popMatrix();
    }

    public static void renderSplit(FontRenderer fontRenderer, String text, int x, int y, int width, int color, boolean shadow) {
        List list = fontRenderer.listFormattedStringToWidth(text, width);
        for (int i = 0; i < list.size(); i++) {
            String line = (String) list.get(i);
            int yTranslated = y + (i * fontRenderer.FONT_HEIGHT);
            render(fontRenderer, line, x, yTranslated, color, shadow);
        }
    }

    public static void renderSplitScaled(FontRenderer fontRenderer, String text, int x, int y, float scale, int color, boolean shadow, int length) {
        List<String> lines = fontRenderer.listFormattedStringToWidth(text, (int) (length / scale));
        for (int i = 0; i < lines.size(); i++) {
            int yTranslated = y + (i * (int) (fontRenderer.FONT_HEIGHT * scale + 3));
            renderScaled(fontRenderer, lines.get(i), x, yTranslated, scale, color, shadow);
        }
    }
}
