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

/**
 * Some common advancement triggers for mods to use.
 *
 * @since 2.3.15
 */
public class LibTriggers {
    /**
     * Represents an item being used (right-clicking a block, entity, or nothing). No need to
     * trigger this yourself, it's handled in {@link net.silentchaos512.lib.event.SilentLibCommonEvents}.
     */
    public static final UseItemTrigger USE_ITEM = CriteriaTriggers.register(new UseItemTrigger());
    /**
     * Can be used if you just need a trigger with a single int. Requires a ResourceLocation to to
     * distinguish between different uses.
     */
    public static final GenericIntTrigger GENERIC_INT = CriteriaTriggers.register(new GenericIntTrigger());

    public static void init() {
        // NO-OP, just needed to initialize static fields in time.
    }
}
