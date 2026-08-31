package net.silentchaos512.lib.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnchorTest {
    @Test
    void anchorsCalculateExpectedPositions() {
        assertEquals(5, Anchor.TOP_LEFT.getX(100, 20, 5));
        assertEquals(5, Anchor.TOP_LEFT.getY(80, 10, 5));
        assertEquals(40, Anchor.CENTER.getX(100, 20, 5));
        assertEquals(35, Anchor.CENTER.getY(80, 10, 5));
        assertEquals(75, Anchor.BOTTOM_RIGHT.getX(100, 20, 5));
        assertEquals(65, Anchor.BOTTOM_RIGHT.getY(80, 10, 5));
        assertEquals(Anchor.Horizontal.CENTER, Anchor.CENTER.getHorizontal());
        assertEquals(Anchor.Vertical.CENTER, Anchor.CENTER.getVertical());
    }
}
