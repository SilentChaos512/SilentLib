package net.silentchaos512.lib.registry.updated.registryobjects;

import net.minecraft.world.item.Item;
import net.silentchaos512.lib.registry.updated.deferredregisters.DeferredRegisterWrapper;

import java.util.function.Supplier;

public class ItemRegistryObject<T extends Item> extends UpdatedRegistryObjectWrapper<Item> {
    public ItemRegistryObject(DeferredRegisterWrapper<Item> deferredRegister, String name, Supplier<T> supplier) {
        super(deferredRegister, name, supplier);
    }
}
