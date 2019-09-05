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

import net.minecraftforge.forgespi.Environment;

public final class GameUtil {
    private GameUtil() {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * Check if this is the client side.
     *
     * @return True if and only if we are on the client side
     */
    public static boolean isClient() {
        return Environment.get().getDist().isClient();
    }

    /**
     * Check if this is the server side.
     *
     * @return True if and only if we are on the server side
     */
    public static boolean isServer() {
        return Environment.get().getDist().isDedicatedServer();
    }

    /**
     * Check if this is a deobfuscated (development) environment.
     *
     * @return True if and only if we are running in a deobfuscated environment
     */
    public static boolean isDeobfuscated() {
        // FIXME
//        return (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
        return true;
    }

    /**
     * Determine if tooltips should be calculated, call in {@link net.minecraftforge.event.entity.player.ItemTooltipEvent}
     * handlers. This can prevent tooltip events from being processed at unnecessary times (world
     * loading/closing), while still allowing JEI to build its cache. JEI tooltip caches are done in
     * LoaderState#AVAILABLE, in-game is LoaderState#SERVER_STARTED (this info is outdated)
     *
     * @since 3.0.8
     */
    @Deprecated
    public static boolean shouldCalculateTooltip() {
        // FIXME
//        LoaderState state = Loader.instance().getLoaderState();
//        // These states have no reason to go through tooltips that I can tell, but they do.
//        return state != LoaderState.INITIALIZATION
//                && state != LoaderState.SERVER_ABOUT_TO_START
//                && state != LoaderState.SERVER_STOPPING;
        return true;
    }
}
