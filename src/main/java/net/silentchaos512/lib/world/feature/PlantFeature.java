package net.silentchaos512.lib.world.feature;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.FlowersFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

// TODO: May need to rethink this since FlowersFeature has changed significantly
@Deprecated
public class PlantFeature extends FlowersFeature<NoFeatureConfig> {
    private final BlockState plant;
    private final int tryCount;
    private final int maxCount;

    public PlantFeature(BlockState plant, int tryCount, int maxCount) {
        super(NoFeatureConfig.CODEC);
        this.plant = plant;
        this.tryCount = tryCount;
        this.maxCount = maxCount;
    }

    @Override
    public boolean place(ISeedReader worldIn, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        BlockState toPlace = getRandomFlower(rand, pos, config);
        int placedCount = 0;

        // Same as super, but different number of iterations and a placement count cap
        for(int j = 0; j < this.tryCount && placedCount < this.maxCount; ++j) {
            BlockPos pos1 = pos.offset(
                    rand.nextInt(8) - rand.nextInt(8),
                    rand.nextInt(4) - rand.nextInt(4),
                    rand.nextInt(8) - rand.nextInt(8)
            );
            if (worldIn.isEmptyBlock(pos1) && pos1.getY() < 255 && toPlace.canSurvive(worldIn, pos1)) {
                worldIn.setBlock(pos1, toPlace, 2);
                ++placedCount;
            }
        }

        return placedCount > 0;
    }

    @Override
    public BlockState getRandomFlower(Random random, BlockPos pos, NoFeatureConfig config) {
        return this.plant;
    }

    @Override
    public boolean isValid(IWorld world, BlockPos pos, NoFeatureConfig config) {
        return this.plant.equals(world.getBlockState(pos));
    }

    @Override
    public int getCount(NoFeatureConfig config) {
        return this.maxCount;
    }

    @Override
    public BlockPos getPos(Random random, BlockPos pos, NoFeatureConfig config) {
        return pos.offset(
                random.nextInt(8) - random.nextInt(8),
                random.nextInt(4) - random.nextInt(4),
                random.nextInt(8) - random.nextInt(8));
    }
}
