/*
 * SilentLib - DimPos
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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * A {@link BlockPos} which also stores a dimension ID. This extends BlockPos as of 4.0.10, so it
 * has many of the same methods.
 */
public final class DimPos extends BlockPos {
    /**
     * Origin (0, 0, 0) in dimension 0
     */
    public static final DimPos ZERO = new DimPos(0, 0, 0, 0);

    private final int dimension;

    public static DimPos of(BlockPos pos, int dimension) {
        return new DimPos(pos, dimension);
    }

    public static DimPos of(int x, int y, int z, int dimension) {
        return new DimPos(x, y, z, dimension);
    }

    private DimPos(BlockPos pos, int dimension) {
        this(pos.getX(), pos.getY(), pos.getZ(), dimension);
    }

    private DimPos(int x, int y, int z, int dimension) {
        super(x, y, z);
        this.dimension = dimension;
    }

    public int getDimension() {
        return this.dimension;
    }

    public static DimPos read(NBTTagCompound tags) {
        return DimPos.of(
                tags.getInt("posX"),
                tags.getInt("posY"),
                tags.getInt("posZ"),
                tags.getInt("dim"));
    }

    public void write(NBTTagCompound tags) {
        tags.putInt("posX", this.getX());
        tags.putInt("posY", this.getY());
        tags.putInt("posZ", this.getZ());
        tags.putInt("dim", dimension);
    }

    /**
     * Converts to a BlockPos
     *
     * @return A BlockPos with the same coordinates
     * @deprecated DimPos now extends BlockPos, so conversion is unnecessary
     */
    @Deprecated
    public BlockPos getPos() {
        return this;
    }

    /**
     * Offset the DimPos in the given direction by the given distance.
     * <p>
     * This is called by the {@link #offset(EnumFacing, int)} override. This is a convenience method
     * to avoid casting.
     *
     * @param facing The direction to offset
     * @param n      The distance
     * @return A new DimPos with offset coordinates.
     * @since 4.0.10
     */
    public DimPos offsetDimPos(EnumFacing facing, int n) {
        if (n == 0) {
            return this;
        }
        return new DimPos(this.getX() + facing.getXOffset() * n, this.getY() + facing.getYOffset() * n, this.getZ() + facing.getZOffset() * n, this.dimension);
    }

    @Override
    public BlockPos offset(EnumFacing facing, int n) {
        return offsetDimPos(facing, n);
    }

    @Override
    public String toString() {
        return String.format("(%d, %d, %d) in dimension %d", this.getX(), this.getY(), this.getZ(), dimension);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DimPos)) {
            return false;
        }
        DimPos pos = (DimPos) other;
        return this.dimension == pos.dimension && super.equals(other);
    }
}
