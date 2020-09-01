package net.silentchaos512.lib.world.feature;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.FlowersFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.StructureManager;

import java.util.Random;

// TODO: May need to rethink this since FlowersFeature has changed significantly
public class PlantFeature extends FlowersFeature<NoFeatureConfig> {
    private final BlockState plant;
    private final int tryCount;
    private final int maxCount;

    public PlantFeature(BlockState plant, int tryCount, int maxCount) {
        super(NoFeatureConfig.field_236558_a_);
        this.plant = plant;
        this.tryCount = tryCount;
        this.maxCount = maxCount;
    }

    @Override
    public boolean func_241855_a(ISeedReader worldIn, ChunkGenerator chunkGenerator, Random rand, BlockPos pos, NoFeatureConfig config) {
        BlockState toPlace = getFlowerToPlace(rand, pos, config);
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
    public BlockState getFlowerToPlace(Random random, BlockPos pos, NoFeatureConfig config) {
        return this.plant;
    }

    @Override
    public boolean isValidPosition(IWorld world, BlockPos pos, NoFeatureConfig config) {
        return this.plant.equals(world.getBlockState(pos));
    }

    @Override
    public int getFlowerCount(NoFeatureConfig config) {
        return this.maxCount;
    }

    @Override
    public BlockPos getNearbyPos(Random random, BlockPos pos, NoFeatureConfig config) {
        return pos.add(
                random.nextInt(8) - random.nextInt(8),
                random.nextInt(4) - random.nextInt(4),
                random.nextInt(8) - random.nextInt(8));
    }
}
