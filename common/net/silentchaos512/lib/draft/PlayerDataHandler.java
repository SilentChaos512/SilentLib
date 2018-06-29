package net.silentchaos512.lib.draft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.silentchaos512.lib.tile.SyncVariable;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public abstract class PlayerDataHandler {

  protected final String nbtRootKey;
  protected final Map<Integer, PlayerData> playerData = new HashMap<>();

  protected PlayerDataHandler(String nbtRootKey) {

    this.nbtRootKey = nbtRootKey;
  }

  public abstract <T extends PlayerDataHandler> T instance();

  public NBTTagCompound getDataCompoundForPlayer(EntityPlayer player) {
    throw new NotImplementedException();
  }

  public abstract class PlayerData {

    public WeakReference<EntityPlayer> playerWR;
    protected final boolean client;

    public PlayerData(EntityPlayer player) {
      playerWR = new WeakReference<>(player);
      client = player.world.isRemote;
      load();
    }

    public abstract void tick();

    protected void sendUpdateMessage() {
      if (!client) {
        EntityPlayer player = playerWR.get();
        // TODO: Send data sync message to client
      }
    }

    public final void save() {
      if (!client) {
        EntityPlayer player = playerWR.get();

        if (player != null) {
          NBTTagCompound tags = getDataCompoundForPlayer(player);
          writeToNBT(tags);
          SyncVariable.Helper.writeSyncVars(this, tags, SyncVariable.Type.WRITE);
        }
      }
    }

    public abstract void writeToNBT(NBTTagCompound tags);

    public final void load() {
      if (!client) {
        EntityPlayer player = playerWR.get();

        if (player != null) {
          NBTTagCompound tags = getDataCompoundForPlayer(player);
          readFromNBT(tags);
          SyncVariable.Helper.readSyncVars(this, tags);
        }
      }
    }

    public abstract void readFromNBT(NBTTagCompound tags);
  }
}
