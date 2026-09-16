package net.silentchaos512.lib.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeUtilsTest {
    @Test
    void convertsTimeUnitsToTicks() {
        assertEquals(20, TimeUtils.ticksFromSeconds(1f));
        assertEquals(30, TimeUtils.ticksFromSeconds(1.5f));
        assertEquals(1_200, TimeUtils.ticksFromMinutes(1f));
        assertEquals(72_000, TimeUtils.ticksFromHours(1f));
    }
}
