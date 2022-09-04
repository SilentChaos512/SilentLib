package net.silentchaos512.lib.registry.updated.deferredregisters;

import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.lib.registry.updated.registryobjects.FluidTypeRegistryObject;

import java.util.function.Supplier;

public class FluidTypeDeferredRegister extends DeferredRegisterWrapper<FluidType> {
    public FluidTypeDeferredRegister(String modid) {
        super(modid, ForgeRegistries.FLUID_TYPES.get());
    }

    @Override
    public <U extends FluidType> FluidTypeRegistryObject<U> register(String name, Supplier<U> supplier) {
        return new FluidTypeRegistryObject<>(this, name, supplier);
    }
}
