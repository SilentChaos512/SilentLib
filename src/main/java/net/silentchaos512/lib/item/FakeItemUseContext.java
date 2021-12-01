package net.silentchaos512.lib.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Extension of {@link UseOnContext} to work around the needed constructor being private. This is
 * useful for simulating the player using an item without needing to place the item in the player's
 * hand.
 *
 * @since 4.1.0
 */
public class FakeItemUseContext extends UseOnContext {
    public FakeItemUseContext(UseOnContext original, ItemStack fakeItem) {
        super(original.getLevel(), original.getPlayer(), original.getHand(), fakeItem,
                new BlockHitResult(original.getClickLocation(), original.getClickedFace(), original.getClickedPos(), original.isInside()));
    }
}
