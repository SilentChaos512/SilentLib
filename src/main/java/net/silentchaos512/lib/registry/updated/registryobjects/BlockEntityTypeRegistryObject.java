package net.silentchaos512.lib.registry.updated.registryobjects;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.silentchaos512.lib.registry.updated.deferredregisters.DeferredRegisterWrapper;

import java.util.List;
import java.util.function.Supplier;

public class BlockEntityTypeRegistryObject<T extends BlockEntity> extends UpdatedRegistryObjectWrapper<BlockEntityType<?>> {
    public BlockEntityTypeRegistryObject(DeferredRegisterWrapper<BlockEntityType<?>> deferredRegister, String name, BlockEntityType.BlockEntitySupplier<T> blockEntitySupplier, List<Supplier<Block>> validBlocks) {
        //noinspection ConstantConditions
        this(deferredRegister, name, () -> BlockEntityType.Builder.of(blockEntitySupplier, validBlocks.stream().map(Supplier::get).toArray(Block[]::new)).build(null));
    }

    public BlockEntityTypeRegistryObject(DeferredRegisterWrapper<BlockEntityType<?>> deferredRegister, String name, Supplier<BlockEntityType<T>> supplier) {
        super(deferredRegister, name, supplier);
    }
}
