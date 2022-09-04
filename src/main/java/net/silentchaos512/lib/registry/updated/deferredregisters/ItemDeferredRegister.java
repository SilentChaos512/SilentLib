package net.silentchaos512.lib.registry.updated.deferredregisters;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.lib.registry.updated.registryobjects.ItemRegistryObject;

import java.util.function.Supplier;

public class ItemDeferredRegister extends DeferredRegisterWrapper<Item> {
    public ItemDeferredRegister(String modid) {
        super(modid, ForgeRegistries.ITEMS);
    }

    @Override
    public <U extends Item> ItemRegistryObject<U> register(String name, Supplier<U> supplier) {
        return new ItemRegistryObject<>(this, name, supplier);
    }
}
