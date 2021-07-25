package net.silentchaos512.lib.registry;

import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.fml.RegistryObject;

public class ItemRegistryObject<T extends Item> extends RegistryObjectWrapper<T> implements IItemProvider {
    public ItemRegistryObject(RegistryObject<T> item) {
        super(item);
    }

    @Override
    public Item asItem() {
        return registryObject.get();
    }
}
