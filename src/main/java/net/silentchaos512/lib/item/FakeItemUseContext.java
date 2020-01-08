package net.silentchaos512.lib.item;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.math.BlockRayTraceResult;

/**
 * Extension of {@link ItemUseContext} to work around the needed constructor being private. This is
 * useful for simulating the player using an item without needing to place the item in the player's
 * hand.
 *
 * @since 4.1.0
 */
public class FakeItemUseContext extends ItemUseContext {
    public FakeItemUseContext(ItemUseContext original, ItemStack fakeItem) {
        super(original.getWorld(), original.getPlayer(), original.getHand(), fakeItem,
                new BlockRayTraceResult(original.getHitVec(), original.getFace(), original.getPos(), original.isInside()));
    }
}
