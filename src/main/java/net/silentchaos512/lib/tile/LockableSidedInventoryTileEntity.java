package net.silentchaos512.lib.tile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.silentchaos512.lib.util.MCMathUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class LockableSidedInventoryTileEntity extends LockableTileEntity implements ISidedInventory {
    protected NonNullList<ItemStack> items;
    private final LazyOptional<? extends IItemHandler>[] handlers =
            SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

    protected LockableSidedInventoryTileEntity(TileEntityType<?> typeIn, int inventorySize) {
        super(typeIn);
        this.items = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        if (index < 0 || index >= items.size()) {
            return ItemStack.EMPTY;
        }
        return items.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return ItemStackHelper.removeItem(items, index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ItemStackHelper.takeItem(items, index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        items.set(index, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return level != null && level.getBlockEntity(worldPosition) == this && MCMathUtils.distanceSq(player, worldPosition) <= 64;
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    @Override
    public void load(BlockState stateIn, CompoundNBT tags) {
        super.load(stateIn, tags);
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(tags, items);
    }

    @Override
    public CompoundNBT save(CompoundNBT tags) {
        super.save(tags);
        ItemStackHelper.saveAllItems(tags, items);
        return tags;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT tags = getUpdateTag();
        ItemStackHelper.saveAllItems(tags, items);
        return new SUpdateTileEntityPacket(worldPosition, 1, tags);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        super.onDataPacket(net, packet);
        ItemStackHelper.loadAllItems(packet.getTag(), items);
    }

    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (!this.remove && side != null && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == Direction.UP)
                return handlers[0].cast();
            if (side == Direction.DOWN)
                return handlers[1].cast();
            return handlers[2].cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        for (LazyOptional<? extends IItemHandler> handler : handlers) {
            handler.invalidate();
        }
    }
}
