package net.silentchaos512.lib.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

/**
 * Basic TileEntity with SyncVariable support.
 *
 * @author Silent
 * @since 2.0.6
 */
public class TileEntitySL extends TileEntity {
    public TileEntitySL(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public final void sendUpdate() {
        if (world != null && !world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            markDirty();
        }
    }

    @Override
    public void read(NBTTagCompound tags) {
        super.read(tags);
        readSyncVars(tags);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tags) {
        super.write(tags);
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

    private void readSyncVars(NBTTagCompound tags) {
        SyncVariable.Helper.readSyncVars(this, tags);
    }

    private NBTTagCompound writeSyncVars(NBTTagCompound tags, SyncVariable.Type syncType) {
        return SyncVariable.Helper.writeSyncVars(this, tags, syncType);
    }
}
