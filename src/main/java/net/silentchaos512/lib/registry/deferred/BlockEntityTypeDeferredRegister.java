package net.silentchaos512.lib.registry.deferred;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class BlockEntityTypeDeferredRegister extends DeferredRegisterWrapper<BlockEntityType<?>> {
    public BlockEntityTypeDeferredRegister(String modid) {
        super(modid, ForgeRegistries.BLOCK_ENTITY_TYPES);
    }

    @SafeVarargs
    public final <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> blockEntitySupplier, Supplier<Block>... validBlocks) {
        //noinspection ConstantConditions
        return this.deferredRegister.register(name, () -> BlockEntityType.Builder.of(blockEntitySupplier, Stream.of(validBlocks).map(Supplier::get).toArray(Block[]::new)).build(null));
    }
}
