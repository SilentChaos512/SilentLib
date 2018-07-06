/*
 * SilentLib - ConfigOptionOreGen
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

package net.silentchaos512.lib.config;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.Configuration;

import java.util.Random;

// TODO: Add option to change blacklist to a whitelist
// TODO: Add getter/setter annotations/methods, make things private, change blacklist to something immutable
public class ConfigOptionOreGen {

  public static final float VEIN_COUNT_MIN = 0.0f;
  public static final float VEIN_COUNT_MAX = 1000.0f;
  public static final int VEIN_SIZE_MIN = 0;
  public static final int VEIN_SIZE_MAX = 1000;
  public static final int Y_MIN = 0;
  public static final int Y_MAX = 255;

  public final String name;
  public float veinCount;
  public int veinSize;
  public int minY;
  public int maxY;
  public final int dimension;
  public int[] dimensionBlacklist = new int[0];

  public ConfigOptionOreGen(String name, int dimension, float veinCount, int veinSize, int minY,
      int maxY) {

    this.name = name;
    this.dimension = dimension;
    this.veinCount = veinCount;
    this.veinSize = veinSize;
    this.minY = minY;
    this.maxY = maxY;
  }

  public int getVeinCount(Random random) {

    // Decimal part of veinCount is a chance to spawn an extra vein.
    float diff = veinCount - (int) veinCount;
    return (int) veinCount + (random.nextFloat() < diff ? 1 : 0);
  }

  public ConfigOptionOreGen loadValue(Configuration c, String category) {

    return loadValue(c, category + c.CATEGORY_SPLITTER + name, "World generation for " + name);
  }

  public ConfigOptionOreGen loadValue(Configuration c, String category, String comment) {

    c.setCategoryComment(category, comment);
    veinCount = (float) c.get(category, "Vein Count", veinCount).getDouble();
    veinSize = c.get(category, "Vein Size", veinSize).getInt();
    minY = c.get(category, "Min Y", minY).getInt();
    maxY = c.get(category, "Max Y", maxY).getInt();
    dimensionBlacklist = c.get(category, "Dimension Blacklist", new int[0]).getIntList();
    return this.validate();
  }

  public ConfigOptionOreGen validate() {

    veinCount = MathHelper.clamp(veinCount, VEIN_COUNT_MIN, VEIN_COUNT_MAX);
    veinSize = MathHelper.clamp(veinSize, VEIN_SIZE_MIN, VEIN_SIZE_MAX);
    minY = MathHelper.clamp(minY, Y_MIN, Y_MAX);
    maxY = MathHelper.clamp(maxY, Y_MIN, Y_MAX);

    // Sanity check: max Y must be greater than min Y.
    if (maxY <= minY) {
      maxY = minY + 1;
    }

    return this;
  }

  public boolean isEnabled() {

    // Ore will be disabled if either the vein count or vein size is set to 0.
    return veinCount > 0 && veinSize > 0;
  }

  public BlockPos getRandomPos(Random random, int posX, int posZ) {

    //@formatter:off
    return new BlockPos(
        posX + random.nextInt(16),
        minY + random.nextInt(maxY - minY),
        posZ + random.nextInt(16));
    //@formatter:on
  }

  public boolean canSpawnInDimension(int dim) {

    for (int i : dimensionBlacklist)
      if (i == dim)
        return false;
    return true;
  }
}
