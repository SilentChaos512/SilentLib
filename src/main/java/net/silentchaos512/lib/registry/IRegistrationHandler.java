/*
 * SilentLib - IRegistrationHandler
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

package net.silentchaos512.lib.registry;

/**
 * Implement on classes like ModBlocks/ModItems. Register them with your SRegistry to have them automatically called for
 * the appropriate event. Creating blocks/items/etc should probably be done statically or in a constructor.
 *
 * @author SilentChaos512
 * @since 2.2.2
 */
@Deprecated
public interface IRegistrationHandler<T> {

  public void registerAll(SRegistry reg);
}
