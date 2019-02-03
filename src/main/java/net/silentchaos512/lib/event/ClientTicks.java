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
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.util.GameUtil;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Can schedule actions to run during {@link TickEvent.ClientTickEvent}, which is mainly useful for
 * handling packets. Also tracks some tick-related variables useful for rendering.
 *
 * @since 2.3.12
 */
@Mod.EventBusSubscriber(modid = SilentLib.MOD_ID, value = Side.CLIENT)
public final class ClientTicks {
    @Deprecated
    public static final ClientTicks INSTANCE = new ClientTicks();

    private static final int QUEUE_OVERFLOW_LIMIT = 200;

    @SuppressWarnings("FieldMayBeFinal")
    private static volatile Queue<Runnable> scheduledActions = new ArrayDeque<>();

    public static int ticksInGame = 0;
    public static float partialTicks = 0f;
    public static float deltaTicks = 0f;
    public static float totalTicks = 0f;

    private ClientTicks() {}

    public static void scheduleAction(Runnable action) {
        if (GameUtil.isClient())
            scheduledActions.add(action);
        else
            SilentLib.LOGGER.error("Tried to add client tick action on server side? {}", action);

        if (scheduledActions.size() >= QUEUE_OVERFLOW_LIMIT) {
            // Queue overflow?
            SilentLib.LOGGER.warn("Too many client tick actions queued! Currently at {} items. Would have added '{}'.",
                    scheduledActions.size(), action);
            SilentLib.LOGGER.catching(new IllegalStateException("ClientTicks queue overflow"));
            scheduledActions.clear();
        }
    }

    @SubscribeEvent
    public static void clientTickEnd(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        runScheduledActions();
        updateTickCounters();
    }

    @SubscribeEvent
    public static void renderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            partialTicks = event.renderTickTime;
    }

    private static void runScheduledActions() {
        Runnable action = scheduledActions.poll();
        while (action != null) {
            action.run();
            action = scheduledActions.poll();
        }
    }

    private static void updateTickCounters() {
        GuiScreen gui = Minecraft.getMinecraft().currentScreen;
        if (gui == null || !gui.doesGuiPauseGame()) {
            ++ticksInGame;
            partialTicks = 0;
        }

        float oldTotal = totalTicks;
        totalTicks = ticksInGame + partialTicks;
        deltaTicks = totalTicks - oldTotal;
    }
}
