package net.silentchaos512.lib.world.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class DimensionFilterConfig implements DecoratorConfiguration {
    public static final Codec<DimensionFilterConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("is_whitelist").forGetter(config -> config.isWhitelist),
                    Codec.STRING.listOf().fieldOf("list").forGetter(config ->
                            config.dimensions.stream()
                                    .map(rk -> rk.getRegistryName().toString())
                                    .collect(Collectors.toList()))
            ).apply(instance, (isWhitelist, strList) -> {
                Collection<ResourceKey<Level>> dims = strList.stream()
                        .map(str -> ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(str)))
                        .collect(Collectors.toList());
                return new DimensionFilterConfig(isWhitelist, dims);
            }));

    private final boolean isWhitelist;
    private final Collection<ResourceKey<Level>> dimensions = new ArrayList<>();

    public DimensionFilterConfig(boolean isWhitelist, Collection<ResourceKey<Level>> dimensions) {
        this.isWhitelist = isWhitelist;
        this.dimensions.addAll(dimensions);
    }

    @SafeVarargs
    public static DimensionFilterConfig whitelist(ResourceKey<Level>... dimensions) {
        return new DimensionFilterConfig(true, Arrays.asList(dimensions));
    }

    @SafeVarargs
    public static DimensionFilterConfig blacklist(ResourceKey<Level>... dimensions) {
        return new DimensionFilterConfig(false, Arrays.asList(dimensions));
    }

    public boolean matches(ResourceKey<Level> dimension) {
        return this.dimensions.contains(dimension) == this.isWhitelist;
    }
}
