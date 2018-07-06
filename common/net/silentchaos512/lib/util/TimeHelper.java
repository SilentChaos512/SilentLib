/*
 * SilentLib - TimeHelper
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
