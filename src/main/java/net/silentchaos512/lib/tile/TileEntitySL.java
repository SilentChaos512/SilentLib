package net.silentchaos512.lib.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * Basic TileEntity with SyncVariable support.
 *
 * @author Silent
 * @since 2.0.6
 */
public class TileEntitySL extends TileEntity {
    public final void sendUpdate() {
        if (world != null && !world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            markDirty();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tags) {
        super.readFromNBT(tags);
        readSyncVars(tags);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tags) {
        super.writeToNBT(tags);
        writeSyncVars(tags, SyncVariable.Type.WRITE);
        return tags;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tags = getUpdateTag();
        return new SPacketUpdateTileEntity(pos, 1, tags);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tags = super.getUpdateTag();
        writeSyncVars(tags, SyncVariable.Type.PACKET);
        return tags;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readSyncVars(packet.getNbtCompound());
    }

    protected void readSyncVars(NBTTagCompound tags) {
        SyncVariable.Helper.readSyncVars(this, tags);
    }

    protected NBTTagCompound writeSyncVars(NBTTagCompound tags, SyncVariable.Type syncType) {
        return SyncVariable.Helper.writeSyncVars(this, tags, syncType);
    }
}
