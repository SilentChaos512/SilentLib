package net.silentchaos512.lib.registry.updated.deferredregisters;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.lib.registry.updated.registryobjects.BlockEntityTypeRegistryObject;

import java.util.List;
import java.util.function.Supplier;

public class BlockEntityDeferredRegister extends DeferredRegisterWrapper<BlockEntityType<?>> {
    public BlockEntityDeferredRegister(String modid) {
        super(modid, ForgeRegistries.BLOCK_ENTITY_TYPES);
    }

    @Override
    @Deprecated
    public <U extends BlockEntityType<?>> BlockEntityTypeRegistryObject<? extends BlockEntity> register(String name, Supplier<U> supplier) {
        throw new IllegalStateException("Please use another implementation for registration");
    }

    public <BE extends BlockEntity> BlockEntityTypeRegistryObject<BE> register(String name, BlockEntityType.BlockEntitySupplier<BE> beSupplier, List<Supplier<Block>> validBlocksSupplier) {
        return new BlockEntityTypeRegistryObject<BE>(this, name, beSupplier, validBlocksSupplier);
    }
}
