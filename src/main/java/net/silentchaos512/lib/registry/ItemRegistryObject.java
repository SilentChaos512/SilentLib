package net.silentchaos512.lib.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;
@Deprecated(forRemoval = true)
public class ItemRegistryObject<T extends Item> extends RegistryObjectWrapper<T> implements ItemLike {
    public ItemRegistryObject(RegistryObject<T> item) {
        super(item);
    }

    @Override
    public Item asItem() {
        return registryObject.get();
    }
}
