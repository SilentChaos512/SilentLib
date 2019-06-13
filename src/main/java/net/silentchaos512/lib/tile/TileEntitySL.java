package net.silentchaos512.lib.tile;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

/**
 * Basic TileEntity with SyncVariable support.
 *
 * @author Silent
 * @since 2.0.6
 */
@Deprecated
public class TileEntitySL extends TileEntity {
    public TileEntitySL(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public final void sendUpdate() {
        if (world != null && !world.isRemote) {
            BlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            markDirty();
        }
    }

    @Override
    public void read(CompoundNBT tags) {
        super.read(tags);
        readSyncVars(tags);
    }

    @Override
    public CompoundNBT write(CompoundNBT tags) {
        super.write(tags);
        writeSyncVars(tags, SyncVariable.Type.WRITE);
        return tags;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT tags = getUpdateTag();
        return new SUpdateTileEntityPacket(pos, 1, tags);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tags = super.getUpdateTag();
        writeSyncVars(tags, SyncVariable.Type.PACKET);
        return tags;
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        readSyncVars(packet.getNbtCompound());
    }

    private void readSyncVars(CompoundNBT tags) {
        SyncVariable.Helper.readSyncVars(this, tags);
    }

    private CompoundNBT writeSyncVars(CompoundNBT tags, SyncVariable.Type syncType) {
        return SyncVariable.Helper.writeSyncVars(this, tags, syncType);
    }
}
