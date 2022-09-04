package net.silentchaos512.lib.registry.updated.registryobjects;

import net.minecraft.world.level.material.Fluid;
import net.silentchaos512.lib.registry.updated.deferredregisters.DeferredRegisterWrapper;

import java.util.function.Supplier;

public class FluidRegistryObject<T extends Fluid> extends UpdatedRegistryObjectWrapper<Fluid> {
    public FluidRegistryObject(DeferredRegisterWrapper<Fluid> deferredRegister, String name, Supplier<T> supplier) {
        super(deferredRegister, name, supplier);
    }
}
