package net.silentchaos512.lib.event;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.silentchaos512.lib.SilentLib;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Can schedule actions to run during {@link ServerTickEvent}, which is mainly useful for
 * handling packets.
 *
 * @since 2.3.12
 */
public final class ServerTicks {
    private static final ServerTicks INSTANCE = new ServerTicks();
    private static final int QUEUE_OVERFLOW_LIMIT = 200;

    @SuppressWarnings("FieldMayBeFinal")
    private volatile Queue<Runnable> scheduledActions = new ConcurrentLinkedDeque<>();

    private ServerTicks() {
        NeoForge.EVENT_BUS.addListener(this::serverTicks);
    }

    public static void scheduleAction(Runnable action) {
        // In SSP, this is still considered client side, so we can't check the side?
        INSTANCE.scheduledActions.add(action);

        if (INSTANCE.scheduledActions.size() > QUEUE_OVERFLOW_LIMIT) {
            SilentLib.LOGGER.warn("Too many server tick actions queued! Currently at {} items. Would have added '{}'.",
                    INSTANCE.scheduledActions.size(), action);
            SilentLib.LOGGER.error("ServerTicks queue overflow", new IllegalStateException("ServerTicks queue overflow"));
            INSTANCE.scheduledActions.clear();
        }
    }

    private void serverTicks(ServerTickEvent.Pre event) {
        runScheduledActions();
    }

    private void runScheduledActions() {
        Runnable action = scheduledActions.poll();
        while (action != null) {
            action.run();
            action = scheduledActions.poll();
        }
    }
}
