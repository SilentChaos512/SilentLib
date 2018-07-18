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

package net.silentchaos512.lib.block;

import net.minecraft.block.BlockOre;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * A simple ore block that allows setting basic quantity/XP amounts without the need to override any
 * methods. The {@link #quantityDroppedWithBonus(int, Random)} method clamps the value to [0,64] and
 * calls a new method, {@link #bonusAmount(int, Random)}. Overriding bonusAmount should be easier.
 */
public class BlockOreSL extends BlockOre {

    private final Item droppedItem;
    private final int quantityMin, quantityMax;
    private final int xpMin, xpMax;

    public BlockOreSL(Item droppedItem, int harvestLevel, int quantityMin, int quantityMax, int xpMin, int xpMax) {
        this.droppedItem = droppedItem;
        this.quantityMin = quantityMin;
        this.quantityMax = quantityMax;
        this.xpMin = xpMin;
        this.xpMax = xpMax;

        setHardness(3f);
        setResistance(15f);
        setSoundType(SoundType.STONE);
        setHarvestLevel("pickaxe", harvestLevel);
    }

    @Nonnull
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return droppedItem;
    }

    @Override
    public int quantityDropped(Random random) {
        return quantityMin + random.nextInt(quantityMax - quantityMin + 1);
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        int quantity = quantityDropped(random);
        if (fortune > 0 && getItemDropped(getDefaultState(), random, fortune) != Item.getItemFromBlock(this)) {
            float bonus = bonusAmount(fortune, random);
            if (bonus < 0) bonus = 0;
            quantity = Math.round(quantity * (bonus + 1));
        }
        return MathHelper.clamp(quantity, 0, 64);
    }

    public float bonusAmount(int fortune, Random random) {
        return random.nextInt(fortune + 2) - 1;
    }

    @Override
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        Item item = getItemDropped(world.getBlockState(pos), RANDOM, fortune);
        return item != Item.getItemFromBlock(this) ? xpMin + RANDOM.nextInt(xpMax - xpMin + 1) : 0;
    }
}
