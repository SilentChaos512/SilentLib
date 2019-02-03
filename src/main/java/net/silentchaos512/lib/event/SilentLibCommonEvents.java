/*
 * SilentLib - SilentLibCommonEvents
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

package net.silentchaos512.lib.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.advancements.LibTriggers;
import net.silentchaos512.lib.advancements.UseItemTrigger;
import net.silentchaos512.lib.item.ItemGuideBookSL;
import net.silentchaos512.lib.util.PlayerHelper;

import java.util.Objects;

/**
 * Silent Lib's common event handler. Do not call any functions of this class.
 *
 * @author SilentChaos512
 * @since 2.1.4
 */
public final class SilentLibCommonEvents {
    private static final String NBT_ROOT_GUIDE_BOOKS = "silentlib_guide_books";

    /**
     * Called when a player logs in. Used to give guide books to the player.
     */
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        NBTTagCompound guideData = PlayerHelper.getPersistedDataSubcompound(player, NBT_ROOT_GUIDE_BOOKS);

        int id = 0;
        ItemGuideBookSL item = ItemGuideBookSL.getBookById(id);
        while (item != null && item.giveBookOnFirstLogin) {
            ResourceLocation name = Objects.requireNonNull(item.getRegistryName());
            if (!guideData.getBoolean(name.toString())) {
                guideData.setBoolean(name.toString(), true);
                PlayerHelper.giveItem(player, new ItemStack(item));
                SilentLib.LOGGER.info("Player has been given guide book {}", name);
            }

            item = ItemGuideBookSL.getBookById(++id);
        }
    }

    @SubscribeEvent
    public void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntityPlayer() instanceof EntityPlayerMP) {
//            SilentLib.logHelper.info("Use {} on {}", event.getItemStack(), UseItemTrigger.Target.BLOCK);
            LibTriggers.USE_ITEM.trigger((EntityPlayerMP) event.getEntityPlayer(), event.getItemStack(), UseItemTrigger.Target.BLOCK);
        }
    }

    @SubscribeEvent
    public void onPlayerRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntityPlayer() instanceof EntityPlayerMP) {
//            SilentLib.logHelper.info("Use {} on {}", event.getItemStack(), UseItemTrigger.Target.ENTITY);
            LibTriggers.USE_ITEM.trigger((EntityPlayerMP) event.getEntityPlayer(), event.getItemStack(), UseItemTrigger.Target.ENTITY);
        }
    }

    @SubscribeEvent
    public void onPlayerRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getEntityPlayer() instanceof EntityPlayerMP) {
//            SilentLib.logHelper.info("Use {} on {}", event.getItemStack(), UseItemTrigger.Target.ITEM);
            LibTriggers.USE_ITEM.trigger((EntityPlayerMP) event.getEntityPlayer(), event.getItemStack(), UseItemTrigger.Target.ITEM);
        }
    }
}
