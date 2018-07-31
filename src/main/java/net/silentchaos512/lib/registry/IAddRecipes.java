/*
 * Silent Lib
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

package net.silentchaos512.lib.registry;

/**
 * Adds methods for adding recipe and ore dictionary entries. Might be removed or altered in 1.13.
 */
public interface IAddRecipes {

    /**
     * Add recipes for the block/item. In 1.13, this may only generate the JSON files.
     *
     * @param recipes RecipeMaker object provided by the mod's SRegistry
     */
    @Deprecated
    default void addRecipes(RecipeMaker recipes) {
    }

    /**
     * Add ore dictionary entries for the block/item.
     *
     * @deprecated Ore dictionary is likely to be removed in 1.13
     */
    @Deprecated
    default void addOreDict() {
    }
}
