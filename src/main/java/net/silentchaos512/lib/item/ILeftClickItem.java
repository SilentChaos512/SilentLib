/*
 * SilentLib - IItemSL
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

package net.silentchaos512.lib.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public interface ILeftClickItem {
    /**
     * Networked left-click handler. Called in SilentLibEventHandlers on both the client- and
     * server-side (via packet) when a player left-clicks on nothing (in the air).
     *
     * @return If this returns SUCCESS on the client-side, a packet will be sent to the server.
     */
    default ActionResult<ItemStack> onItemLeftClickSL(World world, EntityPlayer player, EnumHand hand) {
        return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
    }

    /**
     * Called when the player left-clicks on a block. Defaults to the same behavior as an empty
     * click (onItemLeftClickSL).
     *
     * @return If this returns SUCCESS on the client-side, a packet will be sent to the server.
     */
    default ActionResult<ItemStack> onItemLeftClickBlockSL(World world, EntityPlayer player, EnumHand hand) {
        return onItemLeftClickSL(world, player, hand);
    }
}
