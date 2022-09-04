package net.silentchaos512.lib.registry.updated.deferredregisters;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.silentchaos512.lib.registry.updated.registryobjects.UpdatedRegistryObjectWrapper;

import java.util.function.Supplier;

public abstract class DeferredRegisterWrapper<T> {
    public final DeferredRegister<T> instance;
    public final String modid;

    public DeferredRegisterWrapper(String modid, IForgeRegistry<T> registry) {
        this.modid = modid;
        this.instance = DeferredRegister.create(registry, modid);
    }

    public void registerBus(IEventBus eventBus) {
        this.instance.register(eventBus);
    }

    public abstract <U extends T> UpdatedRegistryObjectWrapper<?> register(String name, Supplier<U> supplier);
}
