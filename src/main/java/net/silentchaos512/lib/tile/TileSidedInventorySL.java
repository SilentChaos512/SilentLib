package net.silentchaos512.lib.tile;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;

public abstract class TileSidedInventorySL extends TileInventorySL implements ISidedInventory {
    public TileSidedInventorySL(TileEntityType<?> tileEntityType, int inventorySize) {
        super(tileEntityType, inventorySize);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
        return false;
    }
}
