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

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.silentchaos512.lib.network.internal.LeftClickItemPacket;
import net.silentchaos512.lib.network.internal.SilentLibNetwork;

public interface ILeftClickItem {
    enum ClickType {
        EMPTY, BLOCK
    }

    /**
     * Networked left-click handler. Called in SilentLibEventHandlers on both the client- and
     * server-side (via packet) when a player left-clicks on nothing (in the air).
     *
     * @param world  The world
     * @param player The player
     * @param hand   The hand the item is in
     * @return If this returns SUCCESS on the client-side, a packet will be sent to the server.
     */
    default InteractionResultHolder<ItemStack> onItemLeftClickSL(Level world, Player player, InteractionHand hand) {
        return new InteractionResultHolder<>(InteractionResult.PASS, player.getItemInHand(hand));
    }

    /**
     * Called when the player left-clicks on a block. Defaults to the same behavior as an empty
     * click (onItemLeftClickSL).
     *
     * @param world  The world
     * @param player The player
     * @param hand   The hand the item is in
     * @return If this returns SUCCESS on the client-side, a packet will be sent to the server.
     */
    default InteractionResultHolder<ItemStack> onItemLeftClickBlockSL(Level world, Player player, InteractionHand hand) {
        return onItemLeftClickSL(world, player, hand);
    }

    final class EventHandler {
        private static EventHandler INSTANCE;

        private EventHandler() { }

        public static void init() {
            if (INSTANCE != null) return;
            INSTANCE = new EventHandler();
            MinecraftForge.EVENT_BUS.addListener(EventHandler::onLeftClickBlock);
            MinecraftForge.EVENT_BUS.addListener(EventHandler::onLeftClickEmpty);
        }

        private static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
            ItemStack stack = event.getItemStack();
            if (!stack.isEmpty() && stack.getItem() instanceof ILeftClickItem) {
                // Client-side call
                InteractionResultHolder<ItemStack> result = ((ILeftClickItem) stack.getItem()).onItemLeftClickBlockSL(event.getWorld(), event.getPlayer(), event.getHand());
                // Server-side call
                if (result.getResult() == InteractionResult.SUCCESS) {
                    SilentLibNetwork.channel.sendToServer(new LeftClickItemPacket(ClickType.BLOCK, event.getHand()));
                }
            }
        }

        private static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
            ItemStack stack = event.getItemStack();
            if (!stack.isEmpty() && stack.getItem() instanceof ILeftClickItem) {
                // Client-side call
                InteractionResultHolder<ItemStack> result = ((ILeftClickItem) stack.getItem()).onItemLeftClickSL(event.getWorld(), event.getPlayer(), event.getHand());
                // Server-side call
                if (result.getResult() == InteractionResult.SUCCESS) {
                    SilentLibNetwork.channel.sendToServer(new LeftClickItemPacket(ClickType.EMPTY, event.getHand()));
                }
            }
        }
    }
}
