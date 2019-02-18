package net.silentchaos512.lib.config;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Deprecated
public final class ConfigValueOreGen {
    private DoubleValue veinCount;
    private DoubleValue veinSize;
    private IntValue minY;
    private IntValue maxY;
    private IntValue dimension;
    private ConfigValue<List<? extends Integer>> dimensionBlacklist;

    private ConfigValueOreGen() { }

    public static ConfigValueOreGen define(ForgeConfigSpec.Builder config, String path, String comment, Builder defaults) {
        config.comment(comment);
        config.push(path);

        ConfigValueOreGen result = new ConfigValueOreGen();

        result.veinCount = config
                .comment("Number of veins per chunk on average")
                .defineInRange("veinCount", defaults.veinCount, 0, 1000);
        result.veinSize = config
                .comment("The size of each ore vein")
                .defineInRange("veinSize", defaults.veinSize, 0, 1000);

        result.minY = config
                .comment("The lowest level the ore will spawn at")
                .defineInRange("minY", defaults.minY, 0, 255);
        result.maxY = config
                .comment("The highest level the ore will spawn at")
                .defineInRange("maxY", defaults.maxY, result.minY.get() + 1, 255);

        result.dimension = config
                .defineInRange("dimension", defaults.dimension, Integer.MIN_VALUE, Integer.MAX_VALUE);
        result.dimensionBlacklist = config
                .defineList("dimensionBlacklist", defaults.dimensionBlacklist, o -> o instanceof Integer);

        config.pop();

        return result;
    }

    public boolean canSpawnIn(int dimension) {
        // Dimension 0 ores can spawn in any dimension, others must match exactly
        if (dimension != 0) {
            return dimension == this.dimension.get();
        }

        for (int i : dimensionBlacklist.get()) {
            if (i == dimension) {
                return false;
            }
        }
        return true;
    }

    public boolean isEnabled() {
        return veinCount.get() > 0 && veinSize.get() > 0;
    }

    public BlockPos selectPos(Random random, int chunkCenterX, int chunkCenterZ) {
        return new BlockPos(
                chunkCenterX + random.nextInt(16),
                minY.get() + random.nextInt(maxY.get() - minY.get()),
                chunkCenterZ + random.nextInt(16)
        );
    }

    public int veinCount(Random random) {
        double diff = veinCount.get() - veinCount.get().intValue();
        return veinCount.get().intValue() + (random.nextFloat() < diff ? 1 : 0);
    }

    public static class Builder {
        private double veinCount = 8;
        private double veinSize = 8;
        private int minY = 0;
        private int maxY = 128;
        private int dimension = 0;
        private final List<Integer> dimensionBlacklist = new ArrayList<>();

        public static Builder create() {
            return new Builder();
        }

        public Builder veinCountSize(double count, double size) {
            this.veinCount = count;
            this.veinSize = size;
            return this;
        }

        public Builder height(int min, int max) {
            if (max <= min) {
                throw new IllegalArgumentException("max must be greater than min");
            }

            this.minY = min;
            this.maxY = max;
            return this;
        }

        public Builder dimension(int dim) {
            this.dimension = dim;
            return this;
        }

        public Builder dimension(int dim, int... blacklist) {
            this.dimension = dim;

            this.dimensionBlacklist.clear();
            for (int i : blacklist) {
                this.dimensionBlacklist.add(i);
            }

            return this;
        }
    }
}
