package net.silentchaos512.lib.util.resourcemanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.silentchaos512.lib.network.SyncResourcesPacket;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public abstract class ResourceManagerBase<T extends IReloadableResource<T>> implements IResourceManagerReloadListener {
    private final String modId;
    private final String dataPath;
    protected final Marker logMarker;
    protected final Logger logger;

    protected final Map<ResourceLocation, T> resources = new LinkedHashMap<>();
    protected final Map<ResourceLocation, IReloadableResourceSerializer<T>> serializers = new ConcurrentHashMap<>();

    public ResourceManagerBase(String modId, String dataPath, String logMarker, Logger logger) {
        this.modId = modId;
        this.dataPath = dataPath.endsWith("/") ? dataPath : dataPath + "/";
        this.logMarker = MarkerManager.getMarker(logMarker);
        this.logger = logger;

        MinecraftForge.EVENT_BUS.register(this);
    }

    public T deserialize(ResourceLocation id, JsonObject json) {
        String typeStr = JsonUtils.getString(json, "type", "");
        if (typeStr.isEmpty()) {
            throw new JsonParseException("Missing required property 'type' in resource " + id);
        } else {
            if (!typeStr.contains(":")) {
                typeStr = this.modId + ":" + typeStr;
            }
            ResourceLocation type = new ResourceLocation(typeStr);

            IReloadableResourceSerializer<T> serializer = this.serializers.get(type);
            if (serializer == null) {
                throw new JsonParseException("Invalid or unsupported resource type: " + type);
            }
            return serializer.read(id, json);
        }
    }

    public T deserialize(PacketBuffer buffer) {
        ResourceLocation id = buffer.readResourceLocation();
        ResourceLocation type = buffer.readResourceLocation();
        IReloadableResourceSerializer<T> serializer = this.serializers.get(type);
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown serializer type " + type);
        }
        return serializer.read(id, buffer);
    }

    public void serialize(PacketBuffer buffer, T obj) {
        buffer.writeResourceLocation(obj.getId());
        IReloadableResourceSerializer<T> serializer = obj.getSerializer();
        buffer.writeResourceLocation(serializer.getSerializerName());
        serializer.write(buffer, obj);
    }

    @Nullable
    public T get(ResourceLocation id) {
        return this.resources.get(id);
    }

    public Collection<T> getValues() {
        return this.resources.values();
    }

    //region Resource reloading

    protected Gson getGson() {
        return (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    }

    protected Collection<ResourceLocation> getAllResources(IResourceManager resourceManager) {
        return resourceManager.getAllResourceLocations(this.dataPath, s -> s.endsWith(".json"));
    }

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
        Gson gson = getGson();
        Collection<ResourceLocation> resourceLocations = getAllResources(resourceManager);
        if (resourceLocations.isEmpty()) return;

        this.resources.clear();

        for (ResourceLocation id : resourceLocations) {
            try (IResource iResource = resourceManager.getResource(id)) {
                int substringStart = this.dataPath.length();
                int substringEnd = id.getPath().length() - ".json".length();
                String path = id.getPath().substring(substringStart, substringEnd);
                ResourceLocation name = new ResourceLocation(id.getNamespace(), path);
                this.logger.debug(this.logMarker, "Found resource file '{}', reading as '{}'", id, name);

                JsonObject json = JsonUtils.fromJson(gson, IOUtils.toString(iResource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
                if (json == null) {
                    this.logger.error(this.logMarker, "Could not load resource '{}' as it's null or empty", name);
                } else {
                    T obj = this.deserialize(name, json);
                    this.addObject(name, obj);
                }
            } catch (IllegalArgumentException | JsonParseException ex) {
                this.logger.error(this.logMarker, "Parsing error loading '{}'", id);
                this.logger.catching(ex);
            } catch (IOException ex) {
                this.logger.error(this.logMarker, "Could not read file '{}'", id);
                this.logger.catching(ex);
            }
        }

        this.logger.info(this.logMarker, "Registered {} objects", this.resources.size());
    }

    protected void addObject(ResourceLocation id, T obj) {
        if (this.resources.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate object with ID " + id);
        } else {
            this.resources.put(id, obj);
        }
    }

    //endregion

    //region Networking

    protected abstract SimpleChannel getModChannel();

    protected abstract SyncResourcesPacket<T> getSyncPacket();

    private void sendResourcesToClient(EntityPlayerMP player) {
        Collection<T> objects = this.getValues();
        this.logger.info(this.logMarker, "Sending {} objects to {}", objects.size(), player.getScoreboardName());
        SyncResourcesPacket<T> packet = this.getSyncPacket();
        getModChannel().sendTo(packet, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    public void onSyncPacket(SyncResourcesPacket<T> packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            this.resources.clear();
            packet.getResources().forEach(t -> this.resources.put(t.getId(), t));
            this.logger.info("Read {} objects from server", this.resources.size());
        });
        context.get().setPacketHandled(true);
    }

    //endregion

    //region Events

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerJoinServer(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.getPlayer();
        if (!(player instanceof EntityPlayerMP)) return;

        EntityPlayerMP playerMP = (EntityPlayerMP) player;
        // FIXME: This is sent too late!
        sendResourcesToClient(playerMP);
    }

    //endregion
}
