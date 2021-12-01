package net.silentchaos512.lib.world.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.Collection;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DimensionFilterPlacement extends PlacementModifier {
    public static final Codec<DimensionFilterPlacement> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("is_whitelist").forGetter(f -> f.isWhitelist),
                    Codec.STRING.listOf().fieldOf("list").forGetter(f ->
                            f.levels.stream()
                                    .map(rk -> rk.getRegistryName().toString())
                                    .collect(Collectors.toList()))
            ).apply(instance, (isWhitelist, strList) -> {
                Collection<ResourceKey<Level>> levels = strList.stream()
                        .map(str -> ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(str)))
                        .collect(Collectors.toList());
                return new DimensionFilterPlacement(isWhitelist, levels);
            }));

    private final boolean isWhitelist;
    private final Collection<ResourceKey<Level>> levels;

    public DimensionFilterPlacement(boolean isWhitelist, Collection<ResourceKey<Level>> levels) {
        this.isWhitelist = isWhitelist;
        this.levels = levels;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext helper, Random rand, BlockPos pos) {
        if (this.levels.contains(helper.getLevel().getLevel().dimension()) == this.isWhitelist) {
            return Stream.of(pos);
        }
        return Stream.empty();
    }

    @Override
    public PlacementModifierType<DimensionFilterPlacement> type() {
        return LibPlacements.DIMENSION_FILTER.get();
    }
}
