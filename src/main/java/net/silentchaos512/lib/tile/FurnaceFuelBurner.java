/*
 * Silent Lib -- FurnaceFuelBurner
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

package net.silentchaos512.lib.tile;

import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.ForgeEventFactory;
import net.silentchaos512.utils.EnumUtils;

@Deprecated
public class FurnaceFuelBurner implements IFuelBurner {
    private final BurnCondition burnCondition;
    @Getter private int timeRemaining;
    @Getter private int currentItemMaxTime;

    public FurnaceFuelBurner(BurnCondition burnCondition) {
        this.burnCondition = burnCondition;
    }

    @Override
    public boolean feedFuel(ItemStack stack) {
        int value = ForgeEventFactory.getItemBurnTime(stack, stack.getBurnTime());
        if (value > 0) {
            timeRemaining = currentItemMaxTime = value;
            return true;
        }
        return false;
    }

    @Override
    public boolean hasFuel() {
        return timeRemaining > 0;
    }

    @Override
    public boolean tickFuel(boolean isProcessing) {
        if (burnCondition.shouldTick(this, isProcessing)) {
            --timeRemaining;
            return true;
        }
        return false;
    }

    private static FurnaceFuelBurner readFromNBT(CompoundNBT tags) {
        BurnCondition condition = EnumUtils.byName(tags.getString("Condition"), BurnCondition.DEFAULT);

        FurnaceFuelBurner result = new FurnaceFuelBurner(condition);
        result.timeRemaining = tags.getInt("Time");
        result.currentItemMaxTime = tags.getInt("MaxTime");

        return result;
    }

    private static void writeToNBT(CompoundNBT tags, FurnaceFuelBurner burner) {
        if (burner.burnCondition != BurnCondition.DEFAULT) {
            tags.putString("Condition", burner.burnCondition.name());
        }
        tags.putInt("Time", burner.timeRemaining);
        tags.putInt("MaxTime", burner.currentItemMaxTime);
    }

    static {
        SyncVariable.Helper.registerSerializer(FurnaceFuelBurner.class,
                FurnaceFuelBurner::readFromNBT,
                FurnaceFuelBurner::writeToNBT);
    }

    public enum BurnCondition {
        STANDARD {
            @Override
            boolean shouldTick(FurnaceFuelBurner burner, boolean processing) {
                return burner.timeRemaining > 0;
            }
        },
        ONLY_WHEN_PROCESSING {
            @Override
            boolean shouldTick(FurnaceFuelBurner burner, boolean processing) {
                return processing;
            }
        },
        ALWAYS {
            @Override
            boolean shouldTick(FurnaceFuelBurner burner, boolean processing) {
                return true;
            }
        };

        abstract boolean shouldTick(FurnaceFuelBurner burner, boolean processing);

        private static final BurnCondition DEFAULT = STANDARD;
    }
}
