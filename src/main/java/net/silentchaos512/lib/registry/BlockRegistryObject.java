package net.silentchaos512.lib.registry;

import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.silentchaos512.lib.block.IBlockProvider;

public class BlockRegistryObject<T extends Block> extends RegistryObjectWrapper<T> implements IBlockProvider {
    public BlockRegistryObject(RegistryObject<T> block) {
        super(block);
    }

    @Override
    public Block asBlock() {
        return registryObject.get();
    }
}
