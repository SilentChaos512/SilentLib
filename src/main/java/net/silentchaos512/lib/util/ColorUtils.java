package net.silentchaos512.lib.util;

import com.scrtwpns.Mixbox;

import java.util.Collection;

public class ColorUtils {
    private ColorUtils() {
        throw new IllegalAccessError("Utility class");
    }

    // TODO: Add weighted blending methods?
    public static int blend(ColorBlendAlgorithm algorithm, Collection<Integer> colors) {
        return switch (algorithm) {
            case ADDITIVE -> blendAdditive(colors);
            case MIXBOX -> blendMixbox(colors);
        };
    }

    public static int blend(ColorBlendAlgorithm algorithm, Collection<Integer> colors, boolean forceAlpha) {
        return switch (algorithm) {
            case ADDITIVE -> blendAdditive(colors, forceAlpha);
            case MIXBOX -> blendMixbox(colors, forceAlpha);
        };
    }

    public static int blendAdditive(Collection<Integer> colors) {
        return blendAdditive(colors, true);
    }

    public static int blendAdditive(Collection<Integer> colors, boolean forceAlpha) {
        final int colorCount = colors.size();
        if (colorCount == 0) {
            return 0xFFFFFFFF;
        }

        int[] componentSums = new int[3];
        int maxColorSum = 0;

        for (int color : colors) {
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            maxColorSum += Math.max(r, Math.max(g, b));
            componentSums[0] += r;
            componentSums[1] += g;
            componentSums[2] += b;
        }

        int r = componentSums[0] / colorCount;
        int g = componentSums[1] / colorCount;
        int b = componentSums[2] / colorCount;
        float maxAverage = (float) maxColorSum / (float) colorCount;
        float max = (float) Math.max(r, Math.max(g, b));
        r = (int) ((float) r * maxAverage / max);
        g = (int) ((float) g * maxAverage / max);
        b = (int) ((float) b * maxAverage / max);
        int finalColor = (r << 8) + g;
        finalColor = (finalColor << 8) + b;
        return forceAlpha ? finalColor | 0xFF000000 : finalColor;

    }

    public static int blendMixbox(Collection<Integer> colors) {
        return blendMixbox(colors, true);
    }

    public static int blendMixbox(Collection<Integer> colors, boolean forceAlpha) {
        float ratio = 1f / colors.size();
        float[][] z = new float[colors.size()][Mixbox.LATENT_SIZE];
        int colorIndex = 0;
        for (int color : colors) {
            z[colorIndex] = Mixbox.rgbToLatent(color);
            ++colorIndex;
        }

        float[] zMix = new float[Mixbox.LATENT_SIZE];
        for (int i = 0; i < zMix.length; ++i) {
            zMix[i] = 0f;
            for (int c = 0; c < colors.size(); ++c) {
                zMix[i] += ratio * z[c][i];
            }
        }
        int result = Mixbox.latentToRgb(zMix);
        return forceAlpha ? result | 0xFF000000 : result;
    }
}
