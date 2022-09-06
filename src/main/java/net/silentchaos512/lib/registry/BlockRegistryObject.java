package net.silentchaos512.lib.registry;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.silentchaos512.lib.block.IBlockProvider;

import java.util.function.Supplier;

public class BlockRegistryObject<T extends Block> extends RegistryObjectWrapper<T> implements IBlockProvider {
    public BlockRegistryObject(DeferredRegister<Block> deferredRegister, String name, Supplier<T> blockSupplier) {
        this(deferredRegister.register(name, blockSupplier));
    }

    public BlockRegistryObject(RegistryObject<T> block) {
        super(block);
    }

    @Override
    public Block asBlock() {
        return registryObject.get();
    }
}
