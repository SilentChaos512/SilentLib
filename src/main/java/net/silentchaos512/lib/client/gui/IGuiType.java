/*
 * Silent Lib -- IGuiType
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

package net.silentchaos512.lib.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

// TODO: Test me!
public interface IGuiType {
    /**
     * Get a unique ID for the GUI. {@link Enum#ordinal()} should be fine in most cases.
     */
    int getID();

    /**
     * Get the mod object (required to open GUIs)
     */
    Object getMod();

    /**
     * Get the {@link TileEntity} class, or null if not applicable.
     */
    @Nullable
    Class<? extends TileEntity> getTileEntityClass();

    @Nullable
    Container getContainer(@Nullable TileEntity tile, EntityPlayer player, int magicVar);

    @Nullable
    GuiScreen getGuiScreen(@Nullable TileEntity tile, EntityPlayer player, int magicVar);

    /**
     * Open the GUI for the block at the given position. This version is mainly intended for tile
     * entities, but arbitrary data could be packed into the BlockPos.
     *
     * @param player The player opening the object
     * @param world  The world the object is in
     * @param pos    The "position" of the object
     */
    default void open(EntityPlayer player, World world, BlockPos pos) {
        player.openGui(getMod(), getID(), world, pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Open an item's GUI, with the item being in the given hand. The hand is passed in as the
     * x-coordinate parameter.
     * <br>
     * Also see: {@link #handFromInt(int)} and {@link #intFromHand(EnumHand)}
     *
     * @param player The player opening the object
     * @param world  The world the object is in
     * @param hand   The hand the object is in
     */
    default void open(EntityPlayer player, World world, EnumHand hand) {
        open(player, world, intFromHand(hand));
    }

    default void open(EntityPlayer player, World world, int subtype) {
        player.openGui(getMod(), getID(), world, subtype, 0, 0);
    }

    default boolean tileEntityMatches(@Nullable TileEntity tile) {
        Class<? extends TileEntity> tileClass = getTileEntityClass();
        return tileClass == null || tileClass.isInstance(tile);
    }

    static EnumHand handFromInt(int k) {
        return k == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
    }

    static int intFromHand(EnumHand hand) {
        return hand == EnumHand.MAIN_HAND ? 0 : 1;
    }
}
