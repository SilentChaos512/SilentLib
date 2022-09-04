package net.silentchaos512.lib.registry.updated.registryobjects;

import net.minecraftforge.fluids.FluidType;
import net.silentchaos512.lib.registry.updated.deferredregisters.DeferredRegisterWrapper;

import java.util.function.Supplier;

public class FluidTypeRegistryObject<T extends FluidType> extends UpdatedRegistryObjectWrapper<FluidType> {
    public FluidTypeRegistryObject(DeferredRegisterWrapper<FluidType> deferredRegister, String name, Supplier<T> supplier) {
        super(deferredRegister, name, supplier);
    }
}
