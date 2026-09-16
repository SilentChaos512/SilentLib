package net.silentchaos512.lib.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringUtilsTest {
    @Test
    void lowerUsesRootLocale() {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr"));
            assertEquals("i", StringUtils.lower("I"));
        } finally {
            Locale.setDefault(previous);
        }
    }

    @Test
    void wrapLinesSplitsAtWhitespaceAndPreservesShortLines() {
        assertEquals(List.of("four", "five", "short"), StringUtils.wrapLines(List.of("four five", "short"), 4, ignored -> ""));
    }
}
