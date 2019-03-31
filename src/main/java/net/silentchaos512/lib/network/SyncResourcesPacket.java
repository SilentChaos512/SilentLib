package net.silentchaos512.lib.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.silentchaos512.lib.util.resourcemanager.IReloadableResource;
import net.silentchaos512.lib.util.resourcemanager.ResourceManagerBase;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class SyncResourcesPacket<T extends IReloadableResource<T>> {
    private List<T> list;

    public SyncResourcesPacket() {
    }

    public SyncResourcesPacket(Collection<T> objects) {
        this.list = new ArrayList<>(objects);
    }

    public void fromBytes(PacketBuffer buffer, Function<PacketBuffer, T> reader) {
        this.list = new ArrayList<>();
        int count = buffer.readVarInt();

        for (int i = 0; i < count; ++i) {
            this.list.add(reader.apply(buffer));
        }
    }

    public void toBytes(PacketBuffer buffer, BiConsumer<PacketBuffer, T> writer) {
        buffer.writeVarInt(this.list.size());
        this.list.forEach(obj -> writer.accept(buffer, obj));
    }

    public List<T> getResources() {
        return Collections.unmodifiableList(this.list);
    }

    /**
     * Register a resource-syncing packet. You MUST extend SyncResourcesPacket and you MUST have a
     * sync packet for each resource manager. The packet class must have a constructor with no
     * parameters.
     *
     * @param channel           Your mod's channel
     * @param id                The packet ID
     * @param markAsLoginPacket Mark as a login packet. WARNING: This does not currently work!
     *                          Creating new login packets makes it impossible to login to a server,
     *                          as it is impossible to reply to them correctly! TODO: Investigate
     * @param resourceManager   The resource manager
     * @param packetClass       The packet class
     * @param <T>               The reloadable resource type
     * @param <M>               The resource manager of T
     * @param <P>               The sync packet class
     */
    public static <T extends IReloadableResource<T>, M extends ResourceManagerBase<T>, P extends SyncResourcesPacket<T>> void register(
            SimpleChannel channel,
            int id,
            boolean markAsLoginPacket,
            M resourceManager,
            Class<P> packetClass
    ) {
        SimpleChannel.MessageBuilder<P> builder = channel.messageBuilder(packetClass, id);
        builder.decoder(buffer -> {
            P packet = constructPacket(packetClass);
            packet.fromBytes(buffer, resourceManager::deserialize);
            return packet;
        });
        builder.encoder((packet, buffer) -> packet.toBytes(buffer, (buf, obj) ->
                resourceManager.serialize((PacketBuffer) buf, (T) obj)));
        builder.consumer(resourceManager::onSyncPacket);
        if (markAsLoginPacket) {
            builder.markAsLoginPacket();
        }
        builder.add();
    }

    private static <T extends IReloadableResource<T>, P extends SyncResourcesPacket<T>> P constructPacket(Class<P> packetClass) {
        try {
            return packetClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("SyncResourcesPacket must have a public constructor with no parameters", e);
        }
    }
}
