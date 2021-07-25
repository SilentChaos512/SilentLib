package net.silentchaos512.lib.world.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.AbstractFlowerFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.Random;

// TODO: May need to rethink this since FlowersFeature has changed significantly
@Deprecated
public class PlantFeature extends AbstractFlowerFeature<NoneFeatureConfiguration> {
    private final BlockState plant;
    private final int tryCount;
    private final int maxCount;

    public PlantFeature(BlockState plant, int tryCount, int maxCount) {
        super(NoneFeatureConfiguration.CODEC);
        this.plant = plant;
        this.tryCount = tryCount;
        this.maxCount = maxCount;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        BlockState toPlace = getRandomFlower(ctx.random(), ctx.origin(), ctx.config());
        int placedCount = 0;

        // Same as super, but different number of iterations and a placement count cap
        for(int j = 0; j < this.tryCount && placedCount < this.maxCount; ++j) {
            BlockPos pos1 = ctx.origin().offset(
                    ctx.random().nextInt(8) - ctx.random().nextInt(8),
                    ctx.random().nextInt(4) - ctx.random().nextInt(4),
                    ctx.random().nextInt(8) - ctx.random().nextInt(8)
            );
            if (ctx.level().isEmptyBlock(pos1) && pos1.getY() < 255 && toPlace.canSurvive(ctx.level(), pos1)) {
                ctx.level().setBlock(pos1, toPlace, 2);
                ++placedCount;
            }
        }

        return placedCount > 0;
    }

    @Override
    public BlockState getRandomFlower(Random random, BlockPos pos, NoneFeatureConfiguration config) {
        return this.plant;
    }

    @Override
    public boolean isValid(LevelAccessor world, BlockPos pos, NoneFeatureConfiguration config) {
        return this.plant.equals(world.getBlockState(pos));
    }

    @Override
    public int getCount(NoneFeatureConfiguration config) {
        return this.maxCount;
    }

    @Override
    public BlockPos getPos(Random random, BlockPos pos, NoneFeatureConfiguration config) {
        return pos.offset(
                random.nextInt(8) - random.nextInt(8),
                random.nextInt(4) - random.nextInt(4),
                random.nextInt(8) - random.nextInt(8));
    }
}
