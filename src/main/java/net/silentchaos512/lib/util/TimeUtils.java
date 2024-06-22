package net.silentchaos512.lib.util;

public final class TimeUtils {
    public static final int TICKS_PER_SECOND = 20;
    public static final int VANILLA_DAY_LENGTH = 24000;

    private TimeUtils() {
        throw new IllegalAccessError("Utility class");
    }

    public static int ticksFromSeconds(float seconds) {
        return (int) (TICKS_PER_SECOND * seconds);
    }

    public static int ticksFromMinutes(float minutes) {
        return (int) (TICKS_PER_SECOND * 60 * minutes);
    }

    public static int ticksFromHours(float hours) {
        return (int) (TICKS_PER_SECOND * 60 * 60 * hours);
    }
}
