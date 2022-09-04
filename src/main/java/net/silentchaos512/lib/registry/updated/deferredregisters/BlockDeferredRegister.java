package net.silentchaos512.lib.registry.updated.deferredregisters;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.lib.registry.updated.registryobjects.BlockRegistryObject;

import java.util.function.Supplier;

public class BlockDeferredRegister extends DeferredRegisterWrapper<Block> {
    public BlockDeferredRegister(String modid) {
        super(modid, ForgeRegistries.BLOCKS);
    }

    @Override
    public <U extends Block> BlockRegistryObject<U> register(String name, Supplier<U> supplier) {
        return new BlockRegistryObject<>(this, name, supplier);
    }
}
