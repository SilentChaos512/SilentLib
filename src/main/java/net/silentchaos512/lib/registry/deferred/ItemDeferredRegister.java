package net.silentchaos512.lib.registry.deferred;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.lib.registry.ItemRegistryObject;

import java.util.function.Supplier;

public class ItemDeferredRegister extends DeferredRegisterWrapper<Item> {
    public final CreativeModeTab defaultTab;

    public ItemDeferredRegister(String modid, CreativeModeTab defaultTab) {
        super(modid, ForgeRegistries.ITEMS);
        this.defaultTab = defaultTab;
    }

    public ItemRegistryObject<Item> register(String name) {
        return new ItemRegistryObject<>(super.deferredRegister, name, () -> new Item(new Item.Properties().tab(this.defaultTab)));
    }

    public <T extends Item> ItemRegistryObject<T> register(String name, Supplier<T> itemSupplier) {
        return new ItemRegistryObject<>(super.deferredRegister, name, itemSupplier);
    }
}
