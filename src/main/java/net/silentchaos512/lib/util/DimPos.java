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
import net.minecraft.util.math.BlockPos;

public final class DimPos {
    public static final DimPos ZERO = new DimPos(0, 0, 0, 0);

    private final int posX;
    private final int posY;
    private final int posZ;
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

    public static DimPos read(NBTTagCompound tags) {
        return DimPos.of(
                tags.getInt("posX"),
                tags.getInt("posY"),
                tags.getInt("posZ"),
                tags.getInt("dim"));
    }

    public void write(NBTTagCompound tags) {
        tags.putInt("posX", posX);
        tags.putInt("posY", posY);
        tags.putInt("posZ", posZ);
        tags.putInt("dim", dimension);
    }

    public BlockPos getPos() {
        return new BlockPos(posX, posY, posZ);
    }

    @Override
    public String toString() {
        return String.format("(%d, %d, %d) in dimension %d", posX, posY, posZ, dimension);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof DimPos)) {
            return false;
        }
        DimPos pos = (DimPos) other;
        return posX == pos.posX && posY == pos.posY && posZ == pos.posZ && dimension == pos.dimension;
    }
}
