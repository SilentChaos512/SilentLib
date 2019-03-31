package net.silentchaos512.lib.world.feature;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.AbstractFlowersFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

public class PlantFeature extends AbstractFlowersFeature {
    private final IBlockState plant;
    private final int tryCount;
    private final int maxCount;

    public PlantFeature(IBlockState plant, int tryCount, int maxCount) {
        this.plant = plant;
        this.tryCount = tryCount;
        this.maxCount = maxCount;
    }

    @Override
    public boolean place(IWorld worldIn, IChunkGenerator<? extends IChunkGenSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        IBlockState toPlace = getRandomFlower(rand, pos);
        int placedCount = 0;

        // Same as super, but different number of iterations and a placement count cap
        for(int j = 0; j < this.tryCount && placedCount < this.maxCount; ++j) {
            BlockPos pos1 = pos.add(
                    rand.nextInt(8) - rand.nextInt(8),
                    rand.nextInt(4) - rand.nextInt(4),
                    rand.nextInt(8) - rand.nextInt(8)
            );
            if (worldIn.isAirBlock(pos1) && pos1.getY() < 255 && toPlace.isValidPosition(worldIn, pos1)) {
                worldIn.setBlockState(pos1, toPlace, 2);
                ++placedCount;
            }
        }

        return placedCount > 0;
    }

    @Override
    public IBlockState getRandomFlower(Random rand, BlockPos pos) {
        return this.plant;
    }
}
