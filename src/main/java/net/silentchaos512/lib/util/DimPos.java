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

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;

import javax.annotation.Nullable;

/**
 * Basically a BlockPos with a dimension coordinate.
 * <p>
 * I tried to extend BlockPos here, but that caused some strange issues with Silent's Gems'
 * teleporters. They would apparently fail to link, but would work correctly after reloading the
 * world. No idea why this happens, but extending BlockPos seems to be the cause.
 */
public final class DimPos {
    /**
     * Origin (0, 0, 0) in dimension 0
     */
    public static final DimPos ZERO = new DimPos(0, 0, 0, 0);

    private final int posX;
    private final int posY;
    private final int posZ;
    private final int dimension;

    //region Static factory methods

    public static DimPos of(BlockPos pos, DimensionType dimension) {
        return new DimPos(pos, dimension.getId());
    }

    public static DimPos of(BlockPos pos, int dimension) {
        return new DimPos(pos, dimension);
    }

    public static DimPos of(int x, int y, int z, int dimension) {
        return new DimPos(x, y, z, dimension);
    }

    public static DimPos of(Entity entity) {
        return new DimPos(entity.getPosition(), entity.dimension.getId());
    }

    //endregion

    private DimPos(BlockPos pos, int dimension) {
        this(pos.getX(), pos.getY(), pos.getZ(), dimension);
    }

    private DimPos(int x, int y, int z, int dimension) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.dimension = dimension;
    }

    public int getX() {
        return this.posX;
    }

    public int getY() {
        return this.posY;
    }

    public int getZ() {
        return this.posZ;
    }

    public int getDimension() {
        return this.dimension;
    }

    @Nullable
    public DimensionType getDimensionType() {
        return DimensionType.getById(this.dimension);
    }

    public static DimPos read(CompoundNBT tags) {
        return DimPos.of(
                tags.getInt("posX"),
                tags.getInt("posY"),
                tags.getInt("posZ"),
                tags.getInt("dim"));
    }

    public void write(CompoundNBT tags) {
        tags.putInt("posX", this.posX);
        tags.putInt("posY", this.posY);
        tags.putInt("posZ", this.posZ);
        tags.putInt("dim", dimension);
    }

    /**
     * Converts to a BlockPos
     *
     * @return A BlockPos with the same coordinates
     */
    public BlockPos getPos() {
        return new BlockPos(posX, posY, posZ);
    }

    public Vec3d getPosCentered(double yOffset) {
        return new Vec3d(posX + 0.5, posY + yOffset, posZ + 0.5);
    }

    /**
     * Offset the DimPos in the given direction by the given distance.
     *
     * @param facing The direction to offset
     * @param n      The distance
     * @return A new DimPos with offset coordinates.
     * @since 4.0.10
     */
    public DimPos offset(Direction facing, int n) {
        if (n == 0) {
            return this;
        }
        return new DimPos(
                this.posX + facing.getXOffset() * n,
                this.posY + facing.getYOffset() * n,
                this.posZ + facing.getZOffset() * n,
                this.dimension);
    }

    @Override
    public String toString() {
        return String.format("(%d, %d, %d, d%d)", this.posX, this.posY, this.posZ, dimension);
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
        return pos.dimension == dimension && pos.posX == posX && pos.posY == posY && pos.posZ == posZ;
    }

    @Override
    public int hashCode() {
        return 31 * (31 * (31 * posX + posY) + posZ) + dimension;
    }
}
