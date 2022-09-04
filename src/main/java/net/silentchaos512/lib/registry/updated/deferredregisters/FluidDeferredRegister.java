package net.silentchaos512.lib.registry.updated.deferredregisters;

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.lib.registry.updated.registryobjects.FluidRegistryObject;

import java.util.function.Supplier;

public class FluidDeferredRegister extends DeferredRegisterWrapper<Fluid> {
    public FluidDeferredRegister(String modid) {
        super(modid, ForgeRegistries.FLUIDS);
    }

    @Override
    public <U extends Fluid> FluidRegistryObject<U> register(String name, Supplier<U> supplier) {
        return new FluidRegistryObject<>(this, name, supplier);
    }
}
