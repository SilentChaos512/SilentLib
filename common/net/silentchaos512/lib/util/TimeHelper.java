package net.silentchaos512.lib.util;

public class TimeHelper {

  public static int TICKS_PER_SECOND = 20;
  public static float SECONDS_PER_TICK = 1f / 20f;

  public static int ticksFromSeconds(float seconds) {

    return (int) (20 * seconds);
  }

  public static int ticksFromMinutes(float minutes) {

    return (int) (1200 * minutes);
  }

  public static int ticksFromHours(float hours) {

    return (int) (72000 * hours);
  }
}
