package net.silentchaos512.lib.world.placement;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.lib.SilentLib;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = SilentLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class LibPlacements {
    public static final Supplier<PlacementModifierType<DimensionFilterPlacement>> DIMENSION_FILTER =
            Suppliers.memoize(() -> register(SilentLib.getId("dimension_filter"), DimensionFilterPlacement.CODEC));

    //need this for typing issues
    private static <P extends PlacementModifier> PlacementModifierType<P> register(ResourceLocation loc, Codec<P> codec) {
        return Registry.register(Registry.PLACEMENT_MODIFIERS, loc, () -> codec);
    }

    private LibPlacements() {}
}
