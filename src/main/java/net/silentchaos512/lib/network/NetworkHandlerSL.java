/*
 * SilentLib - NetworkHandlerSL
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

package net.silentchaos512.lib.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Network handler class, mostly copied from Silents' Gems.
 *
 * @author SilentChaos512
 * @since 2.1.3
 */
public class NetworkHandlerSL {

  public final SimpleNetworkWrapper wrapper;

  private int lastMessageId = -1;

  public NetworkHandlerSL(String modId) {

    wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(modId);
  }

  public void register(Class clazz, Side handlerSide) {

    wrapper.registerMessage(clazz, clazz, ++lastMessageId, handlerSide);
  }
}
