package net.silentchaos512.lib.tile;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.activation.UnsupportedDataTypeException;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.silentchaos512.lib.collection.ItemStackList;
import net.silentchaos512.lib.util.StackHelper;

/**
 * Basic TileEntity with SyncVariable support.
 *
 * @author Silent
 * @since 2.0.6
 *
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

    // Try to read from NBT for fields marked with SyncVariable.
    for (Field field : getClass().getDeclaredFields()) {
      for (Annotation annotation : field.getDeclaredAnnotations()) {
        if (annotation instanceof SyncVariable) {
          SyncVariable sync = (SyncVariable) annotation;

          try {
            // Set fields accessible if necessary.
            if (!field.isAccessible())
              field.setAccessible(true);
            String name = sync.name();

            if (field.getType() == int.class)
              field.setInt(this, tags.getInteger(name));
            else if (field.getType() == float.class)
              field.setFloat(this, tags.getFloat(name));
            else if (field.getType() == String.class)
              field.set(this, tags.getString(name));
            else if (field.getType() == boolean.class)
              field.setBoolean(this, tags.getBoolean(name));
            else if (field.getType() == double.class)
              field.setDouble(this, tags.getDouble(name));
            else if (field.getType() == long.class)
              field.setLong(this, tags.getLong(name));
            else if (field.getType() == short.class)
              field.setShort(this, tags.getShort(name));
            else if (field.getType() == byte.class)
              field.setByte(this, tags.getByte(name));
            else
              throw new UnsupportedDataTypeException(
                  "Don't know how to read type " + field.getType() + " from NBT!");
          } catch (IllegalAccessException | UnsupportedDataTypeException ex) {
            ex.printStackTrace();
          }
        }
      }
    }
  }

  protected NBTTagCompound writeSyncVars(NBTTagCompound tags, SyncVariable.Type syncType) {

    // Try to write to NBT for fields marked with SyncVariable.
    for (Field field : getClass().getDeclaredFields()) {
      for (Annotation annotation : field.getDeclaredAnnotations()) {
        if (annotation instanceof SyncVariable) {
          SyncVariable sync = (SyncVariable) annotation;

          // Does variable allow writing in this case?
          if (syncType == SyncVariable.Type.WRITE && sync.onWrite()
              || syncType == SyncVariable.Type.PACKET && sync.onPacket()) {
            try {
              // Set fields accessible if necessary.
              if (!field.isAccessible())
                field.setAccessible(true);
              String name = sync.name();

              if (field.getType() == int.class)
                tags.setInteger(name, field.getInt(this));
              else if (field.getType() == float.class)
                tags.setFloat(name, field.getFloat(this));
              else if (field.getType() == String.class)
                tags.setString(name, (String) field.get(this));
              else if (field.getType() == boolean.class)
                tags.setBoolean(name, field.getBoolean(this));
              else if (field.getType() == double.class)
                tags.setDouble(name, field.getDouble(this));
              else if (field.getType() == long.class)
                tags.setLong(name, field.getLong(this));
              else if (field.getType() == short.class)
                tags.setShort(name, field.getShort(this));
              else if (field.getType() == byte.class)
                tags.setByte(name, field.getByte(this));
              else
                throw new UnsupportedDataTypeException(
                    "Don't know how to write type " + field.getType() + " to NBT!");
            } catch (IllegalAccessException | UnsupportedDataTypeException ex) {
              ex.printStackTrace();
            }
          }
        }
      }
    }

    return tags;
  }
}
