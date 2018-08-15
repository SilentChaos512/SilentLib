package net.silentchaos512.lib.draft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.silentchaos512.lib.tile.SyncVariable;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PlayerDataHandler<Data extends PlayerDataHandler.PlayerData> {
    private final String nbtRootKey;
    private final Map<Integer, Data> playerData = new HashMap<>();
    private Data emptyData;

    protected PlayerDataHandler(ResourceLocation nbtRootKey) {
        this.nbtRootKey = nbtRootKey.toString().replace(':', '_');
        MinecraftForge.EVENT_BUS.register(new EventHandler(this));
    }

    public abstract <T extends PlayerDataHandler> T instance();

    protected abstract Data createData(@Nullable EntityPlayer player);

    public Data get(EntityPlayer player) {
        if (player == null || player instanceof FakePlayer) {
            if (emptyData == null)
                emptyData = createData(null);
            return emptyData;
        }

        int key = getKey(player);
        if (!playerData.containsKey(key)) {
            playerData.put(key, createData(player));
        }

        Data data = playerData.get(key);
        if (data != null && data.getPlayer() != player) {
            NBTTagCompound tags = new NBTTagCompound();
            data.writeToNBT(tags);
            playerData.remove(key);
            data = get(player);
            data.readFromNBT(tags);
        }

        return data;
    }

    private void cleanup() {
        List<Integer> toRemove = new ArrayList<>();

        for (int key : playerData.keySet()) {
            Data data = playerData.get(key);
            if (data != null && !data.isValid())
                toRemove.add(key);
        }

        for (int key : toRemove) playerData.remove(key);
    }

    private static int getKey(EntityPlayer player) {
        return player.hashCode() << 1 + (player.world.isRemote ? 1 : 0);
    }

    private NBTTagCompound getDataCompoundForPlayer(EntityPlayer player) {
        NBTTagCompound forgeData = player.getEntityData();
        if (!forgeData.hasKey(EntityPlayer.PERSISTED_NBT_TAG))
            forgeData.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());

        NBTTagCompound persistentData = forgeData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (!persistentData.hasKey(nbtRootKey))
            persistentData.setTag(nbtRootKey, new NBTTagCompound());

        return persistentData.getCompoundTag(nbtRootKey);
    }

    public abstract class PlayerData {
        private WeakReference<EntityPlayer> playerWR;
        protected final boolean client;

        protected PlayerData(EntityPlayer player) {
            playerWR = new WeakReference<>(player);
            client = player.world.isRemote;
            load();
        }

        @Nullable
        public final EntityPlayer getPlayer() {
            return playerWR.get();
        }

        public boolean isValid() {
            return playerWR != null;
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

    public static class EventHandler {
        private final PlayerDataHandler<?> handler;

        private EventHandler(PlayerDataHandler<?> handler) {
            this.handler = handler;
        }

        @SubscribeEvent
        public void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.END)
                handler.cleanup();
        }

        @SubscribeEvent
        public void onPlayerTick(LivingEvent.LivingUpdateEvent event) {
            if (event.getEntityLiving() instanceof EntityPlayer)
                handler.get((EntityPlayer) event.getEntityLiving()).tick();
        }

        @SubscribeEvent
        public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.player instanceof EntityPlayerMP) {
                EntityPlayerMP playerMP = (EntityPlayerMP) event.player;
                // TODO Send data sync message
            }
        }
    }
}
