/*
 * forge-1.12.2-SilentLib_main
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

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.silentchaos512.lib.SilentLib;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Can schedule actions to run during {@link TickEvent.ServerTickEvent}, which is mainly useful for
 * handling packets.
 *
 * @since 2.3.12
 */
@Mod.EventBusSubscriber
public final class ServerTicks {
    private static final int QUEUE_OVERFLOW_LIMIT = 30;
    @SuppressWarnings("FieldMayBeFinal")
    private static volatile Queue<Runnable> scheduledActions = new ArrayDeque<>();

    private ServerTicks() {}

    public static void scheduleAction(Runnable action) {
        // In SSP, this is still considered client side, so we can't check the side?
        scheduledActions.add(action);

        if (scheduledActions.size() > QUEUE_OVERFLOW_LIMIT)
            SilentLib.logHelper.warn("Too many server tick actions queued! Currently at {} items.", scheduledActions.size());
    }

    @SubscribeEvent
    public static void serverTicks(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            runScheduledActions();
    }

    private static void runScheduledActions() {
        Runnable action = scheduledActions.poll();
        while (action != null) {
            action.run();
            action = scheduledActions.poll();
        }
    }
}
