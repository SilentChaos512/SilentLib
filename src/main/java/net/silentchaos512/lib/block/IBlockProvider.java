package net.silentchaos512.lib.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;

/**
 * Extension of {@link IItemProvider}, intended for block enums.
 */
public interface IBlockProvider extends IItemProvider {
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
        return asBlock().getDefaultState();
    }

    @Override
    default Item asItem() {
        return asBlock().asItem();
    }
}
