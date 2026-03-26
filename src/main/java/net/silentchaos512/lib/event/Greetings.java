package net.silentchaos512.lib.event;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

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

    private final List<Function<Player, Optional<Component>>> messages = new ArrayList<>();

    private Greetings() {
        NeoForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);
    }

    /**
     * Add a message to display to the player on login. If the function returns {@code null}, no
     * message is displayed. Consider displaying your message only once per session or per day.
     *
     * @param message A function to create the message.
     * @since 3.0.6
     */
    public static void addMessage(Function<Player, Component> message) {
        INSTANCE.messages.add(player -> Optional.ofNullable(message.apply(player)));
    }

    private void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        messages.forEach(msg -> msg.apply(player).ifPresent(player::sendSystemMessage));
    }
}
