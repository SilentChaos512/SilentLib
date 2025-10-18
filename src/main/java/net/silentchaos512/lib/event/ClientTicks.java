package net.silentchaos512.lib.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.silentchaos512.lib.SilentLib;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Can schedule actions to run during {@link ClientTickEvent}, which is mainly useful for
 * handling packets. Also tracks some tick-related variables useful for rendering.
 *
 * @since 2.3.12
 */
public final class ClientTicks {
    private static final ClientTicks INSTANCE = new ClientTicks();
    private static final int QUEUE_OVERFLOW_LIMIT = 200;

    @SuppressWarnings("FieldMayBeFinal")
    private volatile Queue<Runnable> scheduledActions = new ConcurrentLinkedDeque<>();

    public int ticksInGame = 0;
    public float partialTicks = 0f;
    public float deltaTicks = 0f;
    public float totalTicks = 0f;

    private ClientTicks() {
        NeoForge.EVENT_BUS.addListener(this::clientTickEnd);
        NeoForge.EVENT_BUS.addListener(this::renderTick);
    }

    public static void scheduleAction(Runnable action) {
        if (FMLEnvironment.getDist() == Dist.CLIENT)
            INSTANCE.scheduledActions.add(action);
        else
            SilentLib.LOGGER.error("Tried to add client tick action on server side? {}", action);

        if (INSTANCE.scheduledActions.size() >= QUEUE_OVERFLOW_LIMIT) {
            // Queue overflow?
            SilentLib.LOGGER.warn("Too many client tick actions queued! Currently at {} items. Would have added '{}'.",
                    INSTANCE.scheduledActions.size(), action);
            SilentLib.LOGGER.error("ClientTicks queue overflow", new IllegalStateException("ClientTicks queue overflow"));
            INSTANCE.scheduledActions.clear();
        }
    }

    private void clientTickEnd(ClientTickEvent.Post event) {
        runScheduledActions();
        updateTickCounters();
    }

    private void renderTick(RenderFrameEvent.Pre event) {
        partialTicks = event.getPartialTick().getGameTimeDeltaTicks();
    }

    private void runScheduledActions() {
        Runnable action = scheduledActions.poll();
        while (action != null) {
            action.run();
            action = scheduledActions.poll();
        }
    }

    private void updateTickCounters() {
        Screen gui = Minecraft.getInstance().screen;
        if (gui == null || !gui.isPauseScreen()) {
            ++ticksInGame;
            partialTicks = 0;
        }

        float oldTotal = totalTicks;
        totalTicks = ticksInGame + partialTicks;
        deltaTicks = totalTicks - oldTotal;
    }

    public static int ticksInGame() {
        return INSTANCE.ticksInGame;
    }

    public static float partialTicks() {
        return INSTANCE.partialTicks;
    }

    public static float deltaTicks() {
        return INSTANCE.deltaTicks;
    }

    public static float totalTicks() {
        return INSTANCE.totalTicks;
    }
}
