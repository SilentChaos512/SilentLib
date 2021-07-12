package net.silentchaos512.lib.world.placement;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.Placement;

import java.util.Random;
import java.util.stream.Stream;

public class DimensionFilterPlacement extends Placement<DimensionFilterConfig> {
    public DimensionFilterPlacement(Codec<DimensionFilterConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldDecoratingHelper helper, Random rand, DimensionFilterConfig config, BlockPos pos) {
        // AT'd visibility of level
        if (config.matches(helper.level.getLevel().dimension())) {
            return Stream.of(pos);
        }
        return Stream.empty();
    }
}
