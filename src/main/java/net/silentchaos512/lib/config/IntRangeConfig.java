/*
 * Silent Lib -- IntRangeConfig
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.lib.config;

import lombok.Getter;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.silentchaos512.lib.util.LogHelper;

// TODO: Test me! And make a float version.
public class IntRangeConfig {
    @Getter private int min;
    @Getter private int max;
    private final int defaultMin;
    private final int defaultMax;
    private final int lowerLimit;
    private final int upperLimit;

    public IntRangeConfig(int defaultMin, int defaultMax, int lowerLimit, int upperLimit) {
        this.defaultMin = defaultMin;
        this.defaultMax = defaultMax;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
    }

    public void loadConfig(ConfigBase config, String name, String category, String comment) {
        min = config.loadInt(name + " Min", category, defaultMin, "Minimum: " + comment);
        max = config.loadInt(name + " Max", category, defaultMax, "Maximum: " + comment);

        min = MathHelper.clamp(min, lowerLimit, upperLimit);
        max = MathHelper.clamp(max, lowerLimit, upperLimit);

        if (max < min) {
            // Values not sane
            ModContainer modContainer = Loader.instance().activeModContainer();
            if (modContainer != null) {
                LogHelper.getRegisteredLogger(modContainer.getName()).ifPresent(log -> log.warn(
                        "Config '{}' (category {}): max ({}) is less than min ({})!", name, category, max, min));
            }
            // Make it sane
            max = min;
        }
    }
}
