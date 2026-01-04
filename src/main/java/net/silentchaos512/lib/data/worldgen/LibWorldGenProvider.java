package net.silentchaos512.lib.data.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Aids in generating world generation data files. Create an instance of your subclass, then call {@link #createRunner()}
 * and register the resulting object with your data providers.
 *
 * @since 11.0.0
 */
public abstract class LibWorldGenProvider {
    public static final RuleTest REPLACE_STONE = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
    public static final RuleTest REPLACE_DEEPSLATE = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
    public static final RuleTest REPLACE_NETHERRACK = new TagMatchTest(Tags.Blocks.NETHERRACKS);
    public static final RuleTest REPLACE_END_STONE = new TagMatchTest(Tags.Blocks.END_STONES);

    final PackOutput packOutput;
    final CompletableFuture<HolderLookup.Provider> registries;
    final String modId;

    public LibWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String modId) {
        this.packOutput = output;
        this.registries = registries;
        this.modId = modId;
    }

    public final Runner<? extends LibWorldGenProvider> createRunner() {
        return new Runner<>(this);
    }

    protected RegistrySetBuilder createRegistrySetBuilder() {
        var builder = new RegistrySetBuilder();
        builder.add(Registries.CONFIGURED_FEATURE, this::registerConfiguredFeatures);
        builder.add(Registries.PLACED_FEATURE, this::registerPlacedFeatures);
        builder.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, this::registerBiomeModifiers);
        return builder;
    }

    public abstract void registerConfiguredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> ctx);

    public abstract void registerPlacedFeatures(BootstrapContext<PlacedFeature> ctx);

    public abstract void registerBiomeModifiers(BootstrapContext<BiomeModifier> ctx);

    //region registration helpers

    protected void register(BootstrapContext<ConfiguredFeature<?, ?>> ctx, Identifier key, ConfiguredFeature<?, ?> configuredFeature) {
        ctx.register(configuredFeatureKey(key), configuredFeature);
    }

    protected void register(BootstrapContext<PlacedFeature> ctx, Identifier key, Function<Holder<ConfiguredFeature<?, ?>>, PlacedFeature> placedFeature) {
        ctx.register(placedFeatureKey(key), placedFeature.apply(holderFeature(ctx, configuredFeatureKey(key))));
    }

    protected void register(BootstrapContext<PlacedFeature> ctx, Identifier key, PlacedFeature placedFeature) {
        ctx.register(placedFeatureKey(key), placedFeature);
    }

    protected void registerBiomeAddFeature(
            BootstrapContext<BiomeModifier> ctx,
            Identifier key,
            TagKey<Biome> biomes,
            GenerationStep.Decoration decorationStep,
            List<Identifier> placedFeatureKeys
    ) {
        ctx.register(
                biomeModifierKey(key),
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        ctx.lookup(Registries.BIOME).getOrThrow(biomes),
                        HolderSet.direct(
                                placedFeatureKeys.stream()
                                        .map(placedFeatureKey -> holderPlaced(ctx, placedFeatureKey))
                                        .toList()
                        ),
                        decorationStep
                )
        );
    }

    //endregion

    //region ResourceKey helpers

    public static ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureKey(Identifier name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, name);
    }

    public static ResourceKey<PlacedFeature> placedFeatureKey(Identifier name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, name);
    }

    public static ResourceKey<BiomeModifier> biomeModifierKey(Identifier name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, name);
    }

    //endregion

    //region Holder helpers

    public static Holder<ConfiguredFeature<?, ?>> holderFeature(BootstrapContext<PlacedFeature> ctx, ResourceKey<ConfiguredFeature<?, ?>> location) {
        return ctx.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(location);
    }

    public static Holder<PlacedFeature> holderPlaced(BootstrapContext<BiomeModifier> ctx, Identifier location) {
        return ctx.lookup(Registries.PLACED_FEATURE).getOrThrow(placedFeatureKey(location));
    }

    //endregion

    //region ConfiguredFeature helpers

    public static ConfiguredFeature<?, ?> simpleBlockFeature(Supplier<? extends Block> block) {
        return new ConfiguredFeature<>(
                Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(block.get()))
        );
    }

    //endregion

    //region PlacedFeature helpers

    public static PlacedFeature placeOnSurfaceWithRarity(Holder<ConfiguredFeature<?, ?>> configuredFeature, int rarityOnceEvery) {
        if (rarityOnceEvery < 1) {
            throw new IllegalArgumentException("rarity must be greater than zero");
        }
        return new PlacedFeature(configuredFeature, List.of(
                RarityFilter.onAverageOnceEvery(rarityOnceEvery),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                BiomeFilter.biome()
        ));
    }

    //endregion

    public static class Runner<T extends LibWorldGenProvider> extends DatapackBuiltinEntriesProvider {
        public Runner(T provider) {
            super(provider.packOutput, provider.registries, provider.createRegistrySetBuilder(), Collections.singleton(provider.modId));
        }
    }
}
