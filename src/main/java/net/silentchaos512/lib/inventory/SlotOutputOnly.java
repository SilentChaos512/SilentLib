package net.silentchaos512.lib.inventory;

import javax.annotation.Nullable;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotOutputOnly extends Slot {
    public SlotOutputOnly(Container inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@Nullable ItemStack stack) {
        return false;
    }
}
