/*
 * SilentLib - IMatchList
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

package net.silentchaos512.lib.collection;

import net.minecraftforge.common.config.Configuration;

/**
 * Represents a blacklist/whitelist that is meant to be loaded from a config file.
 * See {@link AbstractMatchList} for a skeletal implementation.
 *
 * @param <T> Any type would work, but IMatchList is typically used to match objects with string keys, such as Item or Entity.
 */
public interface IMatchList<T> {
    /**
     * Check whether the object "matches" the list. This is not the same as being "contained" in the list, as this
     * depends on whether we are a whitelist or a blacklist.
     *
     * @param t The object being checked
     * @return Returns true if the list is a whitelist and contains the object, or if the list is a blacklist and
     * does not contain the object, false otherwise.
     */
    boolean matches(T t);

    /**
     * Load properties of the list from the config file.
     * @param config The mod's configuration object
     * @param name The base name of the config options. Implementations of loadConfig will likely append text to this.
     * @param category The config category
     * @param comment A comment for the config
     */
    void loadConfig(Configuration config, String name, String category, String comment);
}
