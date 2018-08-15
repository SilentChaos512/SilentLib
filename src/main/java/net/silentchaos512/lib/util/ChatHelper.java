/*
 * SilentLib - ChatHelper
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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class ChatHelper {
    /**
     * Creates a {@link TextComponentString} and sends it to the player's chat log.
     * <em>Use with care:</em> if this is called on the server, the player may not receive text in
     * their local language.
     *
     * @param message The raw string passed to {@link TextComponentString}
     */
    public static void sendMessage(EntityPlayer player, String message) {
        player.sendMessage(new TextComponentString(message));
    }

    public static void sendMessage(EntityPlayer player, ITextComponent component) {
        player.sendMessage(component);
    }

    /**
     * Creates a {@link TextComponentString} and sends it to the player's action bar or chat log.
     * <em>Use with care:</em> if this is called on the server, the player may not receive text in
     * their local language.
     *
     * @param message   The raw string passed to {@link TextComponentString}
     * @param actionBar If true, sends to action bar, chat log otherwise
     */
    public static void sendStatusMessage(EntityPlayer player, String message, boolean actionBar) {
        player.sendStatusMessage(new TextComponentString(message), actionBar);
    }

    /**
     * Sends the {@link ITextComponent} to the player's action bar or chat log. Not recommended, use
     * {@link #translate(EntityPlayer, String, boolean)} instead.
     *
     * @param component The text component.
     * @param actionBar If true, sends to action bar, chat log otherwise
     */
    public static void sendStatusMessage(EntityPlayer player, ITextComponent component, boolean actionBar) {
        player.sendStatusMessage(component, actionBar);
    }

    /**
     * Creates a {@link TextComponentTranslation} and sends a message to the player's chat log.
     *
     * @param translationKey Translation key that the client will translate
     * @since 2.3.13
     */
    public static void translate(EntityPlayer player, String translationKey) {
        sendMessage(player, new TextComponentTranslation(translationKey));
    }

    /**
     * Creates a {@link TextComponentTranslation} and sends a message to the player's chat log.
     *
     * @param translationKey Translation key that the client will translate
     * @param args           Format arguments
     * @since 2.3.17
     */
    public static void translate(EntityPlayer player, String translationKey, Object... args) {
        sendMessage(player, new TextComponentTranslation(translationKey, args));
    }

    /**
     * Creates a {@link TextComponentTranslation} and sends a message to the player's action bar or
     * chat log.
     *
     * @param translationKey Translation key that the client will translate
     * @param actionBar      If true, sends to action bar, chat log otherwise
     * @since 2.3.13
     */
    public static void translateStatus(EntityPlayer player, String translationKey, boolean actionBar) {
        sendStatusMessage(player, new TextComponentTranslation(translationKey), actionBar);
    }

    /**
     * Creates a {@link TextComponentTranslation} and sends a message to the player's action bar or
     * chat log.
     *
     * @param translationKey Translation key that the client will translate
     * @param actionBar      If true, sends to action bar, chat log otherwise
     * @param args           Format arguments
     * @since 2.3.17
     */
    public static void translateStatus(EntityPlayer player, String translationKey, boolean actionBar, Object... args) {
        sendStatusMessage(player, new TextComponentTranslation(translationKey, args), actionBar);
    }
}
