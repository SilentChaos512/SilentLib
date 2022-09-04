package net.silentchaos512.lib.registry.updated.registryobjects;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.silentchaos512.lib.registry.RegistryObjectWrapper;
import net.silentchaos512.lib.registry.updated.deferredregisters.DeferredRegisterWrapper;

import java.util.function.Supplier;

public abstract class UpdatedRegistryObjectWrapper<T> extends RegistryObjectWrapper<T> {
    protected final ResourceLocation registryName;

    public UpdatedRegistryObjectWrapper(DeferredRegisterWrapper<T> deferredRegister, String name, Supplier<? extends T> supplier) {
        this(deferredRegister.instance, deferredRegister.modid, name, supplier);
    }

    public UpdatedRegistryObjectWrapper(DeferredRegister<T> deferredRegister, String modid, String name, Supplier<? extends T> supplier) {
        super(deferredRegister.register(name, supplier));
        this.registryName = new ResourceLocation(modid, name);
    }

    public ResourceLocation getRegistryName() {
        return this.registryName;
    }

    public String getRegistryPath() {
        return this.registryName.getPath();
    }
}
