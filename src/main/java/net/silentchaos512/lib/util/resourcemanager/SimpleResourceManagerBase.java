package net.silentchaos512.lib.util.resourcemanager;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Logger;

/**
 * ResourceManagerBase, but modified to use only a single serializer.
 *
 * @param <T> Resource type
 */
public abstract class SimpleResourceManagerBase<T extends IReloadableResource<T>> extends ResourceManagerBase<T> {
    private final IReloadableResourceSerializer<T> serializer;

    public SimpleResourceManagerBase(String modId, String dataPath, String logMarker, Logger logger, IReloadableResourceSerializer<T> serializer) {
        super(modId, dataPath, logMarker, logger);
        this.serializer = serializer;
    }

    @Override
    public T deserialize(ResourceLocation id, JsonObject json) {
        return this.serializer.read(id, json);
    }

    @Override
    public T deserialize(PacketBuffer buffer) {
        ResourceLocation id = buffer.readResourceLocation();
        return this.serializer.read(id, buffer);
    }

    @Override
    public void serialize(PacketBuffer buffer, T obj) {
        buffer.writeResourceLocation(obj.getId());
        buffer.writeResourceLocation(this.serializer.getSerializerName());
        this.serializer.write(buffer, obj);
    }
}
