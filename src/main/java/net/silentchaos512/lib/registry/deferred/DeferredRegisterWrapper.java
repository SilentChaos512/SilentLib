package net.silentchaos512.lib.registry.deferred;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import java.util.stream.Stream;

public class DeferredRegisterWrapper<T> {
    protected final DeferredRegister<T> deferredRegister;

    public DeferredRegisterWrapper(String modid, IForgeRegistry<T> registry) {
        this.deferredRegister = DeferredRegister.create(registry, modid);
    }

    public void registerBus(IEventBus bus) {
        this.deferredRegister.register(bus);
    }

    public Stream<RegistryObject<T>> getEntries() {
        return this.deferredRegister.getEntries().stream();
    }
}
