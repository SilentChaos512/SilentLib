package net.silentchaos512.lib.registry.updated.registryobjects;

import net.minecraft.world.level.block.Block;
import net.silentchaos512.lib.registry.updated.deferredregisters.BlockDeferredRegister;

import java.util.function.Supplier;

public class BlockRegistryObject<T extends Block> extends UpdatedRegistryObjectWrapper<Block> {
    public BlockRegistryObject(BlockDeferredRegister deferredRegister, String name, Supplier<T> supplier) {
        super(deferredRegister, name, supplier);
    }
}
