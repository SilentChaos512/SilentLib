package net.silentchaos512.lib.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ColorUtilsTest {
    @Test
    void additiveBlendUsesExpectedAverageAndAlphaMode() {
        List<Integer> colors = List.of(0xFF0000, 0x0000FF);

        assertEquals(0xFFFF00FF, ColorUtils.blendAdditive(colors));
        assertEquals(0x00FF00FF, ColorUtils.blendAdditive(colors, false));
    }

    @Test
    void mixboxBlendPreservesASingleColorAndHonorsAlphaMode() {
        int color = 0x123456;

        assertEquals(0xFF123456, ColorUtils.blendMixbox(List.of(color)));
        assertEquals(0x00123456, ColorUtils.blendMixbox(List.of(color), false));
    }

    @Test
    void emptyMixboxBlendMatchesAdditiveBlend() {
        assertEquals(0xFFFFFFFF, ColorUtils.blendMixbox(List.of()));
        assertEquals(0xFFFFFFFF, ColorUtils.blendMixbox(List.of(), false));
    }
}
