/*
 * GameUtil
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

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public final class GameUtil {
    private GameUtil() {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * Check if this is the client side.
     * @return True if and only if we are on the client side
     */
    public static boolean isClient() {
        return FMLCommonHandler.instance().getSide() == Side.CLIENT;
    }

    /**
     * Check if this is a deobfuscated (development) environment.
     * @return True if and only if we are running in a deobfuscated environment
     */
    public static boolean isDeobfuscated() {
        return (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    }
}
