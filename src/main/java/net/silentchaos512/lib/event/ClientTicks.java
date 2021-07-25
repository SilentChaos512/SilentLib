/*
 * Silent Lib
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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.util.GameUtil;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Can schedule actions to run during {@link TickEvent.ClientTickEvent}, which is mainly useful for
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
        MinecraftForge.EVENT_BUS.addListener(this::clientTickEnd);
        MinecraftForge.EVENT_BUS.addListener(this::renderTick);
    }

    public static void scheduleAction(Runnable action) {
        if (GameUtil.isClient())
            INSTANCE.scheduledActions.add(action);
        else
            SilentLib.LOGGER.error("Tried to add client tick action on server side? {}", action);

        if (INSTANCE.scheduledActions.size() >= QUEUE_OVERFLOW_LIMIT) {
            // Queue overflow?
            SilentLib.LOGGER.warn("Too many client tick actions queued! Currently at {} items. Would have added '{}'.",
                    INSTANCE.scheduledActions.size(), action);
            SilentLib.LOGGER.catching(new IllegalStateException("ClientTicks queue overflow"));
            INSTANCE.scheduledActions.clear();
        }
    }

    private void clientTickEnd(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        runScheduledActions();
        updateTickCounters();
    }

    private void renderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            partialTicks = event.renderTickTime;
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
