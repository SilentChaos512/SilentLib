package net.silentchaos512.lib.block;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Extension of {@link IItemProvider}, intended for block enums.
 */
public interface IBlockProvider extends ItemLike {
    /**
     * Get the block this object represents.
     *
     * @return The block, which may be newly constructed
     */
    Block asBlock();

    /**
     * Shortcut for getting the default state of the block.
     *
     * @return Default block state
     */
    default BlockState asBlockState() {
        return asBlock().defaultBlockState();
    }

    @Override
    default Item asItem() {
        return asBlock().asItem();
    }
}
