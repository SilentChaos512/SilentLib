package net.silentchaos512.lib.world.placement;

import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.lib.SilentLib;

@Mod.EventBusSubscriber(modid = SilentLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class LibPlacements {
    public static final FeatureDecorator<DimensionFilterConfig> DIMENSION_FILTER = new DimensionFilterPlacement(DimensionFilterConfig.CODEC);

    private LibPlacements() {}

    @SubscribeEvent
    public static void register(RegistryEvent.Register<FeatureDecorator<?>> event) {
        event.getRegistry().register(DIMENSION_FILTER.setRegistryName(SilentLib.getId("dimension_filter")));
    }
}
