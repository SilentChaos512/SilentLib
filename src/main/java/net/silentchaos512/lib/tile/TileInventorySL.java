package net.silentchaos512.lib.tile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.silentchaos512.lib.util.MCMathUtils;

public abstract class TileInventorySL extends TileEntitySL implements IInventory {
    protected NonNullList<ItemStack> inventory;

    public TileInventorySL(TileEntityType<?> tileEntityType, int inventorySize) {
        super(tileEntityType);
        inventory = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        if (index < 0 || index >= inventory.size())
            return ItemStack.EMPTY;
        return inventory.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(inventory, index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(inventory, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventory.set(index, stack);

        if (stack.getCount() > getInventoryStackLimit())
            stack.setCount(getInventoryStackLimit());
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return world != null && world.getTileEntity(pos) == this && MCMathUtils.distanceSq(player, pos) <= 64.0;
    }

    @SuppressWarnings("NoopMethodInAbstractClass")
    @Override
    public void openInventory(PlayerEntity player) {
    }

    @SuppressWarnings("NoopMethodInAbstractClass")
    @Override
    public void closeInventory(PlayerEntity player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    @Override
    public void read(CompoundNBT tags) {
        super.read(tags);
        inventory = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tags, inventory);
    }

    @Override
    public CompoundNBT write(CompoundNBT tags) {
        super.write(tags);
        ItemStackHelper.saveAllItems(tags, inventory);
        return tags;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT tags = getUpdateTag();
        ItemStackHelper.saveAllItems(tags, inventory);
        return new SUpdateTileEntityPacket(pos, 1, tags);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        super.onDataPacket(net, packet);
        ItemStackHelper.loadAllItems(packet.getNbtCompound(), inventory);
    }
}
