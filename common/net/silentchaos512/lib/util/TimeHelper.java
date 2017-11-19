package net.silentchaos512.lib.util;

public class TimeHelper {

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
