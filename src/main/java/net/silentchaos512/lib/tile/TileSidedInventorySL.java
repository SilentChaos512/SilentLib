package net.silentchaos512.lib.tile;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

public abstract class TileSidedInventorySL extends TileInventorySL implements ISidedInventory {
    /**
     * @param tileEntityTypeIn The TileEntityType
     * @deprecated Use other constructor
     */
    @Deprecated
    public TileSidedInventorySL(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public TileSidedInventorySL(TileEntityType<?> tileEntityType, int inventorySize) {
        super(tileEntityType, inventorySize);
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable EnumFacing direction) {
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }
}
