/*
 * SilentLib - DimensionalPosition
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

public class DimensionalPosition {
    public static final DimensionalPosition ZERO = new DimensionalPosition(0, 0, 0, 0);

    public final int x, y, z, dim;

    public DimensionalPosition(BlockPos pos, int dim) {
        this(pos.getX(), pos.getY(), pos.getZ(), dim);
    }

    public DimensionalPosition(int x, int y, int z, int dim) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = dim;
    }

    public static DimensionalPosition readFromNBT(NBTTagCompound tags) {
        return new DimensionalPosition(tags.getInteger("posX"), tags.getInteger("posY"),
                tags.getInteger("posZ"), tags.getInteger("dim"));
    }

    public void writeToNBT(NBTTagCompound tags) {
        tags.setInteger("posX", x);
        tags.setInteger("posY", y);
        tags.setInteger("posZ", z);
        tags.setInteger("dim", dim);
    }

    public BlockPos toBlockPos() {
        return new BlockPos(x, y, z);
    }

    @Override
    public String toString() {
        return String.format("(%d, %d, %d) in dimension %d", x, y, z, dim);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof DimensionalPosition)) {
            return false;
        }
        DimensionalPosition pos = (DimensionalPosition) other;
        return x == pos.x && y == pos.y && z == pos.z && dim == pos.dim;
    }
}
