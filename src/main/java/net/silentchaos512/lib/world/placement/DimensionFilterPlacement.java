package net.silentchaos512.lib.world.placement;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

import java.util.Random;
import java.util.stream.Stream;

public class DimensionFilterPlacement extends FeatureDecorator<DimensionFilterConfig> {
    public DimensionFilterPlacement(Codec<DimensionFilterConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecorationContext helper, Random rand, DimensionFilterConfig config, BlockPos pos) {
        // AT'd visibility of level
        if (config.matches(helper.level.getLevel().dimension())) {
            return Stream.of(pos);
        }
        return Stream.empty();
    }
}
