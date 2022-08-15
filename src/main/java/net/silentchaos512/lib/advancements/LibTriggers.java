/*
 * Silent Lib -- LibTriggers
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

package net.silentchaos512.lib.advancements;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Some common advancement triggers for mods to use.
 *
 * @since 2.3.15
 */
public final class LibTriggers {
    /**
     * Represents an item being used (right-clicking a block, entity, or nothing). No need to
     * trigger this yourself, it's handled in {@link EventHandler}.
     */
    public static final UseItemTrigger USE_ITEM = CriteriaTriggers.register(new UseItemTrigger());
    /**
     * Can be used if you just need a trigger with a single int. Requires a ResourceLocation to to
     * distinguish between different uses.
     */
    public static final GenericIntTrigger GENERIC_INT = CriteriaTriggers.register(new GenericIntTrigger());

    private LibTriggers() {}

    public static void init() {
        EventHandler.init();
    }

    static final class EventHandler {
        private static EventHandler INSTANCE;

        private EventHandler() { }

        static void init() {
            if (INSTANCE != null) return;
            INSTANCE = new EventHandler();
            MinecraftForge.EVENT_BUS.addListener(EventHandler::onPlayerRightClickBlock);
            MinecraftForge.EVENT_BUS.addListener(EventHandler::onPlayerRightClickEntity);
            MinecraftForge.EVENT_BUS.addListener(EventHandler::onPlayerRightClickItem);
        }

        public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
            if (event.getEntity() instanceof ServerPlayer) {
                LibTriggers.USE_ITEM.trigger((ServerPlayer) event.getEntity(), event.getItemStack(), UseItemTrigger.Target.BLOCK);
            }
        }

        @SubscribeEvent
        public static void onPlayerRightClickEntity(PlayerInteractEvent.EntityInteract event) {
            if (event.getEntity() instanceof ServerPlayer) {
                LibTriggers.USE_ITEM.trigger((ServerPlayer) event.getEntity(), event.getItemStack(), UseItemTrigger.Target.ENTITY);
            }
        }

        @SubscribeEvent
        public static void onPlayerRightClickItem(PlayerInteractEvent.RightClickItem event) {
            if (event.getEntity() instanceof ServerPlayer) {
                LibTriggers.USE_ITEM.trigger((ServerPlayer) event.getEntity(), event.getItemStack(), UseItemTrigger.Target.ITEM);
            }
        }
    }
}
