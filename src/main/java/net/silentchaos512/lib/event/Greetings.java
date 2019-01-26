/*
 * Silent Lib -- Greetings
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

package net.silentchaos512.lib.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Can be used to send messages to the player's chat log when they log in. Good for notifying the
 * player when something has gone horribly wrong.
 *
 * @since 2.3.17
 */
public final class Greetings {
    private static final Greetings INSTANCE = new Greetings();

    private final List<Function<EntityPlayer, Optional<ITextComponent>>> messages = new ArrayList<>();

    private Greetings() {
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);
    }

    /**
     * Add a message to display to the player on login. If the function returns {@code null}, no
     * message is displayed. Consider displaying your message only once per session or per day.
     *
     * @param message A function to create the message. Using {@link net.minecraft.util.text.TextComponentTranslation}
     *                may be ideal.
     * @since 3.0.6
     */
    public static void addMessage(Function<EntityPlayer, ITextComponent> message) {
        INSTANCE.messages.add(player -> Optional.ofNullable(message.apply(player)));
    }

    private void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player == null) return;
        messages.forEach(msg -> msg.apply(event.player).ifPresent(event.player::sendMessage));
    }
}
