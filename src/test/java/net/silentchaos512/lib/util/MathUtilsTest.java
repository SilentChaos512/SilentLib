package net.silentchaos512.lib.util;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MathUtilsTest {
    @Test
    void clampLimitsAllNumericTypes() {
        assertEquals(0, MathUtils.clamp(-1, 0, 10));
        assertEquals(5, MathUtils.clamp(5, 0, 10));
        assertEquals(10, MathUtils.clamp(11, 0, 10));
        assertEquals(1.5f, MathUtils.clamp(1.5f, 0f, 2f));
        assertEquals(2d, MathUtils.clamp(3d, 0d, 2d));
    }

    @Test
    void comparisonsAndRangesRespectTheirBounds() {
        assertTrue(MathUtils.doublesEqual(1d, 1d + 0.0000000005d));
        assertFalse(MathUtils.doublesEqual(1d, 1d + 0.000000001d));
        assertTrue(MathUtils.doubleIsInt(2.0000000005d));
        assertFalse(MathUtils.floatIsInt(2.1f));
        assertTrue(MathUtils.inRangeInclusive(1, 1, 2));
        assertFalse(MathUtils.inRangeExclusive(1, 1, 2));
        assertTrue(MathUtils.inRangeExclusive(1.5d, 1d, 2d));
    }

    @Test
    void minMaxAndInclusiveRandomValuesWorkAcrossArguments() {
        assertEquals(-2, MathUtils.min(3, 1, 4, -2, 5));
        assertEquals(5, MathUtils.max(3, 1, 4, -2, 5));

        Random random = new Random(42L);
        for (int i = 0; i < 20; ++i) {
            int value = MathUtils.nextIntInclusive(random, -3, 4);
            assertTrue(value >= -3 && value <= 4);
        }
    }
}
