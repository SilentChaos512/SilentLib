package net.silentchaos512.lib.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnumUtilsTest {
    private enum Example {
        FIRST(3), SECOND(3), THIRD(9);

        private final int index;

        Example(int index) {
            this.index = index;
        }
    }

    @Test
    void lookupMethodsHandleMatchesDefaultsAndCase() {
        assertEquals(Example.FIRST, EnumUtils.byIndex(3, Example.THIRD, value -> value.index));
        assertEquals(Example.THIRD, EnumUtils.byIndex(7, Example.THIRD, value -> value.index));
        assertEquals(Example.THIRD, EnumUtils.byIndex(9, Example.class, value -> value.index).orElseThrow());
        assertTrue(EnumUtils.byIndex(7, Example.class, value -> value.index).isEmpty());
        assertEquals(Example.SECOND, EnumUtils.byName("second", Example.FIRST));
        assertTrue(EnumUtils.byName("missing", Example.class).isEmpty());
    }

    @Test
    void ordinalValidationAndCyclingHandleEdges() {
        assertEquals(Example.FIRST, EnumUtils.byOrdinal(0, Example.THIRD));
        assertEquals(Example.THIRD, EnumUtils.byOrdinal(-1, Example.THIRD));
        assertTrue(EnumUtils.validate("third", Example.class));
        assertFalse(EnumUtils.validate(null, Example.class));
        assertEquals(Example.FIRST, EnumUtils.cycle(Example.THIRD, false));
        assertEquals(Example.THIRD, EnumUtils.cycle(Example.FIRST, true));
    }
}
